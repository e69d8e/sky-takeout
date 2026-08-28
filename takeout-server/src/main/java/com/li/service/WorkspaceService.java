package com.li.service;

import com.li.pojo.vo.BusinessDataVO;
import com.li.pojo.vo.DishOverViewVO;
import com.li.pojo.vo.OrderOverViewVO;
import com.li.pojo.vo.SetmealOverViewVO;

public interface WorkspaceService {
    // 查询今日运营数据
    BusinessDataVO businessData();
    // 查询套餐总览
    SetmealOverViewVO overviewSetmeal();
    // 查询菜品总览
    DishOverViewVO overviewDish();
    // 查询订单管理数据
    OrderOverViewVO overviewOrders();
}
