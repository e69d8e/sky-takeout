package com.li.aspect;

import com.li.annotation.AutoFill;
import com.li.common.enumeration.OperationType;
import com.li.common.properties.JwtProperties;
import com.li.common.utils.JwtUtil;
import com.li.common.utils.ThreadLocalUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.li.common.constant.JwtClaimsConstant;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

// 自定义切面类
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    @Autowired
    private ThreadLocalUtil threadLocal;
    @Autowired
    private JwtProperties jwtProperties;

    // 切入点
    @Pointcut("execution(* com.li.mapper.*.*(..)) && @annotation(com.li.annotation.AutoFill)")
    public void pointCut() {
    }


    @Before("pointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始进行公共字段自动填充...");
        Object[] args = joinPoint.getArgs();
        Object o = args[0];
        if (args == null || args.length == 0) {
            return;
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature(); // 获取当前方法签名
        // 获取当前被拦截的方法上的注解对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        // 获取操作类型
        OperationType value = autoFill.value();
        LocalDateTime now = LocalDateTime.now();
        Long id = null;
        try {
            if (threadLocal.get() != null) {
                Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), threadLocal.get().toString());
                if (claims.get(JwtClaimsConstant.EMP_ID) != null) {
                    id = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());
                }
            }
        } catch (Exception e) {
            log.warn("公共字段自动填充获取用户id失败: {}", e.getMessage());
        }
        log.info("当前操作人id为：{} 操作类型{}", id, value);
        // 判断操作类型
        if (value == OperationType.INSERT) {
            try {
                // 反射
                Method setCreateUser = o.getClass().getDeclaredMethod("setCreateUser", Long.class);
                Method setCreateTime = o.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class);
                Method setUpdateUser = o.getClass().getDeclaredMethod("setUpdateUser", Long.class);
                Method setUpdateTime = o.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);

                setCreateUser.invoke(o, id);
                setCreateTime.invoke(o, now);
                setUpdateUser.invoke(o, id);
                setUpdateTime.invoke(o, now);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (value == OperationType.UPDATE) {
            try {
                Method setUpdateUser = o.getClass().getDeclaredMethod("setUpdateUser", Long.class);
                Method setUpdateTime = o.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
                setUpdateUser.invoke(o, id);
                setUpdateTime.invoke(o, now);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
}
