package com.project.TodoAPI.service;


import com.project.TodoAPI.model.Task;
import com.project.TodoAPI.repo.TaskRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

@Service
public class TaskService {

    @Autowired
    private TaskRepo taskRepo;

    public Task addTask(Task task) {
        return taskRepo.save(task);
    }

    public Task updateTask(int id, Task task) {
        if (taskRepo.existsById(id)){
            task.setId(id);
            return taskRepo.save(task);
        } else {
            return null;
        }
    }

    public boolean deleteTask(int id) {
        if (taskRepo.existsById(id)){
            taskRepo.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public Page<Task> getAllTasks(Pageable pageable) {
        return taskRepo.findAll(pageable);
    }
}
