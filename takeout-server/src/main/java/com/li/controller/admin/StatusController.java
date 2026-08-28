package com.li.controller.admin;


import com.li.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/shop")
public class StatusController {
    @Autowired
    private RedisTemplate redisTemplate;
    /*
    设置店铺营业状态
     */
    @PutMapping("/{status}")
    public Result changeStatus(@PathVariable Integer status){
        // status 0 关闭 1 开启
        redisTemplate.opsForValue().set("SHOP_STATUS", status);
        return Result.success();
    }
    /*
    获取营业状态
     */
    @GetMapping
    public Result getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get("SHOP_STATUS");
        log.info("获取营业状态为：{}", status);
        return Result.success(status);
    }
}
