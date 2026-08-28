package com.li.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.li.common.constant.MessageConstant;
import com.li.common.constant.StatusConstant;
import com.li.common.exception.BaseException;
import com.li.common.exception.DeletionNotAllowedException;
import com.li.common.result.PageResult;
import com.li.mapper.CategoryMapper;
import com.li.pojo.dto.CategoryDTO;
import com.li.pojo.entity.Category;
import com.li.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    // 新增
    @Override
    public void save(CategoryDTO categoryDTO) {
        // 新增之前判断是否有该分类
        if (categoryMapper.getByName(categoryDTO.getName()) != null) {
            throw new BaseException(MessageConstant.CATEGORY_EXISTS);
        }
        Category category = Category.builder()
                .name(categoryDTO.getName())
                .sort(categoryDTO.getSort())
                .type(categoryDTO.getType())
                .status(StatusConstant.ENABLE)
                .build();
        log.info("category:{}",category);
        categoryMapper.save(category);
    }
    // 修改分类
    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = Category.builder()
                .id(categoryDTO.getId())
                .name(categoryDTO.getName())
                .sort(categoryDTO.getSort())
                .type(categoryDTO.getType())
                .status(StatusConstant.ENABLE)
                .build();
        log.info("category:{}",category);
        categoryMapper.update(category);
    }
    // 修改状态
    @Override
    public void updateForStatus(Integer status, Long id) {
        categoryMapper.update(Category.builder().status(status).id(id).build());
    }
    // 分页查询
    @Override
    public PageResult page(Integer pageNum, Integer pageSize, String name, Integer type) {
        PageHelper.startPage(pageNum, pageSize);
        Page<Category> page = (Page<Category>) categoryMapper.page(name, type);
        log.info("pageNum:{}, pageSize:{}, name:{}", pageNum, pageSize, name);
        return new PageResult(page.getTotal(), page.getResult());
    }
    // 根据类型获取分类
    @Override
    public List<Category> list(Integer type) {
        return categoryMapper.page(null, type);
    }
    // 删除
    @Override
    public void deleteById(Long id) {
        // 删除前先判断是否关联了菜品或套餐
        if (categoryMapper.getDishById(id) > 0) {
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        if (categoryMapper.getSetmealById(id) > 0) {
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        categoryMapper.deleteById(id);
    }
}
