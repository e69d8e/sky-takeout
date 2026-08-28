package com.li.interceptor;

import com.li.common.constant.JwtClaimsConstant;
import com.li.common.constant.MessageConstant;
import com.li.common.exception.UserNotLoginException;
import com.li.common.properties.JwtProperties;
import com.li.common.utils.JwtUtil;
import com.li.common.utils.ThreadLocalUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private ThreadLocalUtil threadLocal;

    @Autowired
    private RedisTemplate redisTemplate;

    public JwtTokenUserInterceptor() {
        log.info("JwtTokenUserInterceptor 初始化");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            log.info("拦截到的不是动态方法，直接放行");
            return true;
        }
        log.info("拦截到动态方法，开始校验JWT");

        String token = request.getHeader(jwtProperties.getUserTokenName());
        log.info("当前请求头中的令牌为: {}", token);
        try {
            // 解析JWT
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            log.info("当前用户id: {}", userId);
            if (redisTemplate.opsForValue().get(token) == null) {
                log.info("Redis中未找到token，拒绝访问");
                response.setStatus(401);
                throw new UserNotLoginException(MessageConstant.USER_NOT_LOGIN);
            }
            log.info("JWT校验通过，放行请求");
            threadLocal.set(token); // 将token放入threadLocal
            return true;
        } catch (Exception ex) {
            log.error("JWT校验失败: {}", ex.getMessage());
            response.setStatus(401);
            throw new UserNotLoginException(MessageConstant.USER_NOT_LOGIN);
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        threadLocal.remove();
        log.info("拦截器完成，清除ThreadLocal数据");
    }

//    // 拦截
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        log.info("拦截器开始运行");
////        if (!(handler instanceof HandlerMethod)) {
////            log.info("拦截到的不是动态方法，直接放行");
////            return true;
////        }
//        log.info("拦截到动态方法，开始校验JWT");
//
//        String token = request.getHeader(jwtProperties.getAdminTokenName());
//        threadLocal.set(token); // 将token放入threadLocal
//        try {
//            log.info("jwt校验: {}", token);
//            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
//            Long empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());
//            log.info("当前员工id: {}", empId);
//            if (redisTemplate.opsForValue().get(request.getHeader("token")) == null) {
//                log.info("Redis中未找到token，拒绝访问");
//                return false;
//            }
//            log.info("JWT校验通过，放行请求");
//            return true;
//        } catch (Exception ex) {
//            log.error("JWT校验失败: {}", ex.getMessage());
//            response.setStatus(401);
//            return false;
//        }
//    }
//
//
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//
//    }
}


