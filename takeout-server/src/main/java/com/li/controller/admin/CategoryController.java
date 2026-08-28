package com.li.controller.admin;

import com.li.common.result.PageResult;
import com.li.common.result.Result;
import com.li.pojo.dto.CategoryDTO;
import com.li.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    /*
    新增分类
     */
    @PostMapping
    public Result save(@RequestBody CategoryDTO categoryDTO) {
        categoryService.save(categoryDTO);
        return Result.success();
    }
    /*
    修改分类
     */
    @PutMapping
    public Result update(@RequestBody CategoryDTO categoryDTO) {
        categoryService.update(categoryDTO);
        return Result.success();
    }
    /*
    启用/禁用分类
     */
    @PostMapping("/status/{status}")
    public Result updateForStatus(@PathVariable Integer status, @RequestParam Long id) {
        categoryService.updateForStatus(status, id);
        return Result.success();
    }

    /*
    分页查询
     */
    @GetMapping("/page")
    // @RequestParam(value = "page", defaultValue = "1") Integer page,
    public Result<PageResult> page(@RequestParam(value = "pageNum", defaultValue = "1") Integer page,
                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, String name,
                                   Integer type) {
        PageResult pageResult = categoryService.page(page, pageSize, name, type);
        return Result.success(pageResult);
    }
    /*
    根据类型获取分类
     */
    @GetMapping("/list")
    public Result list(Integer type) {
        return Result.success(categoryService.list(type));
    }
    /*
    根据id删除分类
     */
    @DeleteMapping
    public Result deleteById(@RequestParam Long id) {
        categoryService.deleteById(id);
        return Result.success();
    }
}
