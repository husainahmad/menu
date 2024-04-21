package com.harmoni.pos;

import io.github.weasleyj.mybatis.encrypt.annotation.EnableMybatisEncryption;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;

@EnableMybatisEncryption
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class,
		UserDetailsServiceAutoConfiguration.class})
public class MenuApplication {

	public static void main(String[] args) {
		SpringApplication.run(MenuApplication.class, args);
	}

}
