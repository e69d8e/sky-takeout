package com.li.controller.user;

import com.li.common.result.Result;
import com.li.pojo.dto.ShoppingCartDTO;
import com.li.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService service;

    /*
    添加到购物车
     */
    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        service.add(shoppingCartDTO);
        return Result.success();
    }
    /*
    清除购物车
     */
    @DeleteMapping("/clean")
    public Result clean() {
        service.clean();
        return Result.success();
    }
    /*
    查看购物车
     */
    @GetMapping("/list")
    public Result list() {
        return Result.success(service.list());
    }
    /*
    删除购物车的商品
     */
    @PostMapping("/sub")
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        service.sub(shoppingCartDTO);
        return Result.success();
    }
}
