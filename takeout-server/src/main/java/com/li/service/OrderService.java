package com.li.service;

import com.li.common.result.PageResult;
import com.li.pojo.dto.OrdersCancelDTO;
import com.li.pojo.dto.OrdersPaymentDTO;
import com.li.pojo.dto.OrdersRejectionDTO;
import com.li.pojo.dto.OrdersSubmitDTO;
import com.li.pojo.entity.Orders;
import com.li.pojo.vo.OrderStatisticsVO;
import com.li.pojo.vo.OrderSubmitVO;
import com.li.pojo.vo.OrderVO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    // 下单
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);
    // 支付
    LocalDateTime payment(OrdersPaymentDTO ordersPaymentDTO);
    // 订单详情
    Orders details(Long id);
    // 取消订单
    void cancel(OrdersCancelDTO ordersCancelDTO);
    // 取消订单(不填写原因)
    void cancelNotReason(Long id);
    // 历史订单查询
    PageResult historyOrders(Integer page, Integer pageSize, Integer status);
    // 催单
    void reminder(Long id);
    // 接单
    void confirm(Long id);
    // 拒单
    void rejection(OrdersRejectionDTO ordersRejectionDTO);
    // 派送
    void delivery(Long id);
    // 完成
    void complete(Long id);
    // 各状态订单数量统计
    OrderStatisticsVO statistics();
    // 条件分页搜索
    PageResult conditionSearch(LocalDate beginTime, LocalDate endTime, String number, Integer status, Integer page, Integer pageSize, String phone);
}
