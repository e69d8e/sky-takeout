package com.li.controller.user;

import com.li.common.constant.JwtClaimsConstant;
import com.li.common.properties.JwtProperties;
import com.li.common.result.Result;
import com.li.common.utils.JwtUtil;
import com.li.pojo.dto.UserLoginDTO;
import com.li.pojo.entity.User;
import com.li.pojo.vo.UserLoginVO;
import com.li.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    RedisTemplate redisTemplate;
    @PostMapping("/login")
    public Result login(@RequestBody UserLoginDTO userLoginDTO){
        // 调用service登录
        User user = userService.wxLogin(userLoginDTO);
        // 生成jwt
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();
        // 将token存入redis
        redisTemplate.opsForValue().set(token, token, jwtProperties.getUserTtl(), TimeUnit.MILLISECONDS);
        return Result.success(userLoginVO);
    }

    // 退出登录
    @PostMapping("/logout")
    public Result logout(){
        // 清除redis中的token
        redisTemplate.delete(jwtProperties.getUserTokenName());
        return Result.success();
    }




    // 测试
    @GetMapping("/test")
    public Result test(){
        log.info("测试");
        return Result.success();
    }
}
