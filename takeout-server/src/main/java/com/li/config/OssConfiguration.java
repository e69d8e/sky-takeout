package com.li.config;

import com.li.common.properties.AliOssProperties;
import com.li.common.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/*
*  oss配置类
 */
@Configuration
@Slf4j
public class OssConfiguration {
    @Bean // bean管理
    @ConditionalOnMissingBean // 如果不存在这个bean，就创建
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
        log.info("开始创建阿里云OSS客户端对象，配置信息如下：{}",aliOssProperties);
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }
}
