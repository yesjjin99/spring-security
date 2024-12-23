package com.cos.spring_security1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringSecurity1Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurity1Application.class, args);
	}

}
