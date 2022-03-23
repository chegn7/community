package com.c.community.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
public class WkConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(WkConfig.class);

    @Value("${wk.image.storage}")
    String wkImageStoragePath;

    @PostConstruct
    public void init() {
        // 创建文件目录
        File file = new File(wkImageStoragePath);
        if (!file.exists()) {
            file.mkdir();
            LOGGER.info("创建wk图片目录" + wkImageStoragePath);
        }
    }
}
