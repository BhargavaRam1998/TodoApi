package com.project.TodoAPI.service;


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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private Users user;

    @BeforeEach
    void setUp(){
        user = new Users();
        user.setName("Ram");
        user.setEmail("Ram@gmai.com");
        user.setPassword("password123");
    }

    @Test
    void registerUserShouldRegisterUser(){

        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("password123");
        when(userRepo.save(any(Users.class))).thenReturn(user);
        when(jwtUtil.generateToken(anyString())).thenReturn("generated_token");

        HashMap<String, String> response = userService.registerUser(user);

        assertTrue(response.containsKey("token"));
        assertEquals("generated_token", response.get("token"));
        verify(passwordEncoder).encode("password123");
        verify(userRepo).save(any(Users.class));
        verify(jwtUtil).generateToken(anyString());

    }

    @Test
    void registerUserShouldNotRegisterIfExistingUser(){

        Users existingUser = new Users();
        existingUser.setName("Ram");
        existingUser.setEmail("ram@gmail.com");
        existingUser.setPassword("password123");

        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(existingUser));

        HashMap<String, String> response = userService.registerUser(user);

        assertTrue(response.containsKey("Error!"));
        assertEquals("Email is already in use, please enter a new one", response.get("Error!"));
    }

}