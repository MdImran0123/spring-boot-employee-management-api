package com.codemyth.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.codemyth.dto.EmployeeRequest;
import com.codemyth.dto.EmployeeResponse;
import com.codemyth.model.Employee;
import com.codemyth.repository.EmployeeRepository;

@Service
public class EmployeeService {
	private final EmployeeRepository employeeRepository;

	public EmployeeService(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	// Create Employee
	public EmployeeResponse createEmployee(EmployeeRequest request) {
		Employee employee = new Employee();

		employee.setEmp_name(request.getEmp_name());
		employee.setEmp_age(request.getEmp_age());
		employee.setEmp_city(request.getEmp_city());
		employee.setEmp_salary(request.getEmp_salary());

		Employee savedEmployee = employeeRepository.save(employee);

		return mapToResponse(savedEmployee);
	}

	// Get All Employees
	public List<EmployeeResponse> getAllEmployees() {
		return employeeRepository.findAll()
				.stream()
				.map(this::mapToResponse)
				.toList();

	}

	// Entity -> Response DTO
	private EmployeeResponse mapToResponse(Employee employee) {

		return new EmployeeResponse(employee.getEmp_id(), employee.getEmp_name(), employee.getEmp_age(),
				employee.getEmp_city(), employee.getEmp_salary());
	}
}
