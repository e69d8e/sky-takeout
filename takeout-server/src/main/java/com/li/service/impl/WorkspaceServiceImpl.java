package com.li.service.impl;

import com.li.common.constant.StatusConstant;
import com.li.mapper.WorkspaceMapper;
import com.li.pojo.entity.Orders;
import com.li.pojo.vo.BusinessDataVO;
import com.li.pojo.vo.DishOverViewVO;
import com.li.pojo.vo.OrderOverViewVO;
import com.li.pojo.vo.SetmealOverViewVO;
import com.li.service.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {
    @Autowired
    private WorkspaceMapper workspaceMapper;

    // 查询今日运营数据
    @Override
    public BusinessDataVO businessData() {
        LocalDate today = LocalDate.now();
        LocalDateTime begTime = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.now();
        BusinessDataVO businessDataVO = workspaceMapper.businessData(begTime, endTime, Orders.COMPLETED);
        businessDataVO.setNewUsers(workspaceMapper.newUsers(begTime, endTime));
        return businessDataVO;
    }

    // 查询套餐总览
    @Override
    public SetmealOverViewVO overviewSetmeal() {
        return workspaceMapper.overviewSetmeal(StatusConstant.ENABLE, StatusConstant.DISABLE);
    }

    // 查询菜品总览
    @Override
    public DishOverViewVO overviewDish() {
        return workspaceMapper.overviewDish(StatusConstant.ENABLE, StatusConstant.DISABLE);
    }

    // 查询订单管理数据
    @Override
    public OrderOverViewVO overviewOrders() {
        return workspaceMapper.overviewOrders(Orders.TO_BE_CONFIRMED, Orders.CONFIRMED, Orders.COMPLETED, Orders.CANCELLED);
    }
}
