package com.project.TodoAPI.controller;

import com.project.TodoAPI.model.Users;
import com.project.TodoAPI.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @BeforeEach
    void setup(){
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void UserShouldBeRegisteredIfResponseContainsTokenAsKey() throws Exception {

        HashMap<String, String> response = new HashMap<>();
        response.put("token","value");

        Mockito.when(userService.registerUser(Mockito.any(Users.class))).thenReturn(response);

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Ram\", \"email\": \"ram@sample.com\", \"password\": \"1234\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void UserShouldNotBeRegisteredIfResponseDoesnotContainTokenAsKey() throws Exception {
        HashMap<String, String> response = new HashMap<>();

        Mockito.when(userService.registerUser(Mockito.any(Users.class))).thenReturn(response);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Ram\", \"email\": \"ram@sample.com\", \"password\": \"1234\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

}