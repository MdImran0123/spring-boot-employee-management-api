package com.codemyth.config;

import java.util.List;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.StringSchema;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI employeeOpenAPI() {
		return new OpenAPI().info(new Info().title("Employee Management API")
				.description("REST API for creating, updating, searching, and soft-deleting employees.").version("v1"));
	}

	@Bean
	OperationCustomizer sortExampleCustomizer() {
		return (operation, handlerMethod) -> {
			if (operation.getParameters() == null) {
				return operation;
			}
			operation.getParameters().stream().filter(parameter -> "sort".equals(parameter.getName()))
					.forEach(parameter -> {
						StringSchema item = new StringSchema();
						item.example("empId,asc");

						ArraySchema schema = new ArraySchema();
						schema.setItems(item);
						schema.example(List.of("empId,asc"));

						parameter.setSchema(schema);
						parameter.setExample(null);
						parameter.setDescription(
								"Format: property,asc|desc. Example: empId,asc. Repeat the parameter to sort by more than one field.");
					});
			return operation;
		};
	}
}
