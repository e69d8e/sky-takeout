package com.li.service;

import com.li.pojo.dto.ShoppingCartDTO;
import com.li.pojo.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    // 添加到购物车
    void add(ShoppingCartDTO shoppingCartDTO);
    // 清空购物车
    void clean();
    // 购物车列表
    List<ShoppingCart> list();
    // 删除购物车的单条数据
    void sub(ShoppingCartDTO shoppingCartDTO);
}
