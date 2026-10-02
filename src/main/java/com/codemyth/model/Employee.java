package com.codemyth.model;

import java.math.BigDecimal;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "employee")
@SQLRestriction("deleted = false")
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long empId;

	@Column(name = "emp_name", nullable = false)
	private String empName;

	@Column(name = "emp_salary", nullable = false, precision = 10, scale = 2)
	private BigDecimal empSalary;

	@Column(name = "emp_age", nullable = false)
	private int empAge;

	@Column(name = "emp_city", nullable = false)
	private String empCity;

	@Column(nullable = false)
	private boolean deleted = false;

	public Employee() {
	}

	public Employee(Long empId, String empName, BigDecimal empSalary, int empAge, String empCity) {
		this.empId = empId;
		this.empName = empName;
		this.empSalary = empSalary;
		this.empAge = empAge;
		this.empCity = empCity;
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

	public BigDecimal getEmpSalary() {
		return empSalary;
	}

	public void setEmpSalary(BigDecimal empSalary) {
		this.empSalary = empSalary;
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public String toString() {
		return "Employee{" + "empId=" + empId + ", empName='" + empName + '\'' + ", empSalary=" + empSalary
				+ ", empAge=" + empAge + ", empCity='" + empCity + '\'' + '}';
	}
}