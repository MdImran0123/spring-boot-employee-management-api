package com.codemyth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import com.codemyth.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	@Query(value = "SELECT * FROM employee e WHERE e.emp_city = :empcity", nativeQuery = true)
	List<Employee> findByEmpCity(@Param("empcity") String empcity);

	@Query(value = "SELECT * FROM employee e WHERE e.emp_age = :empage", nativeQuery = true)
	List<Employee> findByEmpAge(@Param("empage") int empage);

	@Query(value = "SELECT * FROM employee e WHERE e.emp_salary = :empsalary", nativeQuery = true)
	List<Employee> findByEmpSalary(@Param("empsalary") float empsalary);

	@Query(value = "SELECT * FROM employee e WHERE e.emp_name LIKE CONCAT('%', :empname, '%')", nativeQuery = true)
	List<Employee> findByEmpName(@Param("empname") String empname);
}
