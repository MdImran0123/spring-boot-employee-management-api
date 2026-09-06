package com.codemyth.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class EmployeeRequest {

	@NotBlank(message = "Employee name cannot be blank")
	private String empName;

	@Min(value = 18, message = "Employee age must be at least 18")
	@Max(value = 65, message = "Employee age must not exceed 65")
	private int empAge;

	@NotBlank(message = "Employee city cannot be blank")
	private String empCity;
	
	@NotNull(message = "Employee salary cannot be null")
	@PositiveOrZero(message = "Employee salary cannot be negative")
	private BigDecimal empSalary;

	public EmployeeRequest() {
	}

	public EmployeeRequest(String empName, int empAge, String empCity, BigDecimal empSalary) {
		this.empName = empName;
		this.empAge = empAge;
		this.empCity = empCity;
		this.empSalary = empSalary;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public int getEmpAge() {
		return empAge;
	}

	public void setEmpAge(int empAge) {
		this.empAge = empAge;
	}

	public String getEmpCity() {
		return empCity;
	}

	public void setEmpCity(String empCity) {
		this.empCity = empCity;
	}

	public BigDecimal getEmpSalary() {
		return empSalary;
	}

	public void setEmpSalary(BigDecimal empSalary) {
		this.empSalary = empSalary;
	}

	@Override
	public String toString() {
		return "EmployeeRequest [empName=" + empName + ", empAge=" + empAge + ", empCity=" + empCity + ", empSalary="
				+ empSalary + "]";
	}
}