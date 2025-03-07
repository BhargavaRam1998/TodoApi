package com.project.TodoAPI.controller;


import com.project.TodoAPI.Config.JwtUtil;
import com.project.TodoAPI.model.Task;
import com.project.TodoAPI.repo.TaskRepo;
import com.project.TodoAPI.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/todo")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/task")
    public ResponseEntity<?> addTask(@RequestBody Task task, HttpServletRequest request){

        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (!validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Unauthorized\"}");
        }

        String email = jwtUtil.extractEmail(token);

        Task createdTask = taskService.addTask(task, email);

        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }
    private boolean validateToken(String token) {
        try {
            String email = jwtUtil.extractEmail(token);
            return email!= null;
        } catch (Exception e) {
            return false;
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTask(@PathVariable int id, @RequestBody Task task, HttpServletRequest request){

        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String currentUser = jwtUtil.extractEmail(token);
        // Fetch the existing task from the database
        Optional<Task> existingTask = taskRepo.findById(id);

        if (existingTask.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"Task Not Found\"}");
        }

        // Check if the logged-in user is the task owner
        if (!currentUser.equals(existingTask.get().getCreatedBy())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Unauthorized\"}");
        }

        Task updatedTask = taskService.updateTask(id, task, currentUser);

        if (updatedTask != null) {
            return new ResponseEntity<>(updatedTask, HttpStatus.OK); //called as constructor based approach
            //return ResponseEntity.status(HttpStatus.OK).body(updatedTask); --same usage as above, called as builder based approach
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<?>deleteTask(@PathVariable int id, HttpServletRequest request){

        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String currentUser = jwtUtil.extractEmail(token);
        // Fetch the existing task from the database
        Optional<Task> existingTask = taskRepo.findById(id);

        if (existingTask.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"Task Not Found\"}");
        }

        // Check if the logged-in user is the task owner
        if (!currentUser.equals(existingTask.get().getCreatedBy())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Unauthorized\"}");
        }

        boolean isDeleted = taskService.deleteTask(id);

        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping()
    public ResponseEntity<?> getTasks
            (@RequestParam(defaultValue = "1") int page,
             @RequestParam(defaultValue = "10") int limit,
             HttpServletRequest request){

        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if(!validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Unauthorized\"}");
        }

        Pageable pageable = PageRequest.of(page - 1, limit);

            Page<Task> taskPage = taskService.getAllTasks(pageable);

            Map<String, Object> response = new HashMap<>();

            response.put("data", taskPage.getContent());
            response.put("page", page);
            response.put("limit", limit);
            response.put("total", taskPage.getTotalElements());

            return new ResponseEntity<>(response, HttpStatus.OK);
    }
}