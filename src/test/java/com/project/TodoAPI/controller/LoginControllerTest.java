package com.project.TodoAPI.controller;

import com.project.TodoAPI.Config.JwtUtil;
import com.project.TodoAPI.model.Users;
import com.project.TodoAPI.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


import static org.mockito.ArgumentMatchers.eq;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private LoginController loginController;

    @Mock
    private UserRepo userRepo;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
    }

    @Test
    void login_success() throws Exception {
        Users existingUser = new Users();
        existingUser.setName("Ram");
        existingUser.setEmail("ram@gmail.com");
        existingUser.setPassword("encoded_password");

        Mockito.when(userRepo.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existingUser));
        Mockito.when(passwordEncoder.matches(eq("password123"), eq("encoded_password"))).thenReturn(true);
        Mockito.when(jwtUtil.generateToken(Mockito.anyString())).thenReturn("generated_token");

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"ram@gmail.com\", \"password\": \"password123\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").value("generated_token"));
    }

    @Test
    void login_failure() throws Exception {

        Mockito.when(userRepo.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"ram@gmail.com\", \"password\": \"password123\"}"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(content().json("{\"message\":\"Invalid Credentials\"}"));

    }

}