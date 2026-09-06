package com.codemyth.exception;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(EmployeeNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleNotFound(EmployeeNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
				"timestamp", Instant.now().toString(),
				"status", 404,
				"error", "Not Found",
				"message", ex.getMessage()));

	}

}
