package utils;

import manager.FileBackedTaskManager;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CSVFormatter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String getHead() {
        return "id,type,name,status,description,epic";
    }

    public static String toCSVString(Task anyTask) {
        String format = "%s,%s,%s,%s,%s,%s,%s,%s";

        return switch (anyTask) {
            case Subtask subtask -> format.formatted(
                    subtask.getTaskID(),
                    TaskType.SUBTASK,
                    subtask.getTaskName(),
                    subtask.getTaskStatus(),
                    subtask.getTaskDescription(),
                    subtask.getEpic().getTaskID(),
                    subtask.getTaskDuration().toMinutes(),
                    subtask.getTaskStartTime().format(DATE_TIME_FORMATTER)
            );
            case Epic epic -> format.formatted(
                    epic.getTaskID(),
                    TaskType.EPIC,
                    epic.getTaskName(),
                    epic.getTaskStatus(),
                    epic.getTaskDescription(),
                    " ",
                    epic.getTaskDuration().toMinutes(),
                    epic.getTaskStartTime().format(DATE_TIME_FORMATTER)
            );
            case Task task -> format.formatted(
                    task.getTaskID(),
                    TaskType.TASK,
                    task.getTaskName(),
                    task.getTaskStatus(),
                    task.getTaskDescription(),
                    " ",
                    task.getTaskDuration().toMinutes(),
                    task.getTaskStartTime().format(DATE_TIME_FORMATTER)
            );
        };
    }

    public static String historyToCSVString(List<Task> history) {
        if (history.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Task task : history) {
            sb.append(task.getTaskID()).append(",");
        }

        return sb.toString();
    }

    public static Task fromCSVToString(String str, FileBackedTaskManager manager) {
        String[] fields = str.split(",");
        if (fields.length != 8) {
            throw new RuntimeException("Invalid CSV String");
        }
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        Duration duration = Duration.ofMinutes(Long.parseLong(fields[6]));
        LocalDateTime startTime = LocalDateTime.parse(fields[7], DATE_TIME_FORMATTER);
        return switch (type) {
            case TASK -> new Task(id, name, description, status, duration, startTime);
            case EPIC -> new Epic(id, name, description, status, duration, startTime);
            case SUBTASK ->
                    new Subtask(id, name, description, status, (Epic) manager.get(Integer.parseInt(fields[5])), duration, startTime);
        };
    }

    public static List<Integer> idStringToListHistory(String str) {
        if (str.isEmpty()) {
            return List.of();
        }
        String[] fields = str.split(",");

        List<Integer> tasks = new ArrayList<>();
        for (String field : fields) {
            tasks.add(Integer.parseInt(field));
        }
        return tasks;
    }
}
