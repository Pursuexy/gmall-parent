package com.itcast.gmall.portal;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

// @EnableConfigurationProperties
@EnableDubbo
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class GmallShopPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallShopPortalApplication.class, args);
	}

}
