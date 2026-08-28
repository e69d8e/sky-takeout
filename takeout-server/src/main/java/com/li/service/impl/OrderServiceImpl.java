package com.li.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.li.common.constant.JwtClaimsConstant;
import com.li.common.constant.MessageConstant;
import com.li.common.exception.OrderBusinessException;
import com.li.common.properties.JwtProperties;
import com.li.common.result.PageResult;
import com.li.common.utils.JwtUtil;
import com.li.common.utils.ThreadLocalUtil;
import com.li.common.utils.WebSocketUtil;
import com.li.mapper.AddressBookMapper;
import com.li.mapper.OrderMapper;
import com.li.mapper.ShppingCartMapper;
import com.li.pojo.dto.OrdersCancelDTO;
import com.li.pojo.dto.OrdersPaymentDTO;
import com.li.pojo.dto.OrdersRejectionDTO;
import com.li.pojo.dto.OrdersSubmitDTO;
import com.li.pojo.entity.AddressBook;
import com.li.pojo.entity.OrderDetail;
import com.li.pojo.entity.Orders;
import com.li.pojo.entity.ShoppingCart;
import com.li.pojo.vo.OrderStatisticsVO;
import com.li.pojo.vo.OrderSubmitVO;
import com.li.service.OrderService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ThreadLocalUtil threadLocal;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShppingCartMapper shppingCartMapper;
    @Autowired
    private WebSocketUtil webSocketUtil;

    // 下单
    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        // 获取当前登录用户id
        Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), threadLocal.get().toString());
        Long userId = claims.get(JwtClaimsConstant.USER_ID, Long.class);
        // 判断订单是否存在
        // 查询当前用户的购物车数据
        List<ShoppingCart> shoppingCarts = shppingCartMapper.list(userId);
        if (shoppingCarts == null) throw new OrderBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        // 判断地址是否存在
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId(), userId);
        if (addressBook == null) throw new OrderBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        // 向订单表插入数据
        // 计算总金额
        BigDecimal amount = shppingCartMapper.getAmount(userId);
        Orders orders = new Orders(); // 对象拷贝
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setAmount(amount);
        orders.setOrderTime(LocalDateTime.now());
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(System.currentTimeMillis())); // 订单号是时间戳
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());
        orderMapper.add(orders);
        // 向订单详情表插入数据
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (ShoppingCart shoppingCart : shoppingCarts) {
            OrderDetail orderDetail = OrderDetail.builder()
                    .orderId(orders.getId())
                    .name(shoppingCart.getName())
                    .dishId(shoppingCart.getDishId())
                    .setmealId(shoppingCart.getSetmealId())
                    .image(shoppingCart.getImage())
                    .dishFlavor(shoppingCart.getDishFlavor())
                    .amount(shoppingCart.getAmount())
                    .number(shoppingCart.getNumber())
                    .build();
            orderDetails.add(orderDetail);
        }
        orderMapper.addOrderDetail(orderDetails);
        // 清空购物车
        shppingCartMapper.clean(userId);
        // 发送消息
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orders.getId());
        map.put("type", 1);
        map.put("content", "订单号" + orders.getNumber());
        webSocketUtil.sendAllClient(JSON.toJSONString(map));

        // 返回订单数据
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();

    }

    // 支付
    @Override
    public LocalDateTime payment(OrdersPaymentDTO ordersPaymentDTO) {
        // 获取当前登录用户id
        Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), threadLocal.get().toString());
        Long userId = claims.get(JwtClaimsConstant.USER_ID, Long.class);
        // 更新订单
        Orders orders = orderMapper.getByNumber(ordersPaymentDTO.getOrderNumber(), userId);
        if (orders == null) throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        // 当订单状态为待付款时才进行支付
        if (!Objects.equals(orders.getStatus(), Orders.PENDING_PAYMENT)) throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        orders.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(15));
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(Orders.TO_BE_CONFIRMED);
        orders.setPayStatus(Orders.PAID);
        orders.setPayMethod(ordersPaymentDTO.getPayMethod());
        orderMapper.update(orders);
        // 返回订单预计送达时间
        return orders.getEstimatedDeliveryTime();
    }

    // 订单详情
    @Override
    public Orders details(Long id) {
        // 查询订单
        Orders orders = orderMapper.getById(id);
        if (orders == null) throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        // 查询订单明细
        List<OrderDetail> orderDetails = orderMapper.getByOrderId(id);
        orders.setOrderDetails(orderDetails);
        return orders;
    }

    // 取消订单
    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        // 根据订单id查找
        Orders orders = orderMapper.getById(ordersCancelDTO.getId());
        if (orders == null) throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        // 只用在待付款和待接单状态可以取消
        if (!Objects.equals(orders.getStatus(), Orders.TO_BE_CONFIRMED) &&
                !Objects.equals(orders.getStatus(), Orders.PENDING_PAYMENT)) throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        // 设置订单状态为已取消
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orderMapper.update(orders);
    }

    // 取消订单时，不填写原因
    @Override
    public void cancelNotReason(Long id) {
        // 根据订单id查找
        Orders orders = orderMapper.getById(id);
        if (orders == null) throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        // 只用在待付款和待接单状态可以取消
        if (!Objects.equals(orders.getStatus(), Orders.TO_BE_CONFIRMED) &&
                !Objects.equals(orders.getStatus(), Orders.PENDING_PAYMENT)) throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        // 设置订单状态为已取消
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    // 历史订单查询
    @Override
    public PageResult historyOrders(Integer page, Integer pageSize, Integer status) {
        // 获取当前登录用户id
        Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), threadLocal.get().toString());
        Long userId = claims.get(JwtClaimsConstant.USER_ID, Long.class);
        log.info("历史订单查询，页码：{}，页大小：{}，订单状态：{}", page, pageSize, status);
        PageHelper.startPage(page, pageSize);
        Page<Orders> p = (Page<Orders>) orderMapper.historyOrders(userId, status);
        if (p == null) throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        return new PageResult(p.getTotal(), p.getResult());
    }
    // 催单
    @Override
    public void reminder(Long id) {
        // 根据订单id查找
        Orders orders = orderMapper.getById(id);
        if (orders == null) throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        // 只用在待接单和待派送状态可以催单
        if (!Objects.equals(orders.getStatus(), Orders.TO_BE_CONFIRMED) &&
                !Objects.equals(orders.getStatus(), Orders.DELIVERY_IN_PROGRESS)) throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        Map<String, Object> map = new HashMap<>();
        map.put("type", 2);
        map.put("orderId", id);
        map.put("content", "订单号" + orders.getNumber());
        String message = JSON.toJSONString(map);
        webSocketUtil.sendAllClient(message);
    }

    // 每过一分钟将待付款时间超过了15分钟的订单置为已取消
    @Scheduled(cron = "0 * * * * ?")
    public void autoCancelOrder(){
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        orderMapper.updateStatus(Orders.PENDING_PAYMENT, time, Orders.CANCELLED, Orders.UN_PAID);
    }
    // 每天凌晨1点，将前一天所有处于派送中且已支付状态的订单置为已完成
    @Scheduled(cron = "0 0 1 * * ?")
    public void autoConfirm(){
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        orderMapper.updateStatus(Orders.DELIVERY_IN_PROGRESS, time, Orders.COMPLETED, Orders.PAID);
    }
    // 接单
    @Override
    public void confirm(Long id) {
        // 将待接单的订单设置状态为已接单，并且设置订单的预计送达时间为当前时间的15分钟后
        orderMapper.confirm(id, Orders.TO_BE_CONFIRMED, Orders.CONFIRMED, LocalDateTime.now().plusMinutes(15));
    }
    // 拒单
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        // 将待接单的订单设置状态为已取消，并且设置拒绝原因
        orderMapper.rejection(ordersRejectionDTO.getId(), ordersRejectionDTO.getRejectionReason(), Orders.TO_BE_CONFIRMED, Orders.CANCELLED);
    }
    // 派送
    @Override
    public void delivery(Long id) {
        orderMapper.delivery(id, Orders.CONFIRMED, Orders.DELIVERY_IN_PROGRESS);
    }
    // 完成
    @Override
    public void complete(Long id) {
        // 只用在派送中的订单可以完成
        orderMapper.complete(id, Orders.DELIVERY_IN_PROGRESS, Orders.COMPLETED);
    }

    // 各状态订单数量统计
    @Override
    public OrderStatisticsVO statistics() {
        Integer toBeConfirmed = orderMapper.statistics(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.statistics(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.statistics(Orders.DELIVERY_IN_PROGRESS);
        return new OrderStatisticsVO(toBeConfirmed == null ? 0 : toBeConfirmed,
                confirmed == null ? 0 : confirmed,
                deliveryInProgress == null ? 0 : deliveryInProgress);
    }
    // 条件分页搜索
    @Override
    public PageResult conditionSearch(LocalDate beginTime, LocalDate endTime, String number,
                                      Integer status, Integer page, Integer pageSize, String phone) {
        // 将时间转换
        LocalDateTime beg = null;
        if (beginTime != null) beg = LocalDateTime.of(beginTime, LocalTime.MIN);
        if (endTime == null) endTime = LocalDate.now();
        LocalDateTime end = LocalDateTime.of(endTime, LocalTime.MAX);

        PageHelper.startPage(page, pageSize);
        Page<Orders> p = (Page<Orders>) orderMapper.conditionSearch(beg, end, number, status, phone);
        return new PageResult(p.getTotal(), p.getResult());
    }
}
