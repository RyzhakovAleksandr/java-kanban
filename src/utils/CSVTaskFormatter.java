package utils;

import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CSVTaskFormatter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private CSVTaskFormatter() {
    }

    public static String getHeader() {
        return "id,type,name,status,description,duration,startTime,epicId";
    }

    public static String toCSVString(BaseTask baseTask) {
        String formatPattern = "%s,%s,%s,%s,%s,%d,%s,%s";
        if (baseTask.getStartTime() == null) {
            baseTask.setStartTime(LocalDateTime.MIN);
        }
        return switch (baseTask) {
            case EpicTask epicTask -> formatPattern.formatted(epicTask.getId(),
                    TaskType.EPICTASK,
                    epicTask.getTitle(),
                    epicTask.getStatus(),
                    epicTask.getDescription(),
                    epicTask.getDuration().toMinutes(),
                    epicTask.getStartTime().format(DATE_TIME_FORMATTER),
                    " ");
            case SubTask subTask -> formatPattern.formatted(subTask.getId(),
                    TaskType.SUBTASK,
                    subTask.getTitle(),
                    subTask.getStatus(),
                    subTask.getDescription(),
                    subTask.getDuration().toMinutes(),
                    subTask.getStartTime().format(DATE_TIME_FORMATTER),
                    subTask.getEpicTaskId());
            case Task task -> formatPattern.formatted(task.getId(),
                    TaskType.TASK,
                    task.getTitle(),
                    task.getStatus(),
                    task.getDescription(),
                    task.getDuration().toMinutes(),
                    task.getStartTime().format(DATE_TIME_FORMATTER),
                    " ");
        };
    }

    public static BaseTask fromCSVString(String str) {
        String[] taskFields = str.split(",");
        if (taskFields.length != 8) {
            throw new IllegalArgumentException("Incorrect csv pattern");
        }
        int id = Integer.parseInt(taskFields[0]);
        TaskType type = TaskType.valueOf(taskFields[1]);
        String title = taskFields[2];
        TaskStatus status = TaskStatus.valueOf(taskFields[3]);
        String description = taskFields[4];
        Duration duration = Duration.ofMinutes(Integer.parseInt(taskFields[5]));
        LocalDateTime startTime = LocalDateTime.parse(taskFields[6], DATE_TIME_FORMATTER);
        if (startTime.equals(LocalDateTime.MIN)) {
            startTime = null;
        }
        return switch (type) {
            case TASK -> new Task(title, description, id, status, duration, startTime);
            case SUBTASK -> new SubTask(title, description,
                    id, status, Integer.parseInt(taskFields[7]), duration, startTime);
            case EPICTASK -> new EpicTask(title, description, id);
        };
    }

    public static String tasksToIdString(List<BaseTask> tasks) {
        if (tasks.isEmpty()) {
            return " ";
        }

        List<Integer> taskIdList = tasks.stream().map(BaseTask::getId).toList();

        StringBuilder taskIdStringBuilder = new StringBuilder();
        for (Integer taskId : taskIdList) {
            taskIdStringBuilder.append(taskId).append(",");
        }

        taskIdStringBuilder.deleteCharAt(taskIdStringBuilder.length() - 1);
        return taskIdStringBuilder.toString();
    }

    public static List<Integer> idStringToList(String str) {
        if (str.isEmpty()) {
            return List.of();
        }
        String[] strings = str.split(",");

        List<Integer> ids = new ArrayList<>();
        for (String string : strings) {
            ids.add(Integer.parseInt(string));
        }

        return ids;
    }
}
