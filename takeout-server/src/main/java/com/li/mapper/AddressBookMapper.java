package com.li.mapper;

import com.li.pojo.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AddressBookMapper {
    // 新增
    @Insert("insert into address_book (user_id, consignee, sex, phone, " +
            "province_code, province_name, city_code, city_name, " +
            "district_code, district_name, detail, label, is_default) " +
            "values (#{userId}, #{consignee},  #{sex}, #{phone}," +
            " #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}, " +
            "#{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    void add(AddressBook address);
    // 获取地址列表
    @Select("select id, user_id, consignee, sex, phone, province_code, " +
            "province_name, city_code, city_name, district_code, district_name, " +
            "detail, label, is_default from address_book where user_id = #{id}")
    List<AddressBook> list(Long id);
    // 根据id修改地址
    void update(AddressBook address);
    // 找出默认地址
    @Select("select id, user_id, consignee, sex, phone, province_code, " +
            "province_name, city_code, city_name, district_code, district_name, " +
            "detail, label, is_default from address_book where user_id = #{id} and is_default = 1")
    AddressBook getByIsDefault(Long id);
    // 根据id查询地址
    @Select("select id, user_id, consignee, sex, phone, province_code, " +
            "province_name, city_code, city_name, district_code, district_name, " +
            "detail, label, is_default from address_book where id = #{id} and user_id = #{userId}")
    AddressBook getById(Long id, Long userId);

    @Delete("delete from address_book where id = #{id} and user_id = #{userId}")
    void delete(Long id, Long userId);
}
