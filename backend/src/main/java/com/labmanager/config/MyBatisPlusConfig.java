package com.labmanager.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.labmanager.mapper")
public class MyBatisPlusConfig {
}
