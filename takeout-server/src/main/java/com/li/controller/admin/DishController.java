package com.li.controller.admin;

import com.li.common.result.PageResult;
import com.li.common.result.Result;
import com.li.pojo.dto.DishDTO;
import com.li.pojo.vo.DishVO;
import com.li.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {
    @Autowired
    DishService dishService;
    /*
    根据id查询菜品
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id){
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }
    /*
    根据分类id查询菜品
     */
    @GetMapping("/list/{categoryId}")
    public Result getByCategoryId(@PathVariable Long categoryId){
        return Result.success(dishService.getByCategoryId(categoryId));
    }
    /*
    分页查询
     */
    @GetMapping("/page")
    public Result<PageResult> page(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                   String name, Long categoryId, Integer status){
        return Result.success(dishService.page(page, pageSize, name, categoryId, status));
        //
    }
    /*
    菜品起售停售
     */
    @PostMapping("/status/{status}")
    public Result updateForStatus(@PathVariable Integer status,@RequestParam Long id){
        dishService.updateForStatus(status, id);
        return Result.success();
    }

    /*
    修改菜品
     */
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        dishService.update(dishDTO);
        return Result.success();
    }
    /*
    新增菜品
     */
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO){
        dishService.save(dishDTO);
        return Result.success();
    }
    /*
    菜品批量删除
     */
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除菜品，id为{}", ids);
        dishService.deleteByIds(ids);
        return Result.success();
    }

}
