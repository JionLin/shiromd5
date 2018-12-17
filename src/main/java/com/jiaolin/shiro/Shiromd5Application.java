package com.jiaolin.shiro;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@MapperScan("com.jiaolin.shiro.mapper")
@ComponentScan
@SpringBootApplication
public class Shiromd5Application {

	public static void main(String[] args) {
		SpringApplication.run(Shiromd5Application.class, args);
	}

}

