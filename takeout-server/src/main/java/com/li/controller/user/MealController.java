package com.li.controller.user;

import com.li.common.result.Result;
import com.li.pojo.entity.Category;
import com.li.pojo.entity.Setmeal;
import com.li.pojo.vo.DishItemVO;
import com.li.pojo.vo.DishVO;
import com.li.service.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class MealController {
    @Autowired
    private MealService mealService;

    /*
    * 获取菜品分类列表
     */
    @GetMapping("/category/list")
    public Result categoryList(String type){
        List<Category> categorys = mealService.categoryList(type);
        return Result.success(categorys);
    }
    /*
    根据分类id获取套餐列表
     */
    @GetMapping("/setmeal/list")
    @Cacheable(cacheNames = "setmeal_category_", key = "#categoryId") // 注意不要导错包
    public Result<List<Setmeal>> setmealList(Long categoryId){
        return Result.success(mealService.setmealList(categoryId));
    }
    /*
    根据套餐id查询菜品列表
     */
    @GetMapping("setmeal/dish/{id}")
    public Result<List<DishItemVO>> getSetmealDishes(@PathVariable Long id){
        return Result.success(mealService.dishList(id));
    }
    /*
    根据分类id查询菜品列表
     */
    @GetMapping("/dish/list")
    public Result<List<DishVO>> dishList(Long categoryId){
        return Result.success(mealService.dishListByCategoryId(categoryId));
    }
}
