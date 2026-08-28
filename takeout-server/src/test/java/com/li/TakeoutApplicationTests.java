package com.li;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
class TakeoutApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        String password = "123456";

        try {
            // 获取MessageDigest实例，指定算法为MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 将字符串转换为字节数组并更新到MessageDigest对象中
            md.update(password.getBytes());
            // 完成哈希计算，得到字节数组
            byte[] digest = md.digest();
            // 将字节数组转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            System.out.println(sb.toString());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不支持", e);

        }
    }

    @Test
    public void test(){
        System.out.println("hello world");
    }

    @Test
    public void test1(){
        ValueOperations operations = redisTemplate.opsForValue();
        operations.set("name","lisi");
        System.out.println(operations.get("name"));
    }



}
