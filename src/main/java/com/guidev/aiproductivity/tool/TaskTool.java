package com.guidev.aiproductivity.tool;

import com.guidev.aiproductivity.dto.CreateTaskRequest;
import com.guidev.aiproductivity.dto.TaskResponse;
import com.guidev.aiproductivity.model.TaskPriority;
import com.guidev.aiproductivity.service.TaskService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class TaskTool {

    private final TaskService taskService;

    public TaskTool(TaskService taskService) {
        this.taskService = taskService;
    }

    @Tool(description = "Cria uma nova tarefa no sistema de produtividade")
    public TaskResponse createTask(
            @ToolParam(description = "Título da tarefa") String title,

            @ToolParam(description = "Descrição da tarefa") String description,

            @ToolParam(description = "Prioridade: LOW, MEDIUM ou HIGH") String priority,

            @ToolParam(description = """
                    Data limite da tarefa.
                    Pode ser uma data no formato yyyy-MM-ddTHH:mm:ss
                    ou uma expressão como:
                    hoje às 09:00,
                    amanhã às 09:00,
                    depois de amanhã às 14:30.
                    """)
            String dueDate) {

        LocalDateTime parsedDueDate = parseDueDate(dueDate);

        CreateTaskRequest request = new CreateTaskRequest(
                title,
                description,
                TaskPriority.valueOf(priority.toUpperCase()),
                parsedDueDate
        );

        return taskService.create(request);
    }

    private LocalDateTime parseDueDate(String value) {

        String normalized = value
                .trim()
                .toLowerCase();

        LocalDate date;

        if (normalized.contains("depois de amanhã")) {
            date = LocalDate.now().plusDays(2);
        } else if (normalized.contains("amanhã")) {
            date = LocalDate.now().plusDays(1);
        } else if (normalized.contains("hoje")) {
            date = LocalDate.now();
        } else {
            return LocalDateTime.parse(value);
        }

        LocalTime time = extractTime(normalized);

        return LocalDateTime.of(date, time);
    }

    private LocalTime extractTime(String value) {

        String time = value.replaceAll(".*?(\\d{1,2}(?::\\d{2})?).*", "$1");

        if (time.matches("\\d{1,2}:\\d{2}")) {
            return LocalTime.parse(
                    time,
                    DateTimeFormatter.ofPattern("H:mm")
            );
        }

        if (time.matches("\\d{1,2}")) {
            return LocalTime.of(Integer.parseInt(time), 0);
        }

        return LocalTime.of(9, 0);
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

    @Tool(description = "Conclui uma tarefa pelo título. Use o título exato ou o mais próximo possível.")
    public TaskResponse completeTask(
            @ToolParam(description = "Título da tarefa que será concluída")
            String title) {

        List<TaskResponse> tasks = taskService.findAll();

        TaskResponse task = tasks.stream()
                .filter(t -> t.title().equalsIgnoreCase(title))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Tarefa não encontrada: " + title)
                );

        return taskService.complete(task.id());
    }

    @Tool(description = "Remove uma tarefa pelo título. Use o título exato ou o mais próximo possível.")
    public String deleteTask(
            @ToolParam(description = "Título da tarefa que será removida")
            String title) {

        List<TaskResponse> tasks = taskService.findAll();

        TaskResponse task = tasks.stream()
                .filter(t -> t.title().equalsIgnoreCase(title))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Tarefa não encontrada: " + title)
                );

        taskService.delete(task.id());

        return "Tarefa removida com sucesso: " + task.title();
    }
}
