package com.codemyth.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record EmployeeRequest(
		@NotBlank(message = "Employee name cannot be blank") String empName,

		@Min(value = 18, message = "Employee age must be at least 18")
		@Max(value = 65, message = "Employee age must not exceed 65") int empAge,

		@NotBlank(message = "Employee city cannot be blank") String empCity,

		@NotNull(message = "Employee salary cannot be null")
		@PositiveOrZero(message = "Employee salary cannot be negative") BigDecimal empSalary) {
}
