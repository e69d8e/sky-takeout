package com.li.mapper;

import com.li.pojo.entity.OrderDetail;
import com.li.pojo.entity.Orders;
import com.li.pojo.vo.OrderStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    // 插入订单数据
    void add(Orders orders);
    // 插入订单明细数据
    void addOrderDetail(List<OrderDetail> orderDetails);
    // 根据订单号查询订单数据
    @Select("select * from orders where number = #{number} and user_id = #{userId}")
    Orders getByNumber(String number, Long userId);

    // 修改订单支付状态
    @Update("update orders set pay_status = #{payStatus} where number = #{number} and user_id = #{userId}")
    void updatePayStatus(String number, Integer payStatus, Long userId);
    // 更新订单
    void update(Orders orders);
    // 根据id查询订单数据
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);
    // 根据订单id查询订单明细数据
    @Select("select * from order_detail where order_id = #{id}")
    List<OrderDetail> getByOrderId(Long id);
    // 查询历史订单
    @Select("select * from orders where user_id = #{id} and status = #{status}")
    List<Orders> historyOrders(Long id, Integer status);
    // 修改订单状态
    @Update("update orders set status = #{status} where order_time < #{time} and status = #{oldStatus} and pay_status = #{payStatus}")
    void updateStatus(Integer oldStatus, LocalDateTime time, Integer status, Integer payStatus);
    // 接单
    @Update("update orders set status = #{status}, delivery_time = #{time} where id = #{id} and status = #{oldStatus}")
    void confirm(Long id, Integer oldStatus, Integer status, LocalDateTime time);
    // 拒单
    @Update("update orders set rejection_reason = #{rejectionReason}, status = #{status} where id = #{id} and status = #{oldStatus}")
    void rejection(Long id, String rejectionReason, Integer oldStatus, Integer status);
    // 派送
    @Update("update orders set status = #{deliveryInProgress} where id = #{id} and status = #{confirmed}")
    void delivery(Long id, Integer confirmed, Integer deliveryInProgress);
    // 完成
    @Update("update orders set status = #{completed} where id = #{id} and status = #{status}")
    void complete(Long id, Integer status, Integer completed);
    // 各状态订单数量统计
    @Select("select count(*) from orders where status = #{status}")
    Integer statistics(Integer status);
    // 订单搜索
    List<Orders> conditionSearch(LocalDateTime beginTime, LocalDateTime endTime, String number, Integer status, String phone);
}
