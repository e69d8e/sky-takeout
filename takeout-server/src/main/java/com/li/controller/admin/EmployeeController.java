package com.li.controller.admin;

import com.li.common.constant.JwtClaimsConstant;
import com.li.common.properties.JwtProperties;
import com.li.common.result.PageResult;
import com.li.common.result.Result;
import com.li.common.utils.JwtUtil;
import com.li.pojo.dto.EmployeeDTO;
import com.li.pojo.dto.EmployeeLoginDTO;
import com.li.pojo.dto.PasswordEditDTO;
import com.li.pojo.entity.Employee;
import com.li.pojo.vo.EmployeeLoginVO;
import com.li.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/admin/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO){
        log.info("员工登录：{}",employeeLoginDTO);
        Employee employee = employeeService.login(employeeLoginDTO);
        // 登录成功后，生成jwt令牌
        Map<String,Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID,employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);
        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();
        // 将jwt令牌存入Redis
        redisTemplate.opsForValue().set(token, token, jwtProperties.getAdminTtl(), TimeUnit.MILLISECONDS); // 缓存token
        return Result.success(employeeLoginVO);
    }

    @GetMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        // 获取请求头中的token
        String token = request.getHeader(jwtProperties.getAdminTokenName());
        // 清除Redis中的token
        redisTemplate.delete(token);
        return Result.success("退出成功");
    }

    @PostMapping
    public Result save(@RequestBody EmployeeDTO employeeDot){
        log.info("新增员工，员工信息：{}", employeeDot);
        employeeService.save(employeeDot);
        return Result.success();
    }
    /*
    分页查询
     */
    @GetMapping("/page")
    public Result<PageResult> page(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
            @RequestParam(value = "name", defaultValue = "") String name
            ) {
        // 方法逻辑
        PageResult page = employeeService.page(pageNum, pageSize, name);
        return Result.success(page);
    }
    /*
    根据id查询员工
     */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息。id为：{}",id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    /*
    修改员工状态
     */
    @PutMapping("/status/{status}")
    public Result updateForStatus(@PathVariable Integer status, @RequestParam Long id){
        employeeService.updateForStatus(status, id);
        return Result.success();
    }

    /*
    修改密码
     */
    @PutMapping("/password")
    public Result updatePassword(@RequestBody PasswordEditDTO passwordEditDTO){
        employeeService.updatePassword(passwordEditDTO);
        return Result.success();
    }
    /*
    编辑员工信息
     */
    @PutMapping
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        employeeService.update(employeeDTO);
        return Result.success();
    }

    // 测试
    @GetMapping
    public Result<PageResult> test(){
        log.info("测试");
        return Result.success();
    }
}
