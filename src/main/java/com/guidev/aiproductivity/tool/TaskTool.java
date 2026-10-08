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

            @ToolParam(description = """
        Prioridade da tarefa.
        Use LOW para baixa, MEDIUM para média e HIGH para alta.
        O usuário também pode escrever em português:
        baixa, média, alta.
        """)
            String priority,

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
                parsePriority(priority),
                parsedDueDate
        );

        return taskService.create(request);
    }

    private TaskPriority parsePriority(String priority) {

        String normalized = java.text.Normalizer
                .normalize(priority.trim().toLowerCase(),
                        java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return switch (normalized) {
            case "baixa", "low" -> TaskPriority.LOW;
            case "media", "medium" -> TaskPriority.MEDIUM;
            case "alta", "high" -> TaskPriority.HIGH;
            default -> throw new IllegalArgumentException(
                    "Prioridade inválida. Use baixa, média ou alta."
            );
        };
    }

    private LocalDateTime parseDueDate(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = java.text.Normalizer
                .normalize(value.trim().toLowerCase(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        LocalDate date;

        if (normalized.contains("depois de amanha")) {
            date = LocalDate.now().plusDays(2);

        } else if (normalized.contains("amanha")) {
            date = LocalDate.now().plusDays(1);

        } else if (normalized.contains("hoje")) {
            date = LocalDate.now();

        } else {
            throw new IllegalArgumentException(
                    "Data inválida. Use uma data no formato yyyy-MM-ddTHH:mm:ss " +
                            "ou expressões como hoje às 09:00, amanhã às 09:00 " +
                            "ou depois de amanhã às 14:30."
            );
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

        List<TaskResponse> tasks = taskService.findByTitle(title);

        if (tasks.isEmpty()) {
            throw new RuntimeException(
                    "Tarefa não encontrada: " + title
            );
        }

        TaskResponse task = tasks.stream()
                .filter(t -> !t.completed())
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                "Não existe tarefa pendente com o título: " + title
                        )
                );

        return taskService.complete(task.id());
    }

    @Tool(description = "Remove uma tarefa pelo título. Use o título exato ou o mais próximo possível.")
    public String deleteTask(
            @ToolParam(description = "Título da tarefa que será removida")
            String title) {

        List<TaskResponse> tasks = taskService.findByTitle(title);

        if (tasks.isEmpty()) {
            throw new RuntimeException(
                    "Tarefa não encontrada: " + title
            );
        }

        TaskResponse task = tasks.stream()
                .filter(t -> !t.completed())
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                "Não existe tarefa pendente com o título: " + title
                        )
                );

        taskService.delete(task.id());

        return "Tarefa removida com sucesso: " + task.title();
    }

    @Tool(description = "Lista somente as tarefas que ainda não foram concluídas")
    public List<TaskResponse> listPendingTasks() {

        return taskService.findPending();
    }

    @Tool(description = "Lista tarefas pendentes filtradas por prioridade")
    public List<TaskResponse> listPendingTasksByPriority(
            @ToolParam(description = "Prioridade: LOW, MEDIUM ou HIGH")
            String priority) {

        return taskService.findPendingByPriority(
                parsePriority(priority)
        );
    }

    @Tool(description = "Lista as tarefas pendentes que vencem hoje")
    public List<TaskResponse> listTasksDueToday() {
        return taskService.findDueToday();
    }

    @Tool(description = "Lista tarefas pendentes que já passaram da data e hora de vencimento")
    public List<TaskResponse> listOverdueTasks() {

        return taskService.findOverdue();
    }

    @Tool(description = "Busca todas as tarefas que possuem determinado título")
    public List<TaskResponse> findTasksByTitle(
            @ToolParam(description = "Título da tarefa") String title) {

        return taskService.findByTitle(title);
    }

    @Tool(description = "Edita uma tarefa existente. Altere somente os campos que o usuário solicitar.")
    public TaskResponse updateTask(
            @ToolParam(description = "Título atual da tarefa") String currentTitle,

            @ToolParam(description = "Novo título da tarefa. Se não quiser alterar, informe vazio.")
            String title,

            @ToolParam(description = "Nova descrição. Se não quiser alterar, informe vazio.")
            String description,

            @ToolParam(description = "Nova prioridade: LOW, MEDIUM ou HIGH. Se não quiser alterar, informe vazio.")
            String priority,

            @ToolParam(description = "Nova data limite. Se não quiser alterar, informe vazio.")
            String dueDate) {

        List<TaskResponse> tasks = taskService.findByTitle(currentTitle);

        if (tasks.isEmpty()) {
            throw new RuntimeException(
                    "Tarefa não encontrada: " + currentTitle
            );
        }

        if (tasks.size() > 1) {
            throw new RuntimeException(
                    "Existem várias tarefas com o título '" + currentTitle +
                            "'. É necessário identificar a tarefa correta antes de alterá-la."
            );
        }

        TaskResponse task = tasks.get(0);

        TaskPriority parsedPriority = null;

        if (priority != null && !priority.isBlank()) {
            parsedPriority = TaskPriority.valueOf(priority.toUpperCase());
        }

        LocalDateTime parsedDueDate = null;

        if (dueDate != null && !dueDate.isBlank()) {
            parsedDueDate = parseDueDate(dueDate);
        }

        return taskService.update(
                task.id(),
                title,
                description,
                parsedPriority,
                parsedDueDate
        );
    }
}
