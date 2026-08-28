package com.li.mapper;

import com.li.pojo.dto.ShoppingCartDTO;
import com.li.pojo.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ShppingCartMapper {
    // 添加菜品和套餐
    @Insert("insert into shopping_cart(name, image, user_id, dish_id, " +
            "dish_flavor, number, amount, create_time, setmeal_id) values " +
            "(#{name}, #{image}, #{userId}, #{dishId}, #{dishFlavor}, #{number}, #{amount}, #{createTime}, #{setmealId})")
    void add(ShoppingCart shoppingCart);
    // 根据菜品id查询
    @Select("select name, image, id as dish_id, price as amount from dish where id = #{dishId}")
    ShoppingCart getByDishId(Long dishId);
    // 根据套餐id查询
    @Select("select name, image, id as setmeal_id, price as amount from setmeal where id = #{setmealId}")
    ShoppingCart getBySetmealId(Long setmealId);

    // 获取菜品数量
    @Select("select number from shopping_cart where dish_id = #{dishId} and user_id = #{userId}")
    Integer getDishNumber(Long dishId, Long userId);
    // 获取套餐数量
    @Select("select number from shopping_cart where setmeal_id = #{setmealId} and user_id = #{userId}")
    Integer getSetmealNumber(Long setmealId, Long userId);

    // 修改菜品数量
    @Update("update shopping_cart set number = #{i} where dish_id = #{id} and user_id = #{userId}")
    void updateDishNumber(Long id, Long userId, int i);
    // 修改套餐数量
    @Update("update shopping_cart set number = #{i} where setmeal_id = #{id} and user_id = #{userId}")
    void updateSetmealNumber(Long id, Long userId, int i);
    // 清空购物车
    @Delete("delete from shopping_cart where user_id = #{id}")
    void clean(Long id);
    // 获取购物车列表
    @Select("select id, name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time " +
            "from shopping_cart where user_id = #{id}")
    List<ShoppingCart> list(Long id);
    // 删除购物车的单条数据
    @Delete("delete from shopping_cart where user_id = #{userId} and (dish_id = #{dishId} or setmeal_id = #{setmealId})")
    void sub(Long userId, Long dishId, Long setmealId);
    // 计算购物车总金额
    @Select("select sum(amount*number) from shopping_cart where user_id = #{userId}")
    BigDecimal getAmount(Long userId);
}
