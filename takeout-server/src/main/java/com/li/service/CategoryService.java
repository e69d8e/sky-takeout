package com.li.service;

import com.li.common.result.PageResult;
import com.li.pojo.dto.CategoryDTO;
import com.li.pojo.entity.Category;

import java.util.List;

public interface CategoryService {
    // 新增分类
    void save(CategoryDTO categoryDTO);
    // 修改分类
    void update(CategoryDTO categoryDTO);
    // 修改分类状态
    void updateForStatus(Integer status, Long id);
    // 分页查询
    PageResult page(Integer pageNum, Integer pageSize, String name, Integer type);
    // 通过类型查询分类
    List<Category> list(Integer type);
    // 删除分类
    void deleteById(Long id);
}
