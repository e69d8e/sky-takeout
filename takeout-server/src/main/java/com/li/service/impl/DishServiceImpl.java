package com.li.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.li.common.constant.MessageConstant;
import com.li.common.constant.StatusConstant;
import com.li.common.exception.BaseException;
import com.li.common.exception.DeletionNotAllowedException;
import com.li.common.result.PageResult;
import com.li.mapper.DishMapper;
import com.li.pojo.dto.DishDTO;
import com.li.pojo.entity.Dish;
import com.li.pojo.entity.DishFlavor;
import com.li.pojo.vo.DishVO;
import com.li.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DishMapper dishMapper;
    // 根据id查询菜品
    @Override
    public DishVO getById(Long id) {
        DishVO dishVO = dishMapper.getDishById(id);
        dishVO.setFlavors(dishMapper.getFlavorById(id));
        log.info("dishVo{}", dishVO);
        return dishVO;
    }
    // 根据分类id查询
    @Override
    public List<DishVO> getByCategoryId(Long id) {
        return dishMapper.getCateGoryById(id);
    }
    // 分页查询
    @Override
    public PageResult page(Integer page, Integer pageSize, String name, Long categoryId, Integer status) {
        PageHelper.startPage(page, pageSize);
        Page<DishVO> p = (Page<DishVO>) dishMapper.page(name, categoryId, status);
        return new PageResult(p.getTotal(), p.getResult());
    }
    // 起售停售
    @Override
    @Transactional
    public void updateForStatus(Integer status, Long id) {
        // 清除缓存
        Set keys = redisTemplate.keys("dish_category_*");
        redisTemplate.delete(keys);
        Dish dish = Dish.builder()
                .status(status)
                .id(id)
                .build();
        dishMapper.updateDish(dish);
    }
    // 修改菜品
    @Override
    @Transactional
    public void update(DishDTO dishDTO) {
        Dish dish = Dish.builder()
                .id(dishDTO.getId())
                .price(dishDTO.getPrice())
                .image(dishDTO.getImage())
                .name(dishDTO.getName())
                .categoryId(dishDTO.getCategoryId())
                .description(dishDTO.getDescription())
                .status(dishDTO.getStatus())
                .build();
        log.info("dish{}", dish);
        // 清除缓存
        Set keys = redisTemplate.keys("dish_category_*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        dishMapper.updateDish(dish);
        // 先删除原有口味数据
        dishMapper.deleteFlavor(dishDTO.getId());
        // 重新插入新口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            for (DishFlavor dishFlavor : flavors) {
                dishFlavor.setDishId(dishDTO.getId());
                dishMapper.saveFlavor(dishFlavor);
            }
        }
    }
    // 新增菜品
    @Override
    @Transactional
    public void save(DishDTO dishDTO) {
        // 添加之前判断是否有该菜品
        if (dishMapper.getByName(dishDTO.getName()) != null) {
            throw new BaseException(MessageConstant.DISH_EXISTS);
        }
        if (dishDTO.getStatus() == null) {
            dishDTO.setStatus(StatusConstant.DISABLE);
        }
        if (dishDTO.getDescription() == null) {
            dishDTO.setDescription(MessageConstant.NOT_DESCRIPTION);
        }
        // 删除缓存
        redisTemplate.delete("dish_category_" + dishDTO.getCategoryId());
        Dish dish = Dish.builder()
                .name(dishDTO.getName())
                .price(dishDTO.getPrice())
                .image(dishDTO.getImage())
                .description(dishDTO.getDescription())
                .categoryId(dishDTO.getCategoryId())
                .status(dishDTO.getStatus())
                .build();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        dishMapper.save(dish);
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dish.getId());
            dishMapper.saveFlavor(flavor);
        }
    }
    // 菜品批量删除
    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        for (Long id : ids) {
            if (Objects.equals(dishMapper.getStatusById(id), StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
            if (dishMapper.getSetmealById(id) > 0) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
            // 删除菜品
            dishMapper.deleteByIds(id);
            // 删除菜品口味
            dishMapper.deleteFlavor(id);
        }
        // 删除缓存
        Set keys = redisTemplate.keys("dish_category_*");
        redisTemplate.delete(keys);
    }
}
