package com.li.service;

import com.li.common.result.PageResult;
import com.li.common.result.Result;
import com.li.pojo.dto.SetmealDTO;
import com.li.pojo.vo.SetmealVO;

import java.util.List;

public interface SetMealService {
    // 根据id查询套餐
    SetmealVO getMealById(Long id);
    // 新增套餐
    void addSetMeal(SetmealDTO setmealDTO);
    // 修改套餐
    void updateSetMeal(SetmealDTO setmealDTO);
    // 修改套餐状态
    void updateStatus(Integer status, Long id);
    // 分页查询套餐
    PageResult page(Integer page, Integer pageSize, String name, Long categoryId, Integer status);
    // 批量删除套餐
    void deleteByIds(List<Long> ids);
}
