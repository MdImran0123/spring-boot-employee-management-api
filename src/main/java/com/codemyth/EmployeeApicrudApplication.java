package com.codemyth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.codemyth.config.AppSecurityProperties;
import com.codemyth.config.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties({ JwtProperties.class, AppSecurityProperties.class })
public class EmployeeApicrudApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeApicrudApplication.class, args);
	}

}
