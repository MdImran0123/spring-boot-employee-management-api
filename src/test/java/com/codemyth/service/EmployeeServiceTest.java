package com.codemyth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.codemyth.dto.EmployeeRequest;
import com.codemyth.dto.EmployeeResponse;
import com.codemyth.dto.PageResponse;
import com.codemyth.exception.EmployeeNotFoundException;
import com.codemyth.mapper.EmployeeMapper;
import com.codemyth.model.Employee;
import com.codemyth.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

	@Mock
	private EmployeeRepository employeeRepository;

	private final EmployeeMapper employeeMapper = Mappers.getMapper(EmployeeMapper.class);

	private EmployeeService employeeService;

	private Employee existing;
	private EmployeeRequest request;

	@BeforeEach
	void setUp() {
		employeeService = new EmployeeService(employeeRepository, employeeMapper);
		existing = new Employee(1L, "Ada Lovelace", new BigDecimal("90000.00"), 30, "London");
		request = new EmployeeRequest("Ada Lovelace", 30, "London", new BigDecimal("90000.00"));
	}

	@Test
	void createEmployee_savesMappedEntityAndReturnsResponse() {
		when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> {
			Employee e = inv.getArgument(0);
			e.setEmpId(1L);
			return e;
		});

		EmployeeResponse response = employeeService.createEmployee(request);

		ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
		verify(employeeRepository).save(captor.capture());
		assertThat(captor.getValue().getEmpName()).isEqualTo("Ada Lovelace");
		assertThat(captor.getValue().getEmpCity()).isEqualTo("London");
		assertThat(response.empId()).isEqualTo(1L);
		assertThat(response.empSalary()).isEqualByComparingTo("90000.00");
	}

	@Test
	void getAllEmployees_mapsEntitiesToResponses() {
		Pageable pageable = PageRequest.of(0, 20);
		when(employeeRepository.findAll(any(Specification.class), eq(pageable)))
				.thenReturn(new PageImpl<>(List.of(existing), pageable, 1));

		PageResponse<EmployeeResponse> result = employeeService.getAllEmployees(null, null, null, null, pageable);

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).empName()).isEqualTo("Ada Lovelace");
		assertThat(result.getPage()).isEqualTo(0);
		assertThat(result.getSize()).isEqualTo(20);
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getTotalPages()).isEqualTo(1);
	}

	@Test
	void getAllEmployees_appliesFiltersViaSpecification() {
		Pageable pageable = PageRequest.of(0, 20);
		when(employeeRepository.findAll(any(Specification.class), eq(pageable)))
				.thenReturn(new PageImpl<>(List.of(existing), pageable, 1));

		PageResponse<EmployeeResponse> result = employeeService.getAllEmployees("Ada", "London", 30,
				new BigDecimal("90000"), pageable);

		assertThat(result.getContent()).hasSize(1);
		verify(employeeRepository).findAll(any(Specification.class), eq(pageable));
	}

	@Test
	void getEmployeeById_returnsMappedEmployee() {
		when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));

		EmployeeResponse response = employeeService.getEmployeeById(1L);

		assertThat(response.empId()).isEqualTo(1L);
		assertThat(response.empCity()).isEqualTo("London");
	}

	@Test
	void getEmployeeById_throwsWhenMissing() {
		when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> employeeService.getEmployeeById(99L)).isInstanceOf(EmployeeNotFoundException.class)
				.hasMessageContaining("99");
	}

	@Test
	void updateEmployee_appliesRequestFields() {
		EmployeeRequest update = new EmployeeRequest("Grace Hopper", 40, "New York", new BigDecimal("120000.00"));
		when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

		EmployeeResponse response = employeeService.updateEmployee(1L, update);

		assertThat(response.empName()).isEqualTo("Grace Hopper");
		assertThat(response.empCity()).isEqualTo("New York");
		assertThat(response.empAge()).isEqualTo(40);
		assertThat(response.empSalary()).isEqualByComparingTo("120000.00");
	}

	@Test
	void updateEmployee_throwsWhenMissing() {
		when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> employeeService.updateEmployee(99L, request))
				.isInstanceOf(EmployeeNotFoundException.class);
		verify(employeeRepository, never()).save(any());
	}

	@Test
	void deleteById_deletesWhenExists() {
		when(employeeRepository.existsById(1L)).thenReturn(true);

		employeeService.deleteById(1L);

		verify(employeeRepository).deleteById(1L);
	}

	@Test
	void deleteById_throwsWhenMissing() {
		when(employeeRepository.existsById(99L)).thenReturn(false);

		assertThatThrownBy(() -> employeeService.deleteById(99L)).isInstanceOf(EmployeeNotFoundException.class);
		verify(employeeRepository, never()).deleteById(any());
	}

	@Test
	void deleteAllEmployees_delegatesToRepository() {
		employeeService.deleteAllEmployees();
		verify(employeeRepository).deleteAll();
	}

	@Test
	void getEmployeeByCity_usesIgnoreCaseContaining() {
		when(employeeRepository.findByEmpCityContainingIgnoreCase("lon")).thenReturn(List.of(existing));

		assertThat(employeeService.getEmployeeByCity("lon")).extracting(EmployeeResponse::empName)
				.containsExactly("Ada Lovelace");
	}

	@Test
	void getEmployeeByCity_returnsEmptyListWhenNoMatch() {
		when(employeeRepository.findByEmpCityContainingIgnoreCase("Nowhere")).thenReturn(List.of());

		assertThat(employeeService.getEmployeeByCity("Nowhere")).isEmpty();
	}

	@Test
	void getEmployeeBySalary_normalizesScaleBeforeQuery() {
		when(employeeRepository.findByEmpSalaryBetween(any(), any())).thenReturn(List.of(existing));

		employeeService.getEmployeeBySalary(new BigDecimal("90000"));

		BigDecimal expected = new BigDecimal("90000").setScale(2, RoundingMode.HALF_UP);
		verify(employeeRepository).findByEmpSalaryBetween(eq(expected), eq(expected));
	}

	@Test
	void getEmployeeByName_usesIgnoreCaseContaining() {
		when(employeeRepository.findByEmpNameContainingIgnoreCase("Ada")).thenReturn(List.of(existing));

		assertThat(employeeService.getEmployeeByName("Ada")).hasSize(1);
	}

	@Test
	void getEmployeeByAge_mapsResults() {
		when(employeeRepository.findByEmpAge(30)).thenReturn(List.of(existing));

		assertThat(employeeService.getEmployeeByAge(30)).hasSize(1);
	}
}