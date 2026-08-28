package com.li.service;

import com.li.common.result.PageResult;
import com.li.pojo.dto.DishDTO;
import com.li.pojo.vo.DishVO;

import java.util.List;

public interface DishService {
    // 根据id查询菜品
    DishVO getById(Long id);
    // 根据分类id查询菜品
    List<DishVO> getByCategoryId(Long id);
    // 分页查询菜品
    PageResult page(Integer page, Integer pageSize, String name, Long categoryId, Integer status);
    // 修改菜品状态
    void updateForStatus(Integer status, Long id);
    // 修改菜品
    void update(DishDTO dishDTO);
    // 新增菜品
    void save(DishDTO dishDTO);
    // 批量删除菜品
    void deleteByIds(List<Long> ids);
}
