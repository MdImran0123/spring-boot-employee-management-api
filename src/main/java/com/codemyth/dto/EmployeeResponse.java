package com.codemyth.dto;

import java.math.BigDecimal;

public record EmployeeResponse(Long empId, String empName, int empAge, String empCity, BigDecimal empSalary) {
}
