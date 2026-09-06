package com.codemyth.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codemyth.dto.EmployeeRequest;
import com.codemyth.dto.EmployeeResponse;
import com.codemyth.service.EmployeeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

	private final EmployeeService employeeService;

	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	@PostMapping("/employees")
	public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
		EmployeeResponse employeeDetails = employeeService.createEmployee(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(employeeDetails);
	}

	@GetMapping("/employees")
	public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
		List<EmployeeResponse> empList = employeeService.getAllEmployees();

		return new ResponseEntity<List<EmployeeResponse>>(empList, HttpStatus.OK);

	}

	@GetMapping("/employees/{empId}")
	public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long empId) {
		EmployeeResponse emp = employeeService.getEmployeeById(empId);
		return ResponseEntity.ok(emp);

	}

	@PutMapping("/employees/{empId}")
	public ResponseEntity<EmployeeResponse> updateEmployeeById(@PathVariable Long empId, @Valid @RequestBody EmployeeRequest request) {
		EmployeeResponse empDetails = employeeService.updateEmployee(empId, request);
		return ResponseEntity.ok(empDetails);
	}

	@DeleteMapping("/employees/{empId}")
	public ResponseEntity<Void> deleteById(@PathVariable Long empId) {
		employeeService.deleteById(empId);
		return ResponseEntity.noContent().build();

	}

	@DeleteMapping("/employees")
	public ResponseEntity<Void> deleteAllEmployees() {
		employeeService.deleteAllEmployees();
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/employees/city/{empCity}")
	public ResponseEntity<List<EmployeeResponse>> getEmployeeByCity(@PathVariable String empCity) {
		List<EmployeeResponse> employeeList = employeeService.getEmployeeByCity(empCity);
		return ResponseEntity.ok(employeeList);
	}

	@GetMapping("/employees/age/{empAge}")
	public ResponseEntity<List<EmployeeResponse>> getEmployeeByAge(@PathVariable int empAge) {
		List<EmployeeResponse> employeeList = employeeService.getEmployeeByAge(empAge);
		return ResponseEntity.ok(employeeList);

	}

	@GetMapping("/employees/salary/{empSalary}")
	public ResponseEntity<List<EmployeeResponse>> getEmployeeBySalary(@PathVariable BigDecimal empSalary) {
		List<EmployeeResponse> employeeSalary = employeeService.getEmployeeBySalary(empSalary);
		return ResponseEntity.ok(employeeSalary);

	}

	@GetMapping("/employees/name/{empName}")
	public ResponseEntity<List<EmployeeResponse>> getEmployeesByName(@PathVariable String empName) {
		List<EmployeeResponse> employeeName = employeeService.getEmployeeByName(empName);
		return ResponseEntity.ok(employeeName);

	}

}
