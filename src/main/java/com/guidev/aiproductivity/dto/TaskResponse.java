package com.guidev.aiproductivity.dto;

import com.guidev.aiproductivity.model.TaskPriority;

import java.time.LocalDateTime;

public record TaskResponse(

        Long id,
        String title,
        String description,
        TaskPriority priority,
        boolean completed,
        LocalDateTime dueDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}