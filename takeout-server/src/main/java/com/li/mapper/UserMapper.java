package com.li.mapper;

import com.li.pojo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    // 根据openid查询用户
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);
    // 新增用户
    void save(User user);
}
