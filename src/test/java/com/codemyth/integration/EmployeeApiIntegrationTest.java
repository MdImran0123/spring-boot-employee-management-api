package com.codemyth.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "integration", inheritProfiles = false)
class EmployeeApiIntegrationTest {

	@Container
	@ServiceConnection
	@SuppressWarnings("resource")
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4");

	private static final String VALID_EMPLOYEE_JSON = """
			{"empName":"Ada","empAge":30,"empCity":"London","empSalary":90000}
			""";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoads() {
	}

	@Test
	void getEmployees_returns401_whenUnauthenticated() throws Exception {
		mockMvc.perform(get("/api/v1/employees")).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message").value("Authentication required"));
	}

	@Test
	void editor_canLoginCreateAndListEmployees() throws Exception {
		String token = login("editor@dev", "editor123");

		mockMvc.perform(post("/api/v1/employees").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON).content(VALID_EMPLOYEE_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.empName").value("Ada"));

		mockMvc.perform(get("/api/v1/employees").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk()).andExpect(jsonPath("$.content[0].empName").value("Ada"));
	}

	@Test
	void viewer_canListButNotCreateEmployees() throws Exception {
		String token = login("viewer@dev", "viewer123");

		mockMvc.perform(get("/api/v1/employees").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());

		mockMvc.perform(post("/api/v1/employees").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON).content(VALID_EMPLOYEE_JSON))
				.andExpect(status().isForbidden());
	}

	@Test
	void admin_canDeleteEmployee() throws Exception {
		String editorToken = login("editor@dev", "editor123");
		MvcResult createResult = mockMvc
				.perform(post("/api/v1/employees").header("Authorization", "Bearer " + editorToken)
						.contentType(MediaType.APPLICATION_JSON).content("""
								{"empName":"ToDelete","empAge":25,"empCity":"Paris","empSalary":50000}
								"""))
				.andExpect(status().isCreated()).andReturn();
		long empId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("empId").asLong();

		String adminToken = login("admin@itc", "adminpass");
		mockMvc.perform(delete("/api/v1/employees/{empId}", empId).header("Authorization", "Bearer " + adminToken))
				.andExpect(status().isNoContent());
	}

	private String login(String username, String password) throws Exception {
		MvcResult result = mockMvc
				.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
						{"username":"%s","password":"%s"}
						""".formatted(username, password)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.token").isNotEmpty()).andReturn();
		JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
		return body.get("token").asText();
	}

}
