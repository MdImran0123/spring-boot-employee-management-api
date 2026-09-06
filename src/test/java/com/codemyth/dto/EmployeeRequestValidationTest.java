package com.codemyth.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

class EmployeeRequestValidationTest {

	private Validator validator;

	@BeforeEach
	void setUp() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	@Test
	void validRequest_hasNoViolations() {
		assertThat(validator.validate(new EmployeeRequest("Ada", 30, "London", new BigDecimal("90000")))).isEmpty();
	}

	@Test
	void age18And65_areValid() {
		assertThat(validator.validate(new EmployeeRequest("Ada", 18, "London", BigDecimal.ZERO))).isEmpty();
		assertThat(validator.validate(new EmployeeRequest("Ada", 65, "London", BigDecimal.ZERO))).isEmpty();
	}

	@Test
	void blankName_isInvalid() {
		assertThat(validator.validate(new EmployeeRequest("", 30, "London", new BigDecimal("90000")))).isNotEmpty();
	}

	@Test
	void ageAbove65_isInvalid() {
		assertThat(validator.validate(new EmployeeRequest("Ada", 66, "London", new BigDecimal("90000")))).isNotEmpty();
	}

	@Test
	void blankCity_isInvalid() {
		assertThat(validator.validate(new EmployeeRequest("Ada", 30, "", new BigDecimal("90000")))).isNotEmpty();
	}

	@Test
	void ageBelow18_isInvalid() {
		assertThat(validator.validate(new EmployeeRequest("Ada", 17, "London", new BigDecimal("90000")))).isNotEmpty();
	}

	@Test
	void negativeSalary_isInvalid() {
		assertThat(validator.validate(new EmployeeRequest("Ada", 30, "London", new BigDecimal("-1")))).isNotEmpty();
	}

	@Test
	void nullSalary_isInvalid() {
		assertThat(validator.validate(new EmployeeRequest("Ada", 30, "London", null))).isNotEmpty();
	}
}