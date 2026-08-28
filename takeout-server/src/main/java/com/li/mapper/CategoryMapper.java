package com.li.mapper;

import com.li.annotation.AutoFill;
import com.li.common.enumeration.OperationType;
import com.li.pojo.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {

    // 新增分类
    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into category(type, name, sort, status, create_time, update_time, create_user, update_user) " +
    "values" + "(#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void save(Category category);
    // 修改分类
    @AutoFill(value = OperationType.UPDATE)
    void update(Category category);
    // 根据条件查询
    List<Category> page(String name, Integer type);
    // 删除分类
    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);
    // 根据id查询分类
    @Select("select count(*) from setmeal where category_id = #{id}")
    Integer getSetmealById(Long id);
    // 根据id查询关联的菜品
    @Select("select count(*) from dish where category_id = #{id}")
    Integer getDishById(Long id);
    // 根据名称查询
    @Select("select * from category where name = #{name}")
    Category getByName(String name);
    // 查询分类列表
    @Select("select id, name, sort, status, create_time, create_user, type, update_time, update_user from category where type = #{type} and status = status")
    List<Category> categoryList(String type, Integer status);
}
