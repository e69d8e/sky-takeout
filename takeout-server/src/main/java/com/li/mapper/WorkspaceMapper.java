package com.li.mapper;

import com.li.pojo.vo.BusinessDataVO;
import com.li.pojo.vo.DishOverViewVO;
import com.li.pojo.vo.OrderOverViewVO;
import com.li.pojo.vo.SetmealOverViewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface WorkspaceMapper {
    // 营业额，有效订单数，平均客单价，订单完成率统计
    @Select("select sum(case when status = #{status} then amount end) as turnover, " +
            "count(case when status = #{status} then 1 end) as validOrderCount," +
            " avg(case when status = #{status} then amount end) as unitPrice, " +
            "count(case when status = #{status} then 1 end)/count(*) as orderCompletionRate " +
            "from orders where order_time between #{begTime} and #{endTime}")
    BusinessDataVO businessData(LocalDateTime begTime, LocalDateTime endTime, Integer status);
    // 新增用户数
    @Select("select count(*) from user where create_time between #{begTime} and #{endTime}")
    Integer newUsers(LocalDateTime begTime, LocalDateTime endTime);
    // 查询套餐总览
    @Select("select count(case when status = #{enable} then 1 end) as sold, " +
            "count(case when status = #{disable} then 1 end) as discontinued " +
            "from setmeal")
    SetmealOverViewVO overviewSetmeal(Integer enable, Integer disable);
    // 查询菜品总览
    @Select("select count(case when status = #{enable} then 1 end) as sold, " +
            "count(case when status = #{disable} then 1 end) as discontinued " +
            "from dish")
    DishOverViewVO overviewDish(Integer enable, Integer disable);
    // 查询订单总览
    @Select("select count(case when status = #{waitingOrders} then 1 end) as waitingOrders, " +
            "count(case when status = #{deliveredOrders} then 1 end) as deliveredOrders, " +
            "count(case when status = #{completedOrders} then 1 end) as completedOrders, " +
            "count(case when status = #{cancelledOrders} then 1 end) as cancelledOrders, " +
            "count(*) as allOrders " +
            "from orders")
    OrderOverViewVO overviewOrders(Integer waitingOrders, Integer deliveredOrders, Integer completedOrders, Integer cancelledOrders);
}
