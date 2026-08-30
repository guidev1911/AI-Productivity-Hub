package com.guidev.aiproductivity.dto;

import com.guidev.aiproductivity.model.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateTaskRequest(

        @NotBlank
        String title,

        String description,

        @NotNull
        TaskPriority priority,

        LocalDateTime dueDate

) {
}