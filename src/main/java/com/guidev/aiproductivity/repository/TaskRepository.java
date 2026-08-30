package com.guidev.aiproductivity.repository;

import com.guidev.aiproductivity.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}