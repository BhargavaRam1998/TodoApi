package com.project.TodoAPI.controller;

import com.project.TodoAPI.Config.JwtUtil;
import com.project.TodoAPI.model.Task;
import com.project.TodoAPI.repo.TaskRepo;
import com.project.TodoAPI.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @Mock
    private TaskRepo taskRepo;

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
                        .content("{\"name\": \"sample task 1\", \"description\": \"Sample Task Description\"}"))
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

    @Test
    void updateTaskShouldUpdateTask() throws Exception {

        String token = "sample_token";

        Task existingTask = new Task();
        existingTask.setId(1);
        existingTask.setName("sample task 1");
        existingTask.setDescription("Sample Task Description");
        existingTask.setCreatedBy("ram@email.com");

        Task updatedTask = new Task();
        updatedTask.setId(1);
        updatedTask.setName("updated task 1");
        updatedTask.setDescription("Updated Task Description");
        updatedTask.setCreatedBy("ram@email.com");


        when(jwtUtil.extractEmail(token)).thenReturn("ram@email.com");
        when(taskRepo.findById(1)).thenReturn(Optional.of(existingTask));
        when(taskService.updateTask(eq(1), Mockito.any(Task.class), eq("ram@email.com"))).thenReturn(updatedTask);

        mockMvc.perform(put("/todo/update/{id}", 1)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"updated task 1\", \"description\": \"Updated Task Description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("updated task 1"))
                .andExpect(jsonPath("$.description").value("Updated Task Description"));
    }

    @Test
    void updateTaskShouldNotUpdateTaskIfUpdatedTaskIsNull() throws Exception {
        String token = "sample_token";

        Task existingTask = new Task();
        existingTask.setId(1);
        existingTask.setName("sample task 1");
        existingTask.setDescription("Sample Task Description");
        existingTask.setCreatedBy("ram@email.com");

        when(jwtUtil.extractEmail(token)).thenReturn("ram@email.com");
        when(taskRepo.findById(1)).thenReturn(Optional.of(existingTask));
        when(taskService.updateTask(eq(1), Mockito.any(Task.class), eq("ram@email.com"))).thenReturn(null);

        mockMvc.perform(put("/todo/update/{id}", 1)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"updated task 1\", \"description\": \"Updated Task Description\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTaskShouldNotUpdateTaskIfAuthorizationHeaderIsMissing() throws Exception {

        Task existingTask = new Task();
        existingTask.setId(1);
        existingTask.setName("sample task 1");
        existingTask.setDescription("Sample Task Description");
        existingTask.setCreatedBy("ram@email.com");


        when(jwtUtil.extractEmail(Mockito.any())).thenReturn(null);
        when(taskRepo.findById(1)).thenReturn(Optional.of(existingTask));

        mockMvc.perform(put("/todo/update/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test Task\", \"description\": \"Sample Description\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"message\": \"Unauthorized\"}"));
    }

    @Test
    void updateTaskShouldNotUpdateTaskIfTokenIsMissing() throws Exception {

        Task existingTask = new Task();
        existingTask.setId(1);
        existingTask.setName("sample task 1");
        existingTask.setDescription("Sample Task Description");
        existingTask.setCreatedBy("ram@email.com");

        when(jwtUtil.extractEmail(Mockito.any())).thenReturn(null);
        when(taskRepo.findById(1)).thenReturn(Optional.of(existingTask));

        mockMvc.perform(put("/todo/update/{id}", 1)
                .header("Authorization", "Bearer " )
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test Task\", \"description\": \"Sample Description\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"message\": \"Unauthorized\"}"));


    }

    @Test
    void updateTaskShouldNotUpdateTaskIfExistingTaskIsEmpty() throws Exception {
        String token = "sample_token";

        when(jwtUtil.extractEmail(token)).thenReturn("ram@email.com");
        when(taskRepo.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(put("/todo/update/{id}", 1)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test Task\", \"description\": \"Sample Description\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"message\": \"Task Not Found\"}"));

    }

    @Test
    void deleteTaskShouldDeleteTask() throws Exception {
        String token = "sample_token";
        Task existingTask = new Task();
        existingTask.setId(1);
        existingTask.setName("sample task 1");
        existingTask.setDescription("sample task description");
        existingTask.setCreatedBy("ram@sample.com");

        when(jwtUtil.extractEmail(token)).thenReturn("ram@sample.com");
        when(taskRepo.findById(1)).thenReturn(Optional.of(existingTask));
        when(taskService.deleteTask(1)).thenReturn(true);

        mockMvc.perform(put("/todo/delete/{id}", 1)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());


    }

    @Test
    void deleteTaskShouldNotDeleteIfTaskDoesNotExistById() throws Exception {
        String token = "sample_token";
        Task existingTask = new Task();
        existingTask.setId(1);
        existingTask.setName("sample task 1");
        existingTask.setDescription("sample task description");
        existingTask.setCreatedBy("ram@sample.com");

        when(jwtUtil.extractEmail(token)).thenReturn("ram@sample.com");
        when(taskRepo.findById(1)).thenReturn(Optional.of(existingTask));
        when(taskService.deleteTask(1)).thenReturn(false);

        mockMvc.perform(put("/todo/delete/{id}", 1)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());

    }

    @Test
    void deleteTaskShouldNotDeleteTaskIfAuthorizationHeaderIsMissing() throws Exception {
        Task existingTask = new Task();
        existingTask.setId(1);
        existingTask.setName("sample task 1");
        existingTask.setDescription("sample task description");
        existingTask.setCreatedBy("ram@sample.com");

        when(jwtUtil.extractEmail(Mockito.any())).thenReturn(null);
        when(taskRepo.findById(1)).thenReturn(Optional.of(existingTask));

        mockMvc.perform(put("/todo/delete/{id}", 1))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"message\": \"Unauthorized\"}"));

    }

    @Test
    void deleteTaskShouldNotHappenForUnauthorizedUsers() throws Exception {
        String token = "sample_token";
        Task existingTask = new Task();
        existingTask.setId(1);
        existingTask.setName("sample task 1");
        existingTask.setDescription("sample task description");
        existingTask.setCreatedBy("actualowner@gmail.com");

        when(jwtUtil.extractEmail(Mockito.any())).thenReturn("ram@sample.com");
        when(taskRepo.findById(1)).thenReturn(Optional.of(existingTask));

        mockMvc.perform(put("/todo/delete/{id}", 1)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"message\": \"Unauthorized\"}"));

    }

    @Test
    void deleteTaskShouldNotHappenIfExistingTaskIsEmpty() throws Exception {
        String token = "sample_token";

        when(jwtUtil.extractEmail(token)).thenReturn("ram@sample.com");
        when(taskRepo.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(put("/todo/delete/{id}", 1)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"message\": \"Task Not Found\"}"));
    }

    @Test
    void getTasksShouldFetchAllTheTasks() throws Exception {

        String token = "sample_token";

        Task task1 = new Task();
        task1.setId(1);
        task1.setDescription("Sample Task Description");
        task1.setName("sample task 1");
        task1.setCreatedBy("sample@email.com");

        Task task2 = new Task();
        task2.setId(2);
        task2.setDescription("Sample Task Description2");
        task2.setName("sample task 2");
        task2.setCreatedBy("sample2@email.com");


        List<Task> taskList = Arrays.asList(task1, task2);
        Pageable pageable = PageRequest.of(0, 10);

        Page<Task> taskPage = new PageImpl<>(taskList, pageable, taskList.size());

        when(jwtUtil.validateToken(Mockito.any())).thenReturn(true);
        when(taskService.getAllTasks(Mockito.any(Pageable.class))).thenReturn(taskPage);

        mockMvc.perform(get("/todo")
                .header("Authorization", "Bearer " + token)
                .param("page", "1")
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.limit").value(10))
                .andExpect(jsonPath("$.total").value(2));
    }

    @Test
    void getTasksShouldNotFetchAllTheTasksIfAuthorizationIsMissing() throws Exception {

        when(jwtUtil.validateToken(Mockito.any())).thenReturn(false);

        mockMvc.perform(get("/todo")
                .param("page", "1")
                .param("limit", "10"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"message\": \"Unauthorized\"}"));
    }

}