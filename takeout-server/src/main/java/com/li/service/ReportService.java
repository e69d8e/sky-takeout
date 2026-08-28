package com.li.service;

import com.li.pojo.vo.OrderReportVO;
import com.li.pojo.vo.SalesTop10ReportVO;
import com.li.pojo.vo.TurnoverReportVO;
import com.li.pojo.vo.UserReportVO;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

public interface ReportService {
    // 营业额统计
    TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end);
    // 订单统计
    OrderReportVO ordersStatistics(LocalDate begin, LocalDate end);
    // 用户统计
    UserReportVO userStatistics(LocalDate begin, LocalDate end);
    // top100
    SalesTop10ReportVO top10(LocalDate begin, LocalDate end);
}
