package com.codemyth.dto;

public class EmployeeResponse {
	private Long emp_id;
	private String emp_name;
	private int emp_age;
	private String emp_city;
	private float emp_salary;

	public EmployeeResponse() {

	}

	public EmployeeResponse(Long emp_id, String emp_name, int emp_age, String emp_city, float emp_salary) {
		this.emp_id = emp_id;
		this.emp_age = emp_age;
		this.emp_city = emp_city;
		this.emp_name = emp_name;
		this.emp_salary = emp_salary;

	}

	public Long getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(Long emp_id) {
		this.emp_id = emp_id;
	}

	public String getEmp_name() {
		return emp_name;
	}

	public void setEmp_name(String emp_name) {
		this.emp_name = emp_name;
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

	public float getEmp_salary() {
		return emp_salary;
	}

	public void setEmp_salary(float emp_salary) {
		this.emp_salary = emp_salary;
	}

	@Override
	public String toString() {
		return "EmployeeResponse [emp_id=" + emp_id + ", emp_name=" + emp_name + ", emp_age=" + emp_age + ", emp_city="
				+ emp_city + ", emp_salary=" + emp_salary + "]";
	}
}
