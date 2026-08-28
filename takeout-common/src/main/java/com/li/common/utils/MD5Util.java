package com.li.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



// MD5加密
public class MD5Util {
    // 私有构造方法，防止实例化
    private MD5Util() {
    }

    public static String encryptToMD5(String input) {
        try {
            // 获取MessageDigest实例，指定算法为MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 将字符串转换为字节数组并更新到MessageDigest对象中
            md.update(input.getBytes());
            // 完成哈希计算，得到字节数组
            byte[] digest = md.digest();
            // 将字节数组转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不支持", e);

        }
    }
}