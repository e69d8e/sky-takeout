package com.li.mapper;

import com.li.pojo.entity.Top100;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReportMapper {
    // 查询每天的营业额
    @Select("select sum(amount) from orders where status = #{status} and order_time between #{beg} and #{end}")
    BigDecimal turnoverStatistics(LocalDateTime beg, LocalDateTime end, Integer status);
    // 查询每天的订单数
    @Select("select count(*) from orders where order_time between #{beg} and #{end}")
    Integer ordersStatistics(LocalDateTime beg, LocalDateTime end);
    // 查询每天有效的订单数
    @Select("select count(*) from orders where status = #{status} and order_time between #{beg} and #{end}")
    Integer validOrdersStatistics(LocalDateTime beg, LocalDateTime end, Integer status);
    // 查询每日新增用户数
    @Select("select count(*) from user where create_time between #{beg} and #{end}")
    Integer newUserCount(LocalDateTime beg, LocalDateTime end);
    // 查询每日总用户
    @Select("select count(*) from user where create_time < #{end}")
    Integer totalUser(LocalDateTime end);
    // 查询销量排名top10
    @Select("select name as name, sum(od.number) as number " +
            "from orders o, order_detail od where od.order_id = o.id " +
            "and status = #{status} and o.order_time between #{beg} and #{end} " +
            "group by name limit 100")
    List<Top100> top100(Integer status, LocalDateTime beg, LocalDateTime end);
}
