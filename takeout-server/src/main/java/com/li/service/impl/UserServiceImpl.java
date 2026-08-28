package com.li.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.li.common.constant.MessageConstant;
import com.li.common.exception.LoginFailedException;
import com.li.common.properties.WeChatProperties;
import com.li.common.utils.HttpClientUtil;
import com.li.mapper.UserMapper;
import com.li.pojo.dto.UserLoginDTO;
import com.li.pojo.entity.User;
import com.li.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;
    // 微信登录
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        if (userLoginDTO.getCode() == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        Map<String, String> params = new HashMap<>();
        params.put("appid", weChatProperties.getAppid());
        params.put("secret", weChatProperties.getSecret());
        params.put("js_code", userLoginDTO.getCode());
        params.put("grant_type", "authorization_code");
        String s = HttpClientUtil.doGet(WX_LOGIN, params);
        JSONObject res = JSON.parseObject(s);
        String openid = res.getString("openid");
        // 判断是否为空 如果是空则登录失败
        if (openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        // 判断当前用户是否为新用户
        User user = userMapper.getByOpenid(openid);
        if (user == null) { // 新用户
            log.info("新用户，自动完成注册");
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.save(user);
        }
        return user;
    }
}
