package com.codemyth.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.codemyth.dto.EmployeePatchRequest;
import com.codemyth.dto.EmployeeRequest;
import com.codemyth.dto.EmployeeResponse;
import com.codemyth.dto.PageResponse;
import com.codemyth.exception.EmployeeNotFoundException;
import com.codemyth.mapper.EmployeeMapper;
import com.codemyth.model.Employee;
import com.codemyth.repository.EmployeeRepository;
import com.codemyth.repository.EmployeeSpecifications;

@Service
public class EmployeeService {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeService.class);

	private final EmployeeRepository employeeRepository;
	private final EmployeeMapper employeeMapper;

	public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
		this.employeeRepository = employeeRepository;
		this.employeeMapper = employeeMapper;
	}

	// Create Employee
	public EmployeeResponse createEmployee(EmployeeRequest request) {
		Employee employee = employeeMapper.toEntity(request);
		Employee savedEmployee = employeeRepository.save(employee);
		LOGGER.info("Created employee with id={}", savedEmployee.getEmpId());
		return employeeMapper.toResponse(savedEmployee);
	}

	// Get employees (paginated + optional filters)
	public PageResponse<EmployeeResponse> getAllEmployees(String name, String city, Integer age, BigDecimal salary,
			Pageable pageable) {
		Page<Employee> page = employeeRepository.findAll(EmployeeSpecifications.withFilters(name, city, age, salary),
				pageable);
		List<EmployeeResponse> content = page.getContent().stream().map(employeeMapper::toResponse).toList();
		return new PageResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(),
				page.getTotalPages());
	}

	// Get Employee by Id
	public EmployeeResponse getEmployeeById(Long empId) {
		Employee employee = employeeRepository.findById(empId)
				.orElseThrow(() -> new EmployeeNotFoundException("No employee found by Id: " + empId));
		return employeeMapper.toResponse(employee);

	}

	// Update Employee Details
	public EmployeeResponse updateEmployee(Long empId, EmployeeRequest request) {
		Employee employee = employeeRepository.findById(empId)
				.orElseThrow(() -> new EmployeeNotFoundException("No employee found by Id: " + empId));
		employeeMapper.updateEntity(request, employee);
		Employee employeeDetail = employeeRepository.save(employee);
		LOGGER.info("Updated employee with id={}", empId);
		return employeeMapper.toResponse(employeeDetail);

	}

	// Partial update
	public EmployeeResponse patchEmployee(Long empId, EmployeePatchRequest request) {
		Employee employee = employeeRepository.findById(empId)
				.orElseThrow(() -> new EmployeeNotFoundException("No employee found by Id: " + empId));
		employeeMapper.patchEntity(request, employee);
		Employee saved = employeeRepository.save(employee);
		LOGGER.info("Patched employee with id={}", empId);
		return employeeMapper.toResponse(saved);
	}

	// Soft delete by id
	public void deleteById(Long empId) {
		Employee employee = employeeRepository.findById(empId)
				.orElseThrow(() -> new EmployeeNotFoundException("No employee found by Id: " + empId));
		employee.setDeleted(true);
		employeeRepository.save(employee);
		LOGGER.info("Soft-deleted employee with id={}", empId);
	}

	// Soft delete all visible employees
	public void deleteAllEmployees() {
		List<Employee> employees = employeeRepository.findAll();
		employees.forEach(employee -> employee.setDeleted(true));
		employeeRepository.saveAll(employees);
		LOGGER.info("Soft-deleted all employees, count={}", employees.size());
	}

	// Get Employee Details By City
	public List<EmployeeResponse> getEmployeeByCity(String city) {
		return employeeRepository.findByEmpCityContainingIgnoreCase(city).stream().map(employeeMapper::toResponse)
				.toList();
	}

	// Get Employee Details By Age
	public List<EmployeeResponse> getEmployeeByAge(int empAge) {
		return employeeRepository.findByEmpAge(empAge).stream().map(employeeMapper::toResponse).toList();
	}

	// Get Employee Details By Salary
	public List<EmployeeResponse> getEmployeeBySalary(BigDecimal empSalary) {
		BigDecimal min = empSalary.setScale(2, java.math.RoundingMode.HALF_UP);
		BigDecimal max = min;
		return employeeRepository.findByEmpSalaryBetween(min, max).stream().map(employeeMapper::toResponse).toList();
	}

	// Get Employee Details By Name
	public List<EmployeeResponse> getEmployeeByName(String empName) {
		return employeeRepository.findByEmpNameContainingIgnoreCase(empName).stream().map(employeeMapper::toResponse)
				.toList();
	}
}
