package com.codemyth.service;

import org.springframework.stereotype.Service;

import com.codemyth.repository.EmployeeRepository;

@Service
public class EmployeeService {
	private final EmployeeRepository employeeRepository;

	public EmployeeService(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

}
