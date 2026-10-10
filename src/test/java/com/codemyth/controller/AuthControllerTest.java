package com.codemyth.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.codemyth.config.AppSecurityProperties;
import com.codemyth.config.CorsConfig;
import com.codemyth.config.JwtProperties;
import com.codemyth.config.SecurityConfig;
import com.codemyth.exception.GlobalExceptionHandler;
import com.codemyth.security.JwtAuthenticationFilter;
import com.codemyth.security.JwtService;
import com.codemyth.security.RestAccessDeniedHandler;
import com.codemyth.security.RestAuthenticationEntryPoint;

@WebMvcTest(AuthController.class)
@Import({ GlobalExceptionHandler.class, SecurityConfig.class, CorsConfig.class, JwtService.class,
		JwtAuthenticationFilter.class, RestAuthenticationEntryPoint.class, RestAccessDeniedHandler.class })
@EnableConfigurationProperties({ JwtProperties.class, AppSecurityProperties.class })
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@MockitoBean
	private UserDetailsService userDetailsService;

	@BeforeEach
	void stubUserDetails() {
		when(userDetailsService.loadUserByUsername("testuser")).thenReturn(User.withUsername("testuser")
				.password(passwordEncoder.encode("testpass")).roles("ADMIN").build());
	}

	@Test
	void login_returnsToken_whenCredentialsValid() throws Exception {
		mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
				{"username":"testuser","password":"testpass"}
				""")).andExpect(status().isOk()).andExpect(jsonPath("$.token").isNotEmpty())
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.roles[0]").value("ADMIN"));
	}

	@Test
	void login_returns401_whenCredentialsInvalid() throws Exception {
		mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
				{"username":"testuser","password":"wrong"}
				""")).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.status").value(401))
				.andExpect(jsonPath("$.message").value("Invalid username or password"));
	}

	@Test
	void login_returns400_whenUsernameMissing() throws Exception {
		mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
				{"username":"","password":"testpass"}
				""")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.username").exists());
	}
}
