package com.li.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.li.common.constant.MessageConstant;
import com.li.common.constant.StatusConstant;
import com.li.common.exception.BaseException;
import com.li.common.exception.DeletionNotAllowedException;
import com.li.common.exception.SetmealEnableFailedException;
import com.li.common.result.PageResult;
import com.li.mapper.SetMealMapper;
import com.li.pojo.dto.SetmealDTO;
import com.li.pojo.entity.Setmeal;
import com.li.pojo.entity.SetmealDish;
import com.li.pojo.vo.SetmealVO;
import com.li.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class SetMealServiceImpl implements SetMealService {
    @Autowired
    private SetMealMapper setMealMapper;
    // 根据套餐id查询套餐信息
    @Override
    public SetmealVO getMealById(Long id) {
        SetmealVO setmealVO = setMealMapper.getMealById(id);
        List<SetmealDish> setmealDishes = setMealMapper.getMealDishes(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }
    // 新增套餐
    @Override
    @Transactional
    @CacheEvict(cacheNames = "setmeal_category_", key = "#setmealDTO.getCategoryId()")
    public void addSetMeal(SetmealDTO setmealDTO) {
        // 新增套餐前先判断套餐内是否有该套餐
        if (setMealMapper.getByName(setmealDTO.getName()) != null) {
            throw new BaseException(MessageConstant.MEAL_EXISTS);
        }

        Setmeal setmeal = Setmeal.builder()
                .categoryId(setmealDTO.getCategoryId())
                .description(setmealDTO.getDescription())
                .id(setmealDTO.getId())
                .image(setmealDTO.getImage())
                .price(setmealDTO.getPrice())
                .status(setmealDTO.getStatus())
                .name(setmealDTO.getName())
                .build();
        setMealMapper.addSetMeal(setmeal); // 保存套餐信息
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmeal.getId());
            setMealMapper.addSetMealDish(setmealDish); // 保存套餐菜品关联信息
        }
    }
    // 修改套餐
    @Override
    @Transactional
    @CacheEvict(cacheNames = "setmeal_category_", allEntries = true)
    public void updateSetMeal(SetmealDTO setmealDTO) {
        if (setMealMapper.getByName(setmealDTO.getName()) != null) {
            return;
        }
        Setmeal setmeal = Setmeal.builder()
                .categoryId(setmealDTO.getCategoryId())
                .description(setmealDTO.getDescription())
                .id(setmealDTO.getId())
                .image(setmealDTO.getImage())
                .price(setmealDTO.getPrice())
                .status(setmealDTO.getStatus())
                .name(setmealDTO.getName())
                .build();
        setMealMapper.updateSetMeal(setmeal); // 更新套餐信息
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmeal.getId());
            setMealMapper.updateSetMealDish(setmealDish); // 更新套餐关联菜品
        }
    }
    // 修改套餐状态
    @Override
    @Transactional
    @CacheEvict(cacheNames = "setmeal_category_", allEntries = true)
    public void updateStatus(Integer status, Long id) {
        // 判断套餐中菜品是否起售
        List<SetmealDish> mealDishes = setMealMapper.getMealDishes(id);
        for (SetmealDish mealDish : mealDishes) {
            Integer statusDish = setMealMapper.getStatusByDishId(mealDish.getDishId());
            if (Objects.equals(statusDish, StatusConstant.DISABLE)) {
                throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }
        // 更新套餐状态
        setMealMapper.updateSetMeal(Setmeal.builder().status(status).id(id).build());
    }

    // 分页查询
    @Override
    public PageResult page(Integer page, Integer pageSize, String name, Long categoryId, Integer status) {
        log.info("分页查询套餐信息，页码：{}，页大小：{} name{}, categoryId{}, Status{}", page, pageSize, name, categoryId, status);
        PageHelper.startPage(page, pageSize);
        Page<SetmealVO> p = (Page<SetmealVO>) setMealMapper.page(name, categoryId, status);
        return new PageResult(p.getTotal(), p.getResult());
    }
    // 批量删除套餐
    @Override
    @Transactional
    @CacheEvict(cacheNames = "setmeal_category_", allEntries = true)
    public void deleteByIds(List<Long> ids) {
        for (Long id : ids) {
            if (Objects.equals(setMealMapper.getStatusById(id), StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
            setMealMapper.deleteById(id);
        }
    }
}
