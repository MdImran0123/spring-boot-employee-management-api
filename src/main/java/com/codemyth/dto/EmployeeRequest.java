package com.codemyth.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public class EmployeeRequest {
	
	@NotBlank(message = "Employee name cannot be blank")
	private String emp_name;
	
	@Min(value = 18, message = "Employee age must be at least 18")
	@Max(value = 65, message = "Employee age must not exceed 65")
	private int emp_age;
	
	@NotBlank(message = "Employee city cannot be blank")
	private String emp_city;
	
	@PositiveOrZero(message = "Employee salary cannot be negative")
	private float emp_salary;
	
	public EmployeeRequest() {
		
	}
	
	public EmployeeRequest(String emp_name, int emp_age, String emp_city, float emp_salary) {
		this.emp_age = emp_age;
		this.emp_city = emp_city;
		this.emp_name = emp_name;
		this.emp_salary = emp_salary;
		
	}
	
	public int getEmp_age() {
		return emp_age;
	}
	public void setEmp_age(int emp_age) {
		this.emp_age = emp_age;
	}
	
	public String getEmp_city() {
		return emp_city;
	}
	public void setEmp_city(String emp_city) {
		this.emp_city = emp_city;
	}
	
	public String getEmp_name() {
		return emp_name;
	}

	public void setEmp_name(String emp_name) {
		this.emp_name = emp_name;
	}
	
	public float getEmp_salary() {
		return emp_salary;
	}
	public void setEmp_salary(float emp_salary) {
		this.emp_salary = emp_salary;
	}
	
	@Override
	public String toString() {
		return "EmployeeRequest [emp_name=" + emp_name + ", emp_age=" + emp_age + ", emp_city=" + emp_city
				+ ", emp_salary=" + emp_salary + "]";
	}
	
	
}
