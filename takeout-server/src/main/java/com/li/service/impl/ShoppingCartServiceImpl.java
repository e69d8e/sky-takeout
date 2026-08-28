package com.li.service.impl;

import com.li.common.constant.JwtClaimsConstant;
import com.li.common.properties.JwtProperties;
import com.li.common.utils.JwtUtil;
import com.li.common.utils.ThreadLocalUtil;
import com.li.mapper.ShppingCartMapper;
import com.li.pojo.dto.ShoppingCartDTO;
import com.li.pojo.entity.ShoppingCart;
import com.li.service.ShoppingCartService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ThreadLocalUtil threadLocal;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private ShppingCartMapper mapper;
    // 添加购物车
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        // 获取当前登录用户id
        Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), threadLocal.get().toString());
        Long id = claims.get(JwtClaimsConstant.USER_ID, Long.class);

        ShoppingCart shoppingCart = new ShoppingCart();
        // 添加菜品
        if (shoppingCartDTO.getDishId() != null) {
            // 判断购物车中是否已经存在该菜品 存在就不添加 更新number
            Integer dishNumber = mapper.getDishNumber(shoppingCartDTO.getDishId(), id);
            if (dishNumber != null) {
                mapper.updateDishNumber(shoppingCartDTO.getDishId(), id, dishNumber + 1);
                return;
            }
            shoppingCart = mapper.getByDishId(shoppingCartDTO.getDishId());
        }
        // 添加套餐
        else if (shoppingCartDTO.getSetmealId() != null) {
            // 判断购物车中是否已经存在该套餐 存在就不添加 更新number
            Integer setmealNumber = mapper.getSetmealNumber(shoppingCartDTO.getSetmealId(), id);
            if (setmealNumber != null) {
                mapper.updateSetmealNumber(shoppingCartDTO.getSetmealId(), id, setmealNumber + 1);
                return;
            }
            shoppingCart = mapper.getBySetmealId(shoppingCartDTO.getSetmealId());
        }
        // 如果不存在就添加
        shoppingCart.setNumber(1);
        shoppingCart.setUserId(id);
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCart.setDishFlavor(shoppingCartDTO.getDishFlavor());
        mapper.add(shoppingCart);
    }
    // 清空购物车
    @Override
    public void clean() {
        // 获取当前登录用户id
        Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), threadLocal.get().toString());
        Long id = claims.get(JwtClaimsConstant.USER_ID, Long.class);
        mapper.clean(id);
    }
    // 获取购物车
    @Override
    public List<ShoppingCart> list() {
        // 获取当前登录用户id
        Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), threadLocal.get().toString());
        Long id = claims.get(JwtClaimsConstant.USER_ID, Long.class);
        return mapper.list(id);
    }
    // 删除购物车的单条数据
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        // 获取当前登录用户id
        Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), threadLocal.get().toString());
        Long id = claims.get(JwtClaimsConstant.USER_ID, Long.class);
        mapper.sub(id, shoppingCartDTO.getDishId(), shoppingCartDTO.getSetmealId());
    }
}
