package com.li.service.impl;

import com.li.common.constant.JwtClaimsConstant;
import com.li.common.properties.JwtProperties;
import com.li.common.utils.JwtUtil;
import com.li.common.utils.ThreadLocalUtil;
import com.li.mapper.AddressBookMapper;
import com.li.pojo.entity.AddressBook;
import com.li.service.AddressBookService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    private ThreadLocalUtil threadLocal;
    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private AddressBookMapper mapper;
    // 新增
    @Override
    public void add(AddressBook address) {
        // 获取当前登录用户id
        Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), threadLocal.get().toString());
        Long id = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
        if (address.getIsDefault() == null || address.getIsDefault() == 1) {
            address.setIsDefault(1);
            // 找出原来的默认地址 将其设置为非默认
            AddressBook addressBook1 = mapper.getByIsDefault(id);
            if (addressBook1 != null) {
                addressBook1.setIsDefault(0);
                mapper.update(addressBook1);
            }
        }
        if (address.getId() == null) {
            address.setUserId(id);
        }
        mapper.add(address);
    }
    // 获取地址列表
    @Override
    public List<AddressBook> list() {
        // 获取当前登录用户id
        Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), threadLocal.get().toString());
        Long id = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
        return mapper.list(id);
    }

    // 设置默认地址
    @Override
    @Transactional
    public void setDefault(Long addressId) {
        // 获取当前登录用户id
        Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), threadLocal.get().toString());
        Long id = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
        // 找出原来的默认地址 将其设置为非默认
        AddressBook addressBook1 = mapper.getByIsDefault(id);
        if (addressBook1 != null) {
            addressBook1.setIsDefault(0);
            mapper.update(addressBook1);
        }
        AddressBook addressBook = new AddressBook();
        addressBook.setIsDefault(1);
        addressBook.setId(addressId);
        addressBook.setUserId(id);
        mapper.update(addressBook);
    }
    // 获取默认地址
    @Override
    public AddressBook getByIsDefault() {
        // 获取当前登录用户id
        Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), threadLocal.get().toString());
        Long id = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
        return mapper.getByIsDefault(id);
    }
    // 修改地址
    @Override
    @Transactional
    public void update(AddressBook address) {
        // 获取当前登录用户id
        Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), threadLocal.get().toString());
        Long id = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            // 找出原来的默认地址 将其设置为非默认
            AddressBook addressBook1 = mapper.getByIsDefault(id);
            if (addressBook1 != null) {
                addressBook1.setIsDefault(0);
                mapper.update(addressBook1);
            }
        }
        mapper.update(address);
    }

    // 根据id查询地址
    @Override
    public AddressBook getById(Long id) {
        // 获取当前登录用户id
        Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), threadLocal.get().toString());
        Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
        return mapper.getById(id, userId);
    }

    // 根据id删除地址
    @Override
    public void delete(Long id) {
        // 获取当前登录用户id
        Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), threadLocal.get().toString());
        Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
        mapper.delete(id, userId);
    }
}
