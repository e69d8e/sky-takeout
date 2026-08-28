package com.li.controller.user;

import com.li.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class GetStatusController {
    @Autowired
    private RedisTemplate redisTemplate;
    /*
    获取营业状态
     */
    @GetMapping("/user/shop/status")
    public Result getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get("SHOP_STATUS");
        log.info("获取营业状态 {}", status);
        return Result.success(status);
    }
}
