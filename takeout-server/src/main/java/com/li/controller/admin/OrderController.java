package com.li.controller.admin;

import com.li.common.result.PageResult;
import com.li.common.result.Result;
import com.li.pojo.dto.OrdersCancelDTO;
import com.li.pojo.dto.OrdersConfirmDTO;
import com.li.pojo.dto.OrdersRejectionDTO;
import com.li.pojo.entity.Orders;
import com.li.pojo.vo.OrderStatisticsVO;
import com.li.pojo.vo.OrderVO;
import com.li.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController("adminOrder")
@RequestMapping("/admin/order")
public class OrderController {
    /*
    接单
     */
    @Autowired
    private OrderService orderService;
    @PutMapping("/confirm")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        orderService.confirm(ordersConfirmDTO.getId());
        return Result.success();
    }
    /*
    拒单
     */
    @PutMapping("/rejection")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }
    /*
    派送订单
     */
    @PutMapping("/delivery/{id}")
    public Result delivery(@PathVariable Long id){
        orderService.delivery(id);
        return Result.success();
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
    完成订单
     */
    @PutMapping("/complete/{id}")
    public Result complete(@PathVariable Long id) {
        orderService.complete(id);
        return Result.success();
    }
    /*
    查询订单详情
     */
    @GetMapping("/details/{id}")
    public Result<Orders> details(@PathVariable Long id) {
        return Result.success(orderService.details(id));
    }
    /*
    各状态订单数量统计
     */
    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> statistics() {
        return Result.success(orderService.statistics());
    }
    /*
    订单搜索
     */
    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginTime,
                                              @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime,
                                              String number, Integer status,
                                              @RequestParam(value = "page", defaultValue = "1") Integer page,
                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, String phone) {
        return Result.success(orderService.conditionSearch(beginTime, endTime, number, status, page, pageSize, phone));
    }
}
