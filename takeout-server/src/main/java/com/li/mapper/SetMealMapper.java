package com.li.mapper;

import com.li.annotation.AutoFill;
import com.li.common.enumeration.OperationType;
import com.li.pojo.dto.SetmealDTO;
import com.li.pojo.entity.Setmeal;
import com.li.pojo.entity.SetmealDish;
import com.li.pojo.vo.SetmealVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealMapper {
    // 获取套餐
    @Select("select s.category_id, s.id, s.name, s.price, s.status, " +
            "s.description, s.image, s.create_time, s.update_time, c.name as category_name " +
            "from setmeal s, category c where s.category_id = c.id and s.id = #{id}")
    SetmealVO getMealById(Long id);
    // 获取套餐菜品
    @Select("select sd.copies, sd.setmeal_id, s.name, sd.dish_id, " +
            "s.price, s.id from setmeal_dish sd, setmeal s " +
            "where  sd.setmeal_id = s.id and s.id = #{id}")
    List<SetmealDish> getMealDishes(Long id);
    // 新增套餐
    @AutoFill(value = OperationType.INSERT)
    void addSetMeal(Setmeal setmeal);
    // 根据名字查询套餐
    @Select("select * from setmeal where name = #{name}")
    Setmeal getByName(String name);
    // 新增套餐菜品
    @Insert("insert into setmeal_dish(copies, setmeal_id, dish_id, name, price) values (#{copies},#{setmealId},#{dishId},#{name}, #{price}) ")
    void addSetMealDish(SetmealDish setmealDish);

    // 更新套餐
    @AutoFill(value = OperationType.UPDATE)
    void updateSetMeal(Setmeal setmeal);
    // 更新套餐菜品
    void updateSetMealDish(SetmealDish setmealDish);
    // 分页查询
    List<SetmealVO> page(String name, Long categoryId, Integer status);
    // 批量删除套餐
    void deleteById(Long id);
    // 根据id查询套餐状态
    @Select("select status from setmeal where id = #{id}")
    Integer getStatusById(Long id);
    // 根据dishId查询菜品状态
    @Select("select status from dish where id = #{dishId}")
    Integer getStatusByDishId(Long dishId);
    // 根据分类id查询套餐列表
    @Select("select id, category_id, name, price, status, description, image, create_time, update_time, create_user, update_user from setmeal where category_id = #{categoryId}")
    List<Setmeal> setmealList(Long categoryId);
}
