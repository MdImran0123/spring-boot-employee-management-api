package com.codemyth.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codemyth.dto.EmployeePatchRequest;
import com.codemyth.dto.EmployeeRequest;
import com.codemyth.dto.EmployeeResponse;
import com.codemyth.dto.PageResponse;
import com.codemyth.service.EmployeeService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Employees", description = "Employee CRUD, search, and soft delete")
public class EmployeeController {

	private final EmployeeService employeeService;

	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	@Operation(summary = "Create employee", security = @SecurityRequirement(name = "bearerAuth"))
	@PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
	@PostMapping("/employees")
	public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
		EmployeeResponse employeeDetails = employeeService.createEmployee(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(employeeDetails);
	}

	@Operation(summary = "List or search employees (paginated)", security = @SecurityRequirement(name = "bearerAuth"))
	@PreAuthorize("hasAnyRole('VIEWER','EDITOR','ADMIN')")
	@GetMapping("/employees")
	public ResponseEntity<PageResponse<EmployeeResponse>> getAllEmployees(@RequestParam(required = false) String name,
			@RequestParam(required = false) String city, @RequestParam(required = false) Integer age,
			@RequestParam(required = false) BigDecimal salary,
			@PageableDefault(size = 20, sort = "empId") Pageable pageable) {
		if (pageable.getSort().stream().anyMatch(order -> order.getProperty().contains("["))) {
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("empId").ascending());
		}
		PageResponse<EmployeeResponse> empPage = employeeService.getAllEmployees(name, city, age, salary, pageable);
		return ResponseEntity.ok(empPage);
	}

	@Operation(summary = "Get employee by id", security = @SecurityRequirement(name = "bearerAuth"))
	@PreAuthorize("hasAnyRole('VIEWER','EDITOR','ADMIN')")
	@GetMapping("/employees/{empId}")
	public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long empId) {
		EmployeeResponse emp = employeeService.getEmployeeById(empId);
		return ResponseEntity.ok(emp);

	}

	@Operation(summary = "Replace employee by id", security = @SecurityRequirement(name = "bearerAuth"))
	@PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
	@PutMapping("/employees/{empId}")
	public ResponseEntity<EmployeeResponse> updateEmployeeById(@PathVariable Long empId,
			@Valid @RequestBody EmployeeRequest request) {
		EmployeeResponse empDetails = employeeService.updateEmployee(empId, request);
		return ResponseEntity.ok(empDetails);
	}

	@Operation(summary = "Partial update employee by id", security = @SecurityRequirement(name = "bearerAuth"))
	@PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
	@PatchMapping("/employees/{empId}")
	public ResponseEntity<EmployeeResponse> patchEmployeeById(@PathVariable Long empId,
			@Valid @RequestBody EmployeePatchRequest request) {
		return ResponseEntity.ok(employeeService.patchEmployee(empId, request));
	}

	@Operation(summary = "Soft delete employee by id", security = @SecurityRequirement(name = "bearerAuth"))
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/employees/{empId}")
	public ResponseEntity<Void> deleteById(@PathVariable Long empId) {
		employeeService.deleteById(empId);
		return ResponseEntity.noContent().build();

	}

	@Operation(summary = "Soft delete all employees", security = @SecurityRequirement(name = "bearerAuth"))
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/employees")
	public ResponseEntity<Void> deleteAllEmployees() {
		employeeService.deleteAllEmployees();
		return ResponseEntity.noContent().build();
	}

	/**
	 * @deprecated Use {@code GET /employees?city=} instead. Will be removed in a
	 *             later release.
	 */
	@Deprecated
	@PreAuthorize("hasAnyRole('VIEWER','EDITOR','ADMIN')")
	@GetMapping("/employees/city/{empCity}")
	public ResponseEntity<List<EmployeeResponse>> getEmployeeByCity(@PathVariable String empCity) {
		List<EmployeeResponse> employeeList = employeeService.getEmployeeByCity(empCity);
		return ResponseEntity.ok(employeeList);
	}

	/**
	 * @deprecated Use {@code GET /employees?age=} instead. Will be removed in a
	 *             later release.
	 */
	@Deprecated
	@PreAuthorize("hasAnyRole('VIEWER','EDITOR','ADMIN')")
	@GetMapping("/employees/age/{empAge}")
	public ResponseEntity<List<EmployeeResponse>> getEmployeeByAge(@PathVariable int empAge) {
		List<EmployeeResponse> employeeList = employeeService.getEmployeeByAge(empAge);
		return ResponseEntity.ok(employeeList);

	}

	/**
	 * @deprecated Use {@code GET /employees?salary=} instead. Will be removed in a
	 *             later release.
	 */
	@Deprecated
	@PreAuthorize("hasAnyRole('VIEWER','EDITOR','ADMIN')")
	@GetMapping("/employees/salary/{empSalary}")
	public ResponseEntity<List<EmployeeResponse>> getEmployeeBySalary(@PathVariable BigDecimal empSalary) {
		List<EmployeeResponse> employeeSalary = employeeService.getEmployeeBySalary(empSalary);
		return ResponseEntity.ok(employeeSalary);

	}

	/**
	 * @deprecated Use {@code GET /employees?name=} instead. Will be removed in a
	 *             later release.
	 */
	@Deprecated
	@PreAuthorize("hasAnyRole('VIEWER','EDITOR','ADMIN')")
	@GetMapping("/employees/name/{empName}")
	public ResponseEntity<List<EmployeeResponse>> getEmployeesByName(@PathVariable String empName) {
		List<EmployeeResponse> employeeName = employeeService.getEmployeeByName(empName);
		return ResponseEntity.ok(employeeName);

	}

}
