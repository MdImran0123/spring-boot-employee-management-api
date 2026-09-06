package com.codemyth.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codemyth.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	List<Employee> findByEmpCityContainingIgnoreCase(String empCity);

	List<Employee> findByEmpAge(int empAge);

	List<Employee> findByEmpSalaryBetween(BigDecimal min, BigDecimal max);

	List<Employee> findByEmpNameContainingIgnoreCase(String empName);
}