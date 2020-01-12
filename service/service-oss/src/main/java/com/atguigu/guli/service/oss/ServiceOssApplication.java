package com.atguigu.guli.service.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author helen
 * @since 2019/12/30
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//排除数据源，不然会让你配置
@ComponentScan({"com.atguigu.guli"})
public class ServiceOssApplication {

    public static void main(String[] args) {

        SpringApplication.run(ServiceOssApplication.class, args);
    }
}
