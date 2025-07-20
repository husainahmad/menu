package com.harmoni.pos;

import io.github.weasleyj.mybatis.encrypt.annotation.EnableMybatisEncryption;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

/**
 * Main class for launching the Harmoni POS Menu microservice application.
 *
 * <p>This application excludes the default Spring Security auto-configurations:
 * <ul>
 *     <li>{@link SecurityAutoConfiguration}</li>
 *     <li>{@link UserDetailsServiceAutoConfiguration}</li>
 * </ul>
 *
 * <p>It also enables MyBatis encryption via the {@link EnableMybatisEncryption} annotation.
 */
@EnableMybatisEncryption
@SpringBootApplication(exclude = {
		SecurityAutoConfiguration.class,
		UserDetailsServiceAutoConfiguration.class
})
public class MenuApplication {

	/**
	 * Entry point of the Menu microservice application.
	 *
	 * @param args command-line arguments passed at application startup
	 */
	public static void main(String[] args) {
		SpringApplication.run(MenuApplication.class, args);
	}
}
