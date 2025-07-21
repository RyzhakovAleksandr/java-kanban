package utils;

import manager.FileBackedTaskManager;
import task.*;

import java.util.ArrayList;
import java.util.List;

public class CSVFormatter {
    public static String getHead() {
        return "id,type,name,status,description,epic";
    }

    public static String toCSVString(Task task) {
        String format = "%s,%s,%s,%s,%s,%s";

        return switch (task) {
            case Subtask subtask -> format.formatted(
                    subtask.getTaskID(),
                    TaskType.SUBTASK,
                    subtask.getTaskName(),
                    subtask.getTaskStatus(),
                    subtask.getTaskDescription(),
                    subtask.getEpic().getTaskID()
            );
            case Epic epic -> format.formatted(
                    epic.getTaskID(),
                    TaskType.EPIC,
                    epic.getTaskName(),
                    epic.getTaskStatus(),
                    epic.getTaskDescription(),
                    " "
            );
            case Task t -> format.formatted(
                    t.getTaskID(),
                    TaskType.TASK,
                    t.getTaskName(),
                    t.getTaskStatus(),
                    t.getTaskDescription(),
                    " "
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
        if (fields.length != 6) {
            throw new RuntimeException("Invalid CSV String");
        }
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        return switch (type) {
            case TASK -> new Task(id, name, description, status);
            case EPIC -> new Epic(id, name, description, status);
            case SUBTASK -> new Subtask(id, name, description, status, (Epic) manager.get(Integer.parseInt(fields[5])));
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
