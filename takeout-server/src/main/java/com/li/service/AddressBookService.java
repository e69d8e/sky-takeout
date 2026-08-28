package com.li.service;

import com.li.pojo.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    // 新增地址
    void add(AddressBook address);

    // 获取地址列表
    List<AddressBook> list();

    // 设置默认地址
    void setDefault(Long id);
    // 获取默认地址
    AddressBook getByIsDefault();
    // 修改地址
    void update(AddressBook address);
    // 根据id查询地址
    AddressBook getById(Long id);
    // 根据id删除地址
    void delete(Long id);
}
