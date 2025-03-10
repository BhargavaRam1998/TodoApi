package com.project.TodoAPI.service;

import com.project.TodoAPI.model.Task;
import com.project.TodoAPI.repo.TaskRepo;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    TaskService taskService;
    @Mock
    TaskRepo taskRepo;

    @Test
    void addTaskShouldAddTaskSuccessfully(){
        Task task = new Task();
        task.setId(1);
        task.setName("GOTO COSTCO");
        task.setDescription("Dont forget to by milk");
        String email = "test@sample.com";

        when(taskRepo.save(task)).thenReturn(task);

        Task addedTask = taskService.addTask(task, email);


        assertNotNull(addedTask);
        assertEquals(task.getId(), addedTask.getId());
        assertEquals(task.getName(), addedTask.getName());
        assertEquals(task.getDescription(), addedTask.getDescription());
        assertEquals(task.getCreatedBy(), addedTask.getCreatedBy());
    }

    @Test
    void updateTaskShouldUpdateTaskSuccessfullyIfTaskExists(){
        Task task = new Task();
        task.setName("GOTO COSTCO");
        task.setDescription("Dont forget to by milk");
        String email = "test@sample.com";
        int id = 1;

        when(taskRepo.existsById(id)).thenReturn(true);
        when(taskRepo.save(task)).thenReturn(task);

        Task updatedTask = taskService.updateTask(id, task, email);

        assertNotNull(updatedTask);
        assertEquals(task.getId(), updatedTask.getId());
        assertEquals(task.getName(), updatedTask.getName());
        assertEquals(task.getDescription(), updatedTask.getDescription());
        assertEquals(task.getCreatedBy(), updatedTask.getCreatedBy());
    }

    @Test
    void updateTaskShouldNotUpdateTaskIfTaskDoesnotExist(){
        Task task = new Task();
        task.setName("GOTO COSTCO");
        task.setDescription("Dont forget to by milk");
        String email = "test@sample.com";
        int id = 1;

        when(taskRepo.existsById(id)).thenReturn(false);

        Task updatedTask = taskService.updateTask(id, task, email);

        assertNull(updatedTask);
    }

    @Test
    void deleteTaskShouldDeleteTaskifTaskExists(){
        int id = 1;

        when(taskRepo.existsById(id)).thenReturn(true);
        doNothing().when(taskRepo).deleteById(id);

        boolean result = taskService.deleteTask(id);

        verify(taskRepo, times(1)).deleteById(1);
        assertTrue(result);
    }

    @Test
    void deleteTaskShouldNotDeleteTaskifTaskDoenotExist(){
        int id = 1;

        when(taskRepo.existsById(id)).thenReturn(false);

        boolean result = taskService.deleteTask(id);

        verify(taskRepo, times(0)).deleteById(id);
        assertFalse(result);
    }

    @Test
    void getAllTasks_ShouldReturnPagedTasks(){
        Pageable pageable = PageRequest.of(0, 5);
        Task task1 = new Task();
        task1.setId(1);
        task1.setName("Task 1");

        Task task2 = new Task();
        task2.setId(2);
        task2.setName("Task 2");

        List<Task> tasks = List.of(task1, task2);
        Page<Task> taskPage = new PageImpl<>(tasks, pageable, tasks.size());

        when(taskRepo.findAll(pageable)).thenReturn(taskPage);

        Page<Task> result = taskService.getAllTasks(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsAll(tasks);

    }

}