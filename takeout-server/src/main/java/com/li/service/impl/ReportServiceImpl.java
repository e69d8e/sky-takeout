package com.li.service.impl;

import com.li.mapper.ReportMapper;
import com.li.pojo.entity.Orders;
import com.li.pojo.entity.Top100;
import com.li.pojo.vo.OrderReportVO;
import com.li.pojo.vo.SalesTop10ReportVO;
import com.li.pojo.vo.TurnoverReportVO;
import com.li.pojo.vo.UserReportVO;
import com.li.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportMapper reportMapper;
    // 营业额统计
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        // 在日期范围内查询所有订单营业额
        List<LocalDate> dateList = new ArrayList<>();
        List<BigDecimal> list = new ArrayList<>();
        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            dateList.add(date);
            BigDecimal turnover = reportMapper.turnoverStatistics(LocalDateTime.of(date, LocalTime.MIN),
                    LocalDateTime.of(date, LocalTime.MAX), Orders.COMPLETED);
            list.add(turnover == null ? BigDecimal.ZERO : turnover);
        }
        // 将日期列表拼接成字符串
        String dateListStr = dateList.stream().map(Object::toString).reduce("", (s1, s2) -> s1 + "," + s2);
        // 将营业额列表拼接成字符串
        String turnoverListStr = list.stream().map(Object::toString).reduce("", (s1, s2) -> s1 + "," + s2);
        return new TurnoverReportVO(dateListStr.substring(1), turnoverListStr.substring(1));
    }
    // 订单统计
    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        // 在日期范围内查询所有订单
        List<LocalDate> dateList = new ArrayList<>(); // 日期列表
        List<Integer> orderCountList = new ArrayList<>(); // 订单数量列表
        List<Integer> validOrderCountList = new ArrayList<>(); // 有效订单数量列表
        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            dateList.add(date);
            // 查询订单数量
            Integer orderCount = reportMapper.ordersStatistics(LocalDateTime.of(date, LocalTime.MIN),
                    LocalDateTime.of(date, LocalTime.MAX));
            orderCountList.add(orderCount == null ? 0 : orderCount);
            // 查询有效订单数量
            Integer validOrderCount = reportMapper.validOrdersStatistics(LocalDateTime.of(date, LocalTime.MIN),
                    LocalDateTime.of(date, LocalTime.MAX), Orders.COMPLETED);
            validOrderCountList.add(validOrderCount == null ? 0 : validOrderCount);
        }
        Integer totalOrderCount = 0; // 总订单数量
        for (Integer i : orderCountList) {
            totalOrderCount += i;
        }
        Integer validOrderCount = 0; // 有效订单数量
        for (Integer i : validOrderCountList) {
            validOrderCount += i;
        }
        double orderCompletionRate = 0.0; // 订单完成率
        if (totalOrderCount != 0) {
            orderCompletionRate = ((double)validOrderCount)/((double)totalOrderCount);
        }
        // 拼接成字符串
        String dateListStr = dateList.stream().map(Object::toString).reduce("", (s1, s2) -> s1 + "," + s2);
        String orderCountListStr = orderCountList.stream().map(Object::toString).reduce("", (s1, s2) -> s1 + "," + s2);
        String validOrderCountListStr = validOrderCountList.stream().map(Object::toString).reduce("", (s1, s2) -> s1 + "," + s2);
        return new OrderReportVO(dateListStr.substring(1), orderCountListStr.substring(1),
                validOrderCountListStr.substring(1),
                totalOrderCount, validOrderCount, orderCompletionRate);
    }
    // 用户统计
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>(); // 日期列表
        List<Integer> newUserList = new ArrayList<>(); // 新增用户数列表
        List<Integer> totalUserList = new ArrayList<>(); // 总用户数列表
        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            dateList.add(date);
            // 查询新增用户数量
            Integer newUserCount = reportMapper.newUserCount(LocalDateTime.of(date, LocalTime.MIN),
                    LocalDateTime.of(date, LocalTime.MAX));
            newUserList.add(newUserCount == null ? 0 : newUserCount);
            // 查询总用户数量
            Integer totalUserCount = reportMapper.totalUser(LocalDateTime.of(date, LocalTime.MIN));
            totalUserList.add(totalUserCount == null ? 0 : totalUserCount);
        }
        // 拼接成字符串
        String dateListStr = dateList.stream().map(Object::toString).reduce("", (s1, s2) -> s1 + "," + s2);
        String newUserListStr = newUserList.stream().map(Object::toString).reduce("", (s1, s2) -> s1 + "," + s2);
        String totalUserListStr = totalUserList.stream().map(Object::toString).reduce("", (s1, s2) -> s1 + "," + s2);
        return new UserReportVO(dateListStr.substring(1),
                totalUserListStr.substring(1),
                newUserListStr.substring(1));
    }
    // top100
    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
//        Top100 top100 = reportMapper.top100();

        List<Top100> top100s = reportMapper.top100(Orders.COMPLETED, LocalDateTime.of(begin, LocalTime.MIN),
                LocalDateTime.of(end, LocalTime.MAX));
        if (top100s == null) return new SalesTop10ReportVO();
        String nameListStr = "";
        String numberListStr = "";
        // 拼接成字符串
        for (Top100 top100 : top100s) {
            nameListStr += top100.getName() + ",";
            numberListStr += top100.getNumber() + ",";
        }
        return new SalesTop10ReportVO(nameListStr.substring(0, nameListStr.length() - 1),
                numberListStr.substring(0, numberListStr.length() - 1));
    }
}
