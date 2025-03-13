package com.project.TodoAPI.controller;

import com.project.TodoAPI.Config.JwtUtil;
import com.project.TodoAPI.model.Task;
import com.project.TodoAPI.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private TaskController taskController;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private TaskService taskService;

    @BeforeEach
    void setup() {
        // 🔹 Initialize mockMvc before running tests
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    void addTestControllerShouldAddTask() throws Exception {

        //Mocking Data
        String token = "sample_token";
        Task createdTask = new Task();
        createdTask.setId(1);
        createdTask.setDescription("Sample Task Description");
        createdTask.setName("sample task 1");
        createdTask.setCreatedBy("sample@email.com");

        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractEmail(token)).thenReturn("sample@email.com");
        when(taskService.addTask(any(Task.class), eq("sample@email.com"))).thenReturn(createdTask);

        mockMvc.perform(post("/todo/task")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test Task\", \"description\": \"Sample Description\"}"))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").value(1))
                        .andExpect(jsonPath("$.name").value("sample task 1"))
                        .andExpect(jsonPath("$.description").value("Sample Task Description"));

    }

    @Test
    void addTestControllerShouldNotAddTaskIfTokenIsNull() throws Exception {
        String token = "";

        when(jwtUtil.validateToken(token)).thenReturn(false);

        mockMvc.perform(post("/todo/task")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test Task\", \"description\": \"Sample Description\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"message\": \"Unauthorized\"}"));
    }

}