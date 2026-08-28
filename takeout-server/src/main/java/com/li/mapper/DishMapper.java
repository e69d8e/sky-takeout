package com.li.mapper;

import com.li.annotation.AutoFill;
import com.li.common.enumeration.OperationType;
import com.li.pojo.entity.Dish;
import com.li.pojo.entity.DishFlavor;
import com.li.pojo.vo.DishItemVO;
import com.li.pojo.vo.DishVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DishMapper {
    // 根据id查询菜品数据
    @Select("select d.category_id as category_id, d.id, d.name, " +
            "c.name as category_name, d.description, d.image, " +
            "d.price, d.status, d.update_time from dish d, " +
            "category c where d.category_id = c.id and d.id = #{id}")
    DishVO getDishById(Long id);
    // 根据菜品id查询口味数据
    @Select("select df.id, df.name, df.value, df.dish_id from dish_flavor df, dish d where df.dish_id = d.id and d.id = #{id}")
    List<DishFlavor> getFlavorById(Long id);
    // 根据分类id查询菜品数据
    @Select("select d.category_id as category_id, d.id, d.name, " +
            "c.name as category_name, d.description, d.image, d.price, " +
            "d.status, d.update_time from dish d, category c where " +
            "d.category_id = c.id and c.id = #{id}")
    List<DishVO> getCateGoryById(Long id);
    // 分页查询菜品数据
    List<DishVO> page(String name, Long categoryId, Integer status);
    // 修改菜品数据
    @AutoFill(value = OperationType.UPDATE)
    void updateDish(Dish dish);
    // 修改口味数据
    @Update("update dish_flavor set value = #{value} where dish_id = #{dishId} and name = #{name}")
    void updateDishFlavor(DishFlavor flavor);
    // 新增菜品数据
    @AutoFill(value = OperationType.INSERT)
    void save(Dish dish);
    // 新增口味数据
    @Insert("insert into dish_flavor(name, value, dish_id) values (#{name}, #{value}, #{dishId})")
    void saveFlavor(DishFlavor flavor);
    // 根据名称查询菜品
    @Select("select id from dish where name = #{name}")
    Dish getByName(String name);
    // 根据id删除菜品
    @Delete("delete from dish where id = #{id}")
    void deleteByIds(Long id);
    // 删除菜品口味
    @Delete("delete from dish_flavor where dish_id = #{id}")
    void deleteFlavor(Long id);
    // 根据菜品id查询关联套餐
    @Select("select count(*) from setmeal_dish where dish_id = #{id}")
    Integer getSetmealById(Long id);
    // 根据id查询菜品状态
    @Select("select status from dish where id = #{id}")
    Integer getStatusById(Long id);
    // 根据套餐id查询菜品列表
    @Select("select copies, description, image, d.name from setmeal_dish sd, dish d where sd.dish_id = d.id and setmeal_id = #{id}")
    List<DishItemVO> dishList(Long id);
    // 根据分类id查询菜品列表
    @Select("select d.category_id as category_id, d.id, d.name, c.name as category_name, " +
            "d.description, d.image, d.price, d.status, d.update_time from dish d, category c " +
            "where d.category_id = c.id and c.id = #{id}")
    List<DishVO> dishByCategoryId(Long categoryId);
    // 根据菜品id查询口味
    @Select("select id, name, value, dish_id from dish_flavor where dish_id = #{id}")
    List<DishFlavor> getFlavors(Long id);
}
