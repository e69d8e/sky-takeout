package com.li.controller.admin;

import com.li.common.result.Result;
import com.li.pojo.vo.BusinessDataVO;
import com.li.pojo.vo.DishOverViewVO;
import com.li.pojo.vo.OrderOverViewVO;
import com.li.pojo.vo.SetmealOverViewVO;
import com.li.service.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/workspace")
public class WorkspaceController {
    @Autowired
    private WorkspaceService workspaceService;
    /*
    查询今日运营数据
     */
    @GetMapping("/businessData")
    public Result<BusinessDataVO> businessData(){
        return Result.success(workspaceService.businessData());
    }
    /*
    查询套餐总览
     */
    @GetMapping("/overviewSetmeals")
    public Result<SetmealOverViewVO> overviewSetmeal(){
        return Result.success(workspaceService.overviewSetmeal());
    }
    /*
    查询菜品总览
     */
    @GetMapping("/overviewDishes")
    public Result<DishOverViewVO> overviewDishes(){
        return Result.success(workspaceService.overviewDish());
    }
    /*
    查询订单管理数据
     */
    @GetMapping("/overviewOrders")
    public Result<OrderOverViewVO> overviewOrders(){
        return Result.success(workspaceService.overviewOrders());
    }
}
