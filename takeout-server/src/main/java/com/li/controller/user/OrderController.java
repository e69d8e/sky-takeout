package com.li.controller.user;

import com.li.common.result.PageResult;
import com.li.common.result.Result;
import com.li.pojo.dto.OrdersCancelDTO;
import com.li.pojo.dto.OrdersPaymentDTO;
import com.li.pojo.dto.OrdersSubmitDTO;
import com.li.pojo.entity.Orders;
import com.li.pojo.vo.OrderSubmitVO;
import com.li.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController("userOrder")
@RequestMapping("/user/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /*
    下单
     */
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        return Result.success(orderService.submit(ordersSubmitDTO));
    }
    /*
    订单支付
     */
    @PutMapping("/payment")
    public Result payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) {
        LocalDateTime time = orderService.payment(ordersPaymentDTO);
        return Result.success(time);
    }
    /*
    查询订单详情
     */
    @GetMapping("/orderDetail/{id}")
    public Result<Orders> details(@PathVariable Long id) {
        return Result.success(orderService.details(id));
    }
    /*
    取消订单
     */
    @PutMapping("/cancel")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        orderService.cancel(ordersCancelDTO);
        return Result.success();
    }
    /*
    无原因取消订单
     */
    @PutMapping("/cancel/{id}")
    public Result noReasonCancel(@PathVariable Long id) {
        orderService.cancelNotReason(id);
        return Result.success();
    }
    /*
    历史订单查询
     */
    @GetMapping("/historyOrders")
    public Result<PageResult> historyOrders(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, Integer status) {
        return Result.success(orderService.historyOrders(page, pageSize, status));
    }

    /*
    用户催单
     */
    @GetMapping("/reminder/{id}")
    public Result reminder(@PathVariable Long id) {
        orderService.reminder(id);
        return Result.success();
    }
}
