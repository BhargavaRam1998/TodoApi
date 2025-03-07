package com.project.TodoAPI.repo;

import com.project.TodoAPI.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepo extends JpaRepository<Task, Integer> {
    Optional<Task> findById(Integer id);

}
