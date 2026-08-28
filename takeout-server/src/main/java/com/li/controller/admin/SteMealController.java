package com.li.controller.admin;

import com.li.common.result.PageResult;
import com.li.common.result.Result;
import com.li.pojo.dto.SetmealDTO;
import com.li.pojo.vo.SetmealVO;
import com.li.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
public class SteMealController {
    @Autowired
    private SetMealService setMealService;
    /*
    查询套餐
     */
    @GetMapping("/{id}")
    public Result getMealById(@PathVariable Long id){
        SetmealVO setmealVO = setMealService.getMealById(id);
        return Result.success(setmealVO);
    }
    /*
    新增套餐
     */
    @PostMapping
    public Result addSetMeal(@RequestBody SetmealDTO setmealDTO){
        setMealService.addSetMeal(setmealDTO);
        return Result.success();
    }
    /*
    修改套餐
     */
    @PutMapping
    public Result updateSetMeal(@RequestBody SetmealDTO setmealDTO){
        setMealService.updateSetMeal(setmealDTO);
        return Result.success();
    }
    /*
    起售停售套餐
     */
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status,Long id){
        setMealService.updateStatus(status,id);
        return Result.success();
    }
    /*
    分页查询
     */
    @GetMapping("/page")
    public Result<PageResult> page(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                   String name, Long categoryId, Integer status){
        return Result.success(setMealService.page(page, pageSize, name, categoryId, status));
    }
    /*
    批量删除
     */
    @DeleteMapping
    public Result deleteByIds(@RequestParam("ids") List<Long> ids){
        setMealService.deleteByIds(ids);
        return Result.success();
    }
}
