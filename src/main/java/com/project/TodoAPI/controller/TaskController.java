package com.project.TodoAPI.controller;


import com.project.TodoAPI.model.Task;
import com.project.TodoAPI.model.Users;
import com.project.TodoAPI.service.TaskService;
import com.project.TodoAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/todo")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<HashMap<String, String>> registerUser(@RequestBody Users user){
        HashMap<String, String> response = userService.registerUser(user);

        if (response.containsKey("token")) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/task")
    public ResponseEntity<Task> addTask(@RequestBody Task task){
        Task createdTask = taskService.addTask(task);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable int id, @RequestBody Task task){
        Task updatedTask = taskService.updateTask(id, task);
        if (updatedTask != null) {
            return new ResponseEntity<>(updatedTask, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<?>deleteTask(@PathVariable int id){
        boolean isDeleted = taskService.deleteTask(id);

        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getTasks
            (@RequestParam(defaultValue = "1") int page,
             @RequestParam(defaultValue = "10") int limit){

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