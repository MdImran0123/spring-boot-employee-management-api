package com.codemyth.dto;

import java.math.BigDecimal;

public class EmployeeResponse {

	private Long empId;
	private String empName;
	private int empAge;
	private String empCity;
	private BigDecimal empSalary;

	public EmployeeResponse() {
	}

	public EmployeeResponse(Long empId, String empName, int empAge, String empCity, BigDecimal empSalary) {
		this.empId = empId;
		this.empName = empName;
		this.empAge = empAge;
		this.empCity = empCity;
		this.empSalary = empSalary;
	}

	public Long getEmpId() {
		return empId;
	}

	public void setEmpId(Long empId) {
		this.empId = empId;
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
		return "EmployeeResponse{" + "empId=" + empId + ", empName='" + empName + '\'' + ", empAge=" + empAge
				+ ", empCity='" + empCity + '\'' + ", empSalary=" + empSalary + '}';
	}
}