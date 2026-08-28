package com.li.service;

import com.li.pojo.entity.Category;
import com.li.pojo.entity.Setmeal;
import com.li.pojo.vo.DishItemVO;
import com.li.pojo.vo.DishVO;

import java.util.List;

public interface MealService {
    // 查询套餐分类
    List<Category> categoryList(String type);
    // 查询套餐
    List<Setmeal> setmealList(Long categoryId);
    // 查询套餐菜品
    List<DishItemVO> dishList(Long id);
    // 查询分类菜品
    List<DishVO> dishListByCategoryId(Long categoryId);
}
