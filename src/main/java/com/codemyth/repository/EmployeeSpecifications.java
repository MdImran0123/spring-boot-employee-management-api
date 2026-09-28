package com.codemyth.repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.codemyth.model.Employee;

import jakarta.persistence.criteria.Predicate;

public final class EmployeeSpecifications {

	private EmployeeSpecifications() {
	}

	public static Specification<Employee> withFilters(String name, String city, Integer age, BigDecimal salary) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (name != null && !name.isBlank()) {
				predicates.add(cb.like(cb.lower(root.get("empName")), "%" + name.trim().toLowerCase() + "%"));
			}
			if (city != null && !city.isBlank()) {
				predicates.add(cb.like(cb.lower(root.get("empCity")), "%" + city.trim().toLowerCase() + "%"));
			}
			if (age != null) {
				predicates.add(cb.equal(root.get("empAge"), age));
			}
			if (salary != null) {
				BigDecimal scaled = salary.setScale(2, RoundingMode.HALF_UP);
				predicates.add(cb.equal(root.get("empSalary"), scaled));
			}

			return cb.and(predicates.toArray(Predicate[]::new));
		};
	}
}
