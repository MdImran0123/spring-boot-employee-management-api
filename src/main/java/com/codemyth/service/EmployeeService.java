package com.codemyth.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

	// Get Employee by Id
	public EmployeeResponse getEmployeeById(Long empId){
		Employee employee = employeeRepository.findById(empId)
				.orElseThrow(() -> 
				new RuntimeException(
						"Employee not found with Id: "+ empId
						)
				);
		return mapToResponse(employee);
				
	}
	
	//Update Employee Details
	public EmployeeResponse updateEmployee(Long empId, Employee request) {
		Employee employee = employeeRepository.findById(empId)
				.orElseThrow(() -> 
				new RuntimeException("Employee not found with Id: " + empId
						)
				);
		employee.setEmp_name(employee.getEmp_name());
		employee.setEmp_age(employee.getEmp_age());
		employee.setEmp_city(employee.getEmp_city());
		employee.setEmp_salary(employee.getEmp_salary());
		Employee employeeDetail = employeeRepository.save(employee);

		return mapToResponse(employeeDetail);
		
	}

	// Entity -> Response DTO
	private EmployeeResponse mapToResponse(Employee employee) {

		return new EmployeeResponse(employee.getEmp_id(), employee.getEmp_name(), employee.getEmp_age(),
				employee.getEmp_city(), employee.getEmp_salary());
	}
}
