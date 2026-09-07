package com.guidev.aiproductivity.service;

import com.guidev.aiproductivity.dto.CreateTaskRequest;
import com.guidev.aiproductivity.dto.TaskResponse;
import com.guidev.aiproductivity.model.Task;
import com.guidev.aiproductivity.model.TaskPriority;
import com.guidev.aiproductivity.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponse create(CreateTaskRequest request) {

        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .priority(request.priority())
                .dueDate(request.dueDate())
                .completed(false)
                .build();

        Task savedTask = taskRepository.save(task);

        return toResponse(savedTask);
    }

    public List<TaskResponse> findAll() {

        return taskRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TaskResponse findById(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Task not found: " + id)
                );

        return toResponse(task);
    }

    public TaskResponse complete(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Task not found: " + id)
                );

        task.setCompleted(true);

        Task savedTask = taskRepository.save(task);

        return toResponse(savedTask);
    }

    public void delete(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Task not found: " + id)
                );

        taskRepository.delete(task);
    }

    private TaskResponse toResponse(Task task) {

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.isCompleted(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    public List<TaskResponse> findPending() {

        return taskRepository.findByCompletedFalse()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<TaskResponse> findPendingByPriority(TaskPriority priority) {

        return taskRepository.findByCompletedFalseAndPriority(priority)
                .stream()
                .map(this::toResponse)
                .toList();
    }
}