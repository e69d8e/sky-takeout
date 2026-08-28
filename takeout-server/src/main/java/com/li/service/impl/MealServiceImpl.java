package com.li.service.impl;

import com.li.common.constant.StatusConstant;
import com.li.mapper.CategoryMapper;
import com.li.mapper.DishMapper;
import com.li.mapper.SetMealMapper;
import com.li.pojo.entity.Category;
import com.li.pojo.entity.DishFlavor;
import com.li.pojo.entity.Setmeal;
import com.li.pojo.vo.DishItemVO;
import com.li.pojo.vo.DishVO;
import com.li.service.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MealServiceImpl implements MealService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SetMealMapper setMealMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    // 分类列表
    @Override
    public List<Category> categoryList(String type) {
        return categoryMapper.categoryList(type, StatusConstant.ENABLE);
    }

    // 套餐列表
    @Override
    @CachePut(cacheNames = "setmeal_category_", key = "#p0")
    public List<Setmeal> setmealList(Long categoryId) {
        // 判断缓存中是否有数据 若有直接返回
//        if (redisTemplate.hasKey("setmeal_category_" + categoryId)) {
//            return (List<Setmeal>) redisTemplate.opsForValue().get("setmeal_category_" + categoryId);
//        }

        List<Setmeal> setMealList = setMealMapper.setmealList(categoryId);
        // 缓存
//        redisTemplate.opsForValue().set("setmeal_category_" + categoryId, setMealList);
        return setMealList;
    }
    // 根据套餐id查菜品列表
    @Override
    public List<DishItemVO> dishList(Long id) {
        return dishMapper.dishList(id);
    }
    // 根据分类id查菜品列表
    @Override
    public List<DishVO> dishListByCategoryId(Long categoryId) {
        // 判断缓存中是否有数据 若有直接返回
        if (redisTemplate.hasKey("dish_category_" + categoryId)) {
            return (List<DishVO>) redisTemplate.opsForValue().get("dish_category_" + categoryId);
        }

        List<DishVO> dishList = dishMapper.dishByCategoryId(categoryId);
        for (DishVO dishVO : dishList) {
            List<DishFlavor> flavors = dishMapper.getFlavors(dishVO.getId());
            dishVO.setFlavors(flavors);
        }
        // 缓存
        redisTemplate.opsForValue().set("dish_category_" + categoryId, dishList);
        return dishList;
    }
}
