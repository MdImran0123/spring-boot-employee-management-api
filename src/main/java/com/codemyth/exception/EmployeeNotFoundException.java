package com.codemyth.exception;

public class EmployeeNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public EmployeeNotFoundException(Long empId) {
		super("Employee not found with Id " + empId);
	}

}
