package com.li.service;

import com.li.pojo.dto.UserLoginDTO;
import com.li.pojo.entity.User;

public interface UserService {
    // 微信登录
    User wxLogin(UserLoginDTO userLoginDTO);
}
