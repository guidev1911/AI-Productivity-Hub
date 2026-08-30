package com.guidev.aiproductivity.tool;

import com.guidev.aiproductivity.model.Task;
import com.guidev.aiproductivity.model.TaskPriority;
import com.guidev.aiproductivity.repository.TaskRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskTool {

    private final TaskRepository taskRepository;

    public TaskTool(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Tool(description = "Cria uma nova tarefa")
    public Task createTask(
            @ToolParam(description = "Título da tarefa")
            String title,

            @ToolParam(description = "Descrição da tarefa")
            String description,

            @ToolParam(description = "Prioridade da tarefa: LOW, MEDIUM ou HIGH")
            TaskPriority priority) {

        Task task = Task.builder()
                .title(title)
                .description(description)
                .priority(priority)
                .completed(false)
                .build();

        return taskRepository.save(task);
    }

    @Tool(description = "Lista todas as tarefas cadastradas")
    public List<Task> listTasks() {
        return taskRepository.findAll();
    }

    @Tool(description = "Marca uma tarefa como concluída")
    public Task completeTask(
            @ToolParam(description = "ID da tarefa")
            Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tarefa não encontrada: " + id
                ));

        task.setCompleted(true);

        return taskRepository.save(task);
    }
}