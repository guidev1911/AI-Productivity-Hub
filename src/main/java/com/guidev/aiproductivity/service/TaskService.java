package com.guidev.aiproductivity.service;

import com.guidev.aiproductivity.model.Task;
import com.guidev.aiproductivity.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task create(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public Task complete(Long id) {
        Task task = findById(id);

        task.setCompleted(true);

        return taskRepository.save(task);
    }

    public void delete(Long id) {
        Task task = findById(id);

        taskRepository.delete(task);
    }
}