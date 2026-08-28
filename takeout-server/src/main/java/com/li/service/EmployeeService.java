package com.li.service;

import com.li.common.result.PageResult;
import com.li.pojo.dto.EmployeeDTO;
import com.li.pojo.dto.EmployeeLoginDTO;
import com.li.pojo.dto.PasswordEditDTO;
import com.li.pojo.entity.Employee;

public interface EmployeeService {
    // 员工登录
    Employee login(EmployeeLoginDTO employeeLoginDTO);
    // 添加员工
    void save(EmployeeDTO employeeDTO);
    // 分页查询
    PageResult page(Integer pageNum, Integer pageSize, String name);
    // 根据id查询员工
    Employee getById(Long id);
    // 修改员工状态
    void updateForStatus(Integer status, Long id);
    // 修改密码
    void updatePassword(PasswordEditDTO passwordEditDTO);

    void update(EmployeeDTO employeeDTO);
}
