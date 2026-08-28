package com.li.mapper;

import com.li.annotation.AutoFill;
import com.li.pojo.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

import com.li.common.enumeration.OperationType;

@Mapper
public interface AdminMapper {
    @Select("select *from employee where username = #{username}")
    Employee getByName(String username);

    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into employee(username,name,phone,sex,id_number,create_time,update_time,create_user,update_user, password) " +
            "values (#{username}, #{name}, #{phone}, #{sex}, #{idNumber}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser}, #{password}) ")
    void save(Employee employee);


    List<Employee> page(Integer pageNum, Integer pageSize, String name);

    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);

    @Update("update employee set status = #{status} where id = #{id}")
    void updateForStatus(Integer status, Long id);

    @Update("update employee set password = #{password} where id = #{id}")
    void updetePassword(String password, Long id);

    @AutoFill(value = OperationType.UPDATE)
    void update(Employee employee);
}
