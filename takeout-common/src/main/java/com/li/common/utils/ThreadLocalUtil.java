package com.li.common.utils;

import org.springframework.stereotype.Component;

@Component
public class ThreadLocalUtil<T> {
    // 线程本地存储
    private static final ThreadLocal THREAD_LOCAL = new ThreadLocal<>();
    // 设置值
    public void set(Object value){
        THREAD_LOCAL.set(value);
    }
    // 获取值
    public <T> T get(){
        return (T) THREAD_LOCAL.get();
    }
    // 移除 防止内存泄漏
    public void remove(){
        THREAD_LOCAL.remove();
    }

}
