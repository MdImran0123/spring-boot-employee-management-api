package com.codemyth.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;

public record EmployeePatchRequest(
		String empName,

		@Min(value = 18, message = "Employee age must be at least 18")
		@Max(value = 65, message = "Employee age must not exceed 65")
		Integer empAge,

		String empCity,

		@PositiveOrZero(message = "Employee salary cannot be negative")
		BigDecimal empSalary) {
}
