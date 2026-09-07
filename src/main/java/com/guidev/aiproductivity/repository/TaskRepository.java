package com.guidev.aiproductivity.repository;

import com.guidev.aiproductivity.model.Task;
import com.guidev.aiproductivity.model.TaskPriority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCompletedFalse();

    List<Task> findByCompletedFalseAndPriority(TaskPriority priority);
}