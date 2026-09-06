package com.codemyth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.codemyth.dto.EmployeeResponse;
import com.codemyth.exception.EmployeeNotFoundException;
import com.codemyth.exception.GlobalExceptionHandler;
import com.codemyth.service.EmployeeService;

@WebMvcTest(EmployeeController.class)
@Import(GlobalExceptionHandler.class)
class EmployeeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private EmployeeService employeeService;

	private static final String VALID_JSON = """
			{"empName":"Ada","empAge":30,"empCity":"London","empSalary":90000}
			""";

	@Test
	void createEmployee_returns201() throws Exception {
		when(employeeService.createEmployee(any()))
				.thenReturn(new EmployeeResponse(1L, "Ada", 30, "London", new BigDecimal("90000")));

		mockMvc.perform(post("/api/v1/employees").contentType(MediaType.APPLICATION_JSON).content(VALID_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.empId").value(1))
				.andExpect(jsonPath("$.empName").value("Ada"));
	}

	@Test
	void createEmployee_returns400_whenNameBlank() throws Exception {
		mockMvc.perform(post("/api/v1/employees").contentType(MediaType.APPLICATION_JSON).content("""
				{"empName":"","empAge":30,"empCity":"London","empSalary":90000}
				""")).andExpect(status().isBadRequest());
	}

	@Test
	void createEmployee_returns400_whenAgeInvalid() throws Exception {
		mockMvc.perform(post("/api/v1/employees").contentType(MediaType.APPLICATION_JSON).content("""
				{"empName":"Ada","empAge":17,"empCity":"London","empSalary":90000}
				""")).andExpect(status().isBadRequest());
	}

	@Test
	void createEmployee_returns400_whenSalaryMissing() throws Exception {
		mockMvc.perform(post("/api/v1/employees").contentType(MediaType.APPLICATION_JSON).content("""
				{"empName":"Ada","empAge":30,"empCity":"London"}
				""")).andExpect(status().isBadRequest());
	}

	@Test
	void getAllEmployees_returns200() throws Exception {
		when(employeeService.getAllEmployees())
				.thenReturn(List.of(new EmployeeResponse(1L, "Ada", 30, "London", new BigDecimal("90000"))));

		mockMvc.perform(get("/api/v1/employees")).andExpect(status().isOk()).andExpect(jsonPath("$[0].empId").value(1));
	}

	@Test
	void getEmployeeById_returns200() throws Exception {
		when(employeeService.getEmployeeById(1L))
				.thenReturn(new EmployeeResponse(1L, "Ada", 30, "London", new BigDecimal("90000")));

		mockMvc.perform(get("/api/v1/employees/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.empName").value("Ada"));
	}

	@Test
	void getEmployeeById_returns404_whenMissing() throws Exception {
		when(employeeService.getEmployeeById(99L)).thenThrow(new EmployeeNotFoundException(99L));

		mockMvc.perform(get("/api/v1/employees/99")).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.message").value("Employee not found with Id 99"));
	}

	@Test
	void updateEmployee_returns200() throws Exception {
		when(employeeService.updateEmployee(eq(1L), any()))
				.thenReturn(new EmployeeResponse(1L, "Grace", 40, "NY", new BigDecimal("120000")));

		mockMvc.perform(put("/api/v1/employees/1").contentType(MediaType.APPLICATION_JSON).content("""
				{"empName":"Grace","empAge":40,"empCity":"NY","empSalary":120000}
				""")).andExpect(status().isOk()).andExpect(jsonPath("$.empName").value("Grace"));
	}

	@Test
	void deleteById_returns204() throws Exception {
		doNothing().when(employeeService).deleteById(1L);

		mockMvc.perform(delete("/api/v1/employees/1")).andExpect(status().isNoContent());
		verify(employeeService).deleteById(1L);
	}

	@Test
	void deleteById_returns404_whenMissing() throws Exception {
		doThrow(new EmployeeNotFoundException(99L)).when(employeeService).deleteById(99L);

		mockMvc.perform(delete("/api/v1/employees/99")).andExpect(status().isNotFound());
	}

	@Test
	void deleteAll_returns204() throws Exception {
		doNothing().when(employeeService).deleteAllEmployees();

		mockMvc.perform(delete("/api/v1/employees")).andExpect(status().isNoContent());
	}

	@Test
	void getByCity_returns200() throws Exception {
		when(employeeService.getEmployeeByCity("London"))
				.thenReturn(List.of(new EmployeeResponse(1L, "Ada", 30, "London", new BigDecimal("90000"))));

		mockMvc.perform(get("/api/v1/employees/city/London")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].empCity").value("London"));
	}

	@Test
	void getByAge_returns200() throws Exception {
		when(employeeService.getEmployeeByAge(30))
				.thenReturn(List.of(new EmployeeResponse(1L, "Ada", 30, "London", new BigDecimal("90000"))));

		mockMvc.perform(get("/api/v1/employees/age/30")).andExpect(status().isOk());
	}

	@Test
	void getBySalary_returns200() throws Exception {
		when(employeeService.getEmployeeBySalary(any()))
				.thenReturn(List.of(new EmployeeResponse(1L, "Ada", 30, "London", new BigDecimal("90000"))));

		mockMvc.perform(get("/api/v1/employees/salary/90000")).andExpect(status().isOk());
	}

	@Test
	void getByName_returns200() throws Exception {
		when(employeeService.getEmployeeByName("Ada"))
				.thenReturn(List.of(new EmployeeResponse(1L, "Ada", 30, "London", new BigDecimal("90000"))));

		mockMvc.perform(get("/api/v1/employees/name/Ada")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].empName").value("Ada"));
	}

	@Test
	void updateEmployee_returns400_whenNameBlank() throws Exception {
		mockMvc.perform(put("/api/v1/employees/1").contentType(MediaType.APPLICATION_JSON).content("""
				{"empName":"","empAge":40,"empCity":"NY","empSalary":120000}
				""")).andExpect(status().isBadRequest());
	}

	@Test
	void updateEmployee_returns400_whenAgeInvalid() throws Exception {
		mockMvc.perform(put("/api/v1/employees/1").contentType(MediaType.APPLICATION_JSON).content("""
				{"empName":"Grace","empAge":66,"empCity":"NY","empSalary":120000}
				""")).andExpect(status().isBadRequest());
	}
}