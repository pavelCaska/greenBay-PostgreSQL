package com.pc.greenbay.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pc.greenbay.model.request.LoginRequestDTO;
import com.pc.greenbay.entity.User;
import com.pc.greenbay.service.JwtService;
import com.pc.greenbay.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class UserControllerITests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper om;

    @BeforeEach
    void setup(){
        User user1 = User.builder()
                .username("user1")
                .password(userService.encodePassword("u12345"))
                .balance(100.0)
                .roles("ROLE_USER")
                .build();
        userService.saveUser(user1);

        User admin = User.builder()
                .username("admin")
                .password(userService.encodePassword("A12345"))
                .balance(0.0)
                .roles("ROLE_ADMIN")
                .build();
        userService.saveUser(admin);
    }

    @Test
    @Transactional
    @DisplayName("Integration test for user login with valid input")
    void givenValidUserData_whenLogin_thenReturnJwtTokenAndBalance() throws Exception {

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("user1", "u12345");

        ResultActions response = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(loginRequestDTO)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").isString())
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance").isNumber());
    }

    @Test
    @Transactional
    @DisplayName("Integration test for user login with invalid password")
    void givenInvalidPassword_whenLogin_thenReturnErrorDTO() throws Exception {

        String invalidPassword = "u54321";
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("user1", invalidPassword);

        ResultActions response = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(loginRequestDTO)));

        response.andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Authentication failed. Incorrect username and/or password."));
    }

    @Test
    @Transactional
    @DisplayName("Integration test for user login with missing username and password")
    void givenInvalidPassword_whenLogin_thenReturnErrorResponseForLogin() throws Exception {

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("", "");

        ResultActions response = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(loginRequestDTO)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Username is empty or missing."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value("Password must consists of a minimum of 6 letters and/or digits."));
    }

    @Test
    @Transactional
    @DisplayName("Integration test for helper isRunning method ")
    void givenAuthenticatedUser_whenIsRunning_thenStringInBody() throws Exception {

        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("user1");

        ResultActions response = mockMvc.perform(get("/api/temp/isRunning")
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Service is running"));
    }

    @Test
    @Transactional
    @DisplayName("Integration test for helper showUser method ")
    void givenAuthenticatedAdmin_whenShowUser_thenUserList() throws Exception {

        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("admin");

        User user2 = User.builder()
                .username("user2")
                .password(userService.encodePassword("u23456"))
                .balance(200.0)
                .roles("ROLE_USER")
                .build();
        userService.saveUser(user2);

        ResultActions response = mockMvc.perform(get("/api/user")
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2)) // Check the size of the array
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value("user1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].password").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].balance").value(100.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].roles").value("ROLE_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].username").value("user2"));
    }

    @Test
    @Transactional
    @DisplayName("Integration test for helper updateBalance method ")
    void givenAuthenticatedAdmin_whenUpdateBalance_thenJSON() throws Exception {

        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("admin");

        ResultActions response = mockMvc.perform(patch("/api/temp/balance/{username}", "user1")
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .param("newBalance", "33.3"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Balance successfully updated to 33.3"));
    }

    @Test
    @Transactional
    @DisplayName("Integration test for helper updateBalance method with missing user")
    void givenAuthenticatedAdminAndMissingUser_whenUpdateBalance_thenErrorDTO() throws Exception {

        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("admin");

        ResultActions response = mockMvc.perform(patch("/api/temp/balance/{username}", "user2")
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .param("newBalance", "33.3"));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("error").value("User not found."));
    }

    @Test
    @Transactional
    @DisplayName("Integration test for helper updateBalance method when negative newBalance")
    void givenAuthenticatedAdminAndNegativeNewBalance_whenUpdateBalance_thenErrorDTO() throws Exception {

        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("admin");

        ResultActions response = mockMvc.perform(patch("/api/temp/balance/{username}", "user2")
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .param("newBalance", "-1"));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("error").value("New balance must be a positive number."));
    }

    @Test
    @Transactional
    @DisplayName("Integration test for helper updateBalance method when parameter is missing")
    void givenAuthenticatedAdminAndMissingParameter_whenUpdateBalance_thenErrorDTO() throws Exception {

        String authorizedUser = "Bearer ";
        authorizedUser += jwtService.generateToken("admin");

        ResultActions response = mockMvc.perform(patch("/api/temp/balance/{username}", "user2")
                .header("authorization", authorizedUser)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("error").value("Parameter 'newBalance' is missing."));
    }
}
