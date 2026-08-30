package com.guidev.aiproductivity.tool;

import com.guidev.aiproductivity.dto.CreateTaskRequest;
import com.guidev.aiproductivity.dto.TaskResponse;
import com.guidev.aiproductivity.service.TaskService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskTool {

    private final TaskService taskService;

    public TaskTool(TaskService taskService) {
        this.taskService = taskService;
    }

    @Tool(description = "Cria uma nova tarefa no sistema de produtividade")
    public TaskResponse createTask(
            @ToolParam(description = "Dados da tarefa que será criada")
            CreateTaskRequest request) {

        return taskService.create(request);
    }

    @Tool(description = "Lista todas as tarefas cadastradas")
    public List<TaskResponse> listTasks() {

        return taskService.findAll();
    }

    @Tool(description = "Busca uma tarefa pelo seu ID")
    public TaskResponse getTask(
            @ToolParam(description = "ID da tarefa")
            Long id) {

        return taskService.findById(id);
    }

    @Tool(description = "Marca uma tarefa como concluída")
    public TaskResponse completeTask(
            @ToolParam(description = "ID da tarefa que será concluída")
            Long id) {

        return taskService.complete(id);
    }

    @Tool(description = "Remove uma tarefa pelo seu ID")
    public String deleteTask(
            @ToolParam(description = "ID da tarefa que será removida")
            Long id) {

        taskService.delete(id);

        return "Tarefa removida com sucesso.";
    }
}