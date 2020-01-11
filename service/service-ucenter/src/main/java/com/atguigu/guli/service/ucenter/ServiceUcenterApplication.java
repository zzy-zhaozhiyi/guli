package com.atguigu.guli.service.ucenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zzy
 * @create 2020-01-09 17:59
 */
@SpringBootApplication
@ComponentScan({"com.atguigu.guli"})
public class ServiceUcenterApplication {

    public static void main(String[] args) {

        SpringApplication.run(ServiceUcenterApplication.class, args);
    }
}