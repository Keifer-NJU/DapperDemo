package com.ae86.mypractice01.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.ae86.mypractice01.mbg.mapper")
public class MyBatisConfig {
}
