package com.li.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.li.common.constant.MessageConstant;
import com.li.common.constant.PasswordConstant;
import com.li.common.constant.StatusConstant;
import com.li.common.exception.*;
import com.li.common.properties.JwtProperties;
import com.li.common.result.PageResult;
import com.li.common.utils.MD5Util;
import com.li.common.utils.ThreadLocalUtil;
import com.li.pojo.dto.EmployeeDTO;
import com.li.pojo.dto.EmployeeLoginDTO;
import com.li.pojo.dto.PasswordEditDTO;
import com.li.pojo.entity.Employee;
import com.li.mapper.AdminMapper;
import com.li.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private ThreadLocalUtil threadLocal;

    @Autowired
    private JwtProperties jwtProperties;

    // 登录
    @Override
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        // 使用MD5加密
        employeeLoginDTO.setPassword(MD5Util.encryptToMD5(employeeLoginDTO.getPassword()));

        //

        Employee employee = adminMapper.getByName(employeeLoginDTO.getUsername());
        if (employee == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND); // 账号不存在
        }
        if (!employee.getPassword().equals(employeeLoginDTO.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR); // 密码错误
        }
        if (Objects.equals(employee.getStatus(), StatusConstant.DISABLE)) {
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED); // 账号被锁定
        }

        return employee;
    }


    // 保存员工信息
    @Override
    public void save(EmployeeDTO employeeDTO) throws LoginFailedException {
        log.info("新增员工，员工信息：{}", employeeDTO);
        // 用户名不能重复
        if (adminMapper.getByName(employeeDTO.getUsername()) != null
        ) {
            // 用户名重复和长度校验
            throw new UsernameAlreadyExistsException(MessageConstant.USERNAME_ALREADY_EXISTS);
        }
        if (employeeDTO.getUsername().length() < 4 ||
                employeeDTO.getUsername().length() > 9) {
            throw new BaseException(MessageConstant.USERNAME_REGEX_ERROR);
        }
        // 手机号码校验
        if (employeeDTO.getPhone() != null && !employeeDTO.getPhone().matches("^1[3-9]\\d{9}$")) {
            throw new BaseException(MessageConstant.PHONE_REGEX_ERROR);
        }
        // 身份证号校验
        if (employeeDTO.getIdNumber() != null
                && !employeeDTO.getIdNumber().matches("^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|)\\d{4}$")) {
            throw new BaseException(MessageConstant.ID_NUMBER_REGEX_ERROR);
        }
        // 性别校验
        if (employeeDTO.getSex() != null && !employeeDTO.getSex().matches("^[01]$")) {
            throw new BaseException(MessageConstant.SEX_REGEX_ERROR);
        }

        // 密码默认为123456
        String password = MD5Util.encryptToMD5(PasswordConstant.DEFAULT_PASSWORD);

//        // 获取当前日期
//        LocalDateTime now = LocalDateTime.now();
//
//        // 获取当前登录用户id
//        Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), threadLocal.get().toString());
//        Long empId = claims.get(JwtClaimsConstant.EMP_ID, Long.class);

        Employee employee = Employee.builder()
                .sex(employeeDTO.getSex())
                .name(employeeDTO.getName())
                .phone(employeeDTO.getPhone())
                .status(StatusConstant.ENABLE)
//                .createTime(now)
//                .updateTime(now)
                .username(employeeDTO.getUsername())
//                .createUser(empId)
//                .updateUser(empId)
                .password(password)
                .idNumber(employeeDTO.getIdNumber())
                .build();

        adminMapper.save(employee);
    }

    // 分页查询
    @Override
    public PageResult page(Integer pageNum, Integer pageSize, String name) {
        log.info("pageNum:{},pageSize:{}, name={}", pageNum, pageSize, name);
        // 使用pageHelper分页插件
        PageHelper.startPage(pageNum, pageSize); // 设置分页参数
        List<Employee> list = adminMapper.page(pageNum, pageSize, name); // 执行查询
        Page<Employee> page = (Page<Employee>) list; // 获取分页结果
        log.info("查询结果：{}", page.toString());
        log.info("总记录数：{}", page.getTotal());
        return new PageResult(page.getTotal(), page.getResult());
    }

    // 根据id查询
    @Override
    public Employee getById(Long id) {
        return adminMapper.getById(id);
    }

    // 修改员工状态
    @Override
    public void updateForStatus(Integer status, Long id) {
//        // 获取当前登录用户id
//        Object o = threadLocal.get();
//        Long empId = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), o.toString()).get(JwtClaimsConstant.EMP_ID, Long.class);
        log.info("员工状态修改，员工id为：{} 状态{}", id, status);
        adminMapper.updateForStatus(status, id);
    }

    // 修改密码
    @Override
    public void updatePassword(PasswordEditDTO passwordEditDTO) {
//        // 获取当前登录用户id
//        Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), threadLocal.get().toString());
//        Long empId = claims.get(JwtClaimsConstant.EMP_ID, Long.class);
//        // 获取当前登录用户
//        Employee employee = adminMapper.getById(empId);
        try {
            // 密码加密
            log.info("新密码：{},旧密码：{}, id:{}", passwordEditDTO.getNewPassword(), passwordEditDTO.getOldPassword(), passwordEditDTO.getEmpId());
            String oldPassword = MD5Util.encryptToMD5(passwordEditDTO.getOldPassword());
            String password = MD5Util.encryptToMD5(passwordEditDTO.getNewPassword());

            Employee employee = adminMapper.getById(passwordEditDTO.getEmpId());

            if (!oldPassword.equals(employee.getPassword())) {
                throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
            }

            adminMapper.updetePassword(password, passwordEditDTO.getEmpId());
        } catch (Exception e) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_EDIT_FAILED);
        }
    }

    // 修改员工信息
    @Override
    public void update(EmployeeDTO employeeDTO) {
        log.info("修改员工信息：{}", employeeDTO);
        // 获取当前登录用户id
//        Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), threadLocal.get().toString());
//        Long empId = claims.get(JwtClaimsConstant.EMP_ID, Long.class);
        Employee employee = Employee.builder()
                .id(employeeDTO.getId())
                .name(employeeDTO.getName())
                .phone(employeeDTO.getPhone())
                .sex(employeeDTO.getSex())
                .idNumber(employeeDTO.getIdNumber())
                .username(employeeDTO.getUsername())
//                .updateUser(empId)
//                .updateTime(LocalDateTime.now())
                .build();
        log.info("修改员工信息：{}", employee);

        adminMapper.update(employee);
    }
}
