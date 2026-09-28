package com.codemyth.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.codemyth.dto.EmployeeRequest;
import com.codemyth.dto.EmployeeResponse;
import com.codemyth.dto.PageResponse;
import com.codemyth.exception.EmployeeNotFoundException;
import com.codemyth.model.Employee;
import com.codemyth.repository.EmployeeRepository;
import com.codemyth.repository.EmployeeSpecifications;

@Service
public class EmployeeService {
	private final EmployeeRepository employeeRepository;

	public EmployeeService(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	// Create Employee
	public EmployeeResponse createEmployee(EmployeeRequest request) {
		Employee employee = new Employee();

		employee.setEmpName(request.getEmpName());
		employee.setEmpAge(request.getEmpAge());
		employee.setEmpCity(request.getEmpCity());
		employee.setEmpSalary(request.getEmpSalary());

		Employee savedEmployee = employeeRepository.save(employee);

		return mapToResponse(savedEmployee);
	}

	// Get employees (paginated + optional filters)
	public PageResponse<EmployeeResponse> getAllEmployees(String name, String city, Integer age, BigDecimal salary,
			Pageable pageable) {
		Page<Employee> page = employeeRepository.findAll(EmployeeSpecifications.withFilters(name, city, age, salary),
				pageable);
		List<EmployeeResponse> content = page.getContent().stream().map(this::mapToResponse).toList();
		return new PageResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(),
				page.getTotalPages());
	}

	// Get Employee by Id
	public EmployeeResponse getEmployeeById(Long empId) {
		Employee employee = employeeRepository.findById(empId)
				.orElseThrow(() -> new EmployeeNotFoundException("No employee found by Id: " + empId));
		return mapToResponse(employee);

	}

	// Update Employee Details
	public EmployeeResponse updateEmployee(Long empId, EmployeeRequest request) {
		Employee employee = employeeRepository.findById(empId)
				.orElseThrow(() -> new EmployeeNotFoundException("No employee found by Id: " + empId));
		employee.setEmpName(request.getEmpName());
		employee.setEmpAge(request.getEmpAge());
		employee.setEmpCity(request.getEmpCity());
		employee.setEmpSalary(request.getEmpSalary());
		Employee employeeDetail = employeeRepository.save(employee);

		return mapToResponse(employeeDetail);

	}

	// Delete Employee By Id
	public void deleteById(Long empId) {
		if (!employeeRepository.existsById(empId)) {
			throw new EmployeeNotFoundException("No employee found by Id: " + empId);
		}
		employeeRepository.deleteById(empId);
	}

	// Delete All Employee Details
	public void deleteAllEmployees() {
		employeeRepository.deleteAll();
	}

	// Get Employee Details By City
	public List<EmployeeResponse> getEmployeeByCity(String city) {
		return employeeRepository.findByEmpCityContainingIgnoreCase(city).stream().map(this::mapToResponse).toList();
	}

	// Get Employee Details By Age
	public List<EmployeeResponse> getEmployeeByAge(int empAge) {
		return employeeRepository.findByEmpAge(empAge).stream().map(this::mapToResponse).toList();
	}

	// Get Employee Details By Salary
	public List<EmployeeResponse> getEmployeeBySalary(BigDecimal empSalary) {
		BigDecimal min = empSalary.setScale(2, java.math.RoundingMode.HALF_UP);
		BigDecimal max = min;
		return employeeRepository.findByEmpSalaryBetween(min, max).stream().map(this::mapToResponse).toList();
	}

	// Get Employee Details By Name
	public List<EmployeeResponse> getEmployeeByName(String empName) {
		return employeeRepository.findByEmpNameContainingIgnoreCase(empName).stream().map(this::mapToResponse).toList();
	}

	// Entity -> Response DTO
	private EmployeeResponse mapToResponse(Employee employee) {

		return new EmployeeResponse(employee.getEmpId(), employee.getEmpName(), employee.getEmpAge(),
				employee.getEmpCity(), employee.getEmpSalary());
	}
}
