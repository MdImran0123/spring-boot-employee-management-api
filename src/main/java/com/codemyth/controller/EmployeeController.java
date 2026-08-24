package com.codemyth.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.codemyth.model.Employee;
import com.codemyth.repository.EmployeeRepository;
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
	public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest employee) {
		EmployeeResponse employeeDetails = employeeService.createEmployee(employee);
		return ResponseEntity.status(HttpStatus.CREATED).body(employeeDetails);
	}

	@GetMapping("/employees")
	public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
		List<EmployeeResponse> empList = employeeService.getAllEmployees();

		return new ResponseEntity<List<EmployeeResponse>>(empList, HttpStatus.OK);

	}

	@GetMapping("/employees/{empid}")
	public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long empid) {
		EmployeeResponse emp = employeeService.getEmployeeById(empid);
		return ResponseEntity.ok(emp);

	}

	@PutMapping("/employees/{empid}")
	public ResponseEntity<?> updateEmployee(@PathVariable long empid, @Valid @RequestBody Employee employee) {
		EmployeeResponse empDetails = employeeService.updateEmployee(empid, employee);
			return ResponseEntity.ok(empDetails);
	}

	@DeleteMapping("/employees/{empid}")
	public String deleteEmployeeById(@PathVariable long empid) {
		Optional<Employee> empDetails = employeeRepository.findById(empid);
		if (empDetails.isPresent()) {
			employeeRepository.deleteById(empid);
			return "The Employee record has been deleted successfully.";
		} else {
			return "Employee record does not exist.";
		}
	}

	@DeleteMapping("/employees")
	public String deleteAllEmployees() {
		employeeRepository.deleteAll();
		return "All Employee details have been deleted.";
	}

	@GetMapping("/employees/city/{empcity}")
	public ResponseEntity<List<Employee>> getEmployeeByCity(@PathVariable String empcity) {
		List<Employee> empList = employeeRepository.findByEmpCity(empcity);
		if (empList.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(empList, HttpStatus.OK);
		}
	}

	@GetMapping("/employees/age/{empage}")
	public ResponseEntity<List<Employee>> getEmployeeByAge(@PathVariable int empage) {
		List<Employee> empList = employeeRepository.findByEmpAge(empage);
		if (empList.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(empList, HttpStatus.OK);
		}

	}

	@GetMapping("/employees/salary/{empsalary}")
	public ResponseEntity<List<Employee>> getEmployeeBySalary(@PathVariable float empsalary) {
		List<Employee> empSalary = employeeRepository.findByEmpSalary(empsalary);
		if (empSalary.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(empSalary, HttpStatus.OK);
		}

	}

	@GetMapping("/employees/name/{empname}")
	public ResponseEntity<List<Employee>> getEmployeeName(@PathVariable String empname) {
		List<Employee> empName = employeeRepository.findByEmpName(empname);
		if (empName.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(empName, HttpStatus.OK);
		}

	}

}
