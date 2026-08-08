package com.tms.tm_auth_service.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Disabled
class AuthControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void register_withValidRequest_returnsCreated() throws Exception {
		mockMvc.perform(post("/api/v1/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "username": "john_doe",
								  "password": "SecurePass1"
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.username", is("john_doe")))
				.andExpect(jsonPath("$.role", is("CUSTOMER")))
				.andExpect(jsonPath("$.message", is("User registered successfully")));
	}

	@Test
	void register_withDuplicateUsername_returnsConflict() throws Exception {
		String payload = """
				{
				  "username": "duplicate_user",
				  "password": "SecurePass1"
				}
				""";

		mockMvc.perform(post("/api/v1/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(payload))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/api/v1/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(payload))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.errorCode", is("USERNAME_ALREADY_EXISTS")))
				.andExpect(jsonPath("$.message", is("Username already exists")));
	}

	@Test
	void register_withWeakPassword_returnsBadRequest() throws Exception {
		mockMvc.perform(post("/api/v1/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "username": "weak_pass_user",
								  "password": "password"
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errorCode", is("VALIDATION_FAILED")))
				.andExpect(jsonPath("$.fieldErrors", hasSize(1)))
				.andExpect(jsonPath("$.fieldErrors[0].field", is("password")));
	}

	@Test
	void register_withInvalidUsername_returnsBadRequest() throws Exception {
		mockMvc.perform(post("/api/v1/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "username": "invalid user!",
								  "password": "SecurePass1"
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errorCode", is("VALIDATION_FAILED")))
				.andExpect(jsonPath("$.fieldErrors[0].field", is("username")));
	}
}
