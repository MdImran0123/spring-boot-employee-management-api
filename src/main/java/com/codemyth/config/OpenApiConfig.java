package com.codemyth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI employeeOpenAPI() {
		return new OpenAPI().info(new Info().title("Employee Management API").description(
				"REST API for creating, updating, searching, and soft-deleting employees.")
				.version("v1"));
	}
}
