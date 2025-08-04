package task;

import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDateTime;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;
    private LocalDateTime taskEndTime;

    public Epic(String nameTask, String taskDescription) {
        super(nameTask, taskDescription, TaskStatus.NEW,  Duration.ZERO, LocalDateTime.MAX);
        this.subtasks = new ArrayList<>();
    }

    public Epic(Integer id, String nameTask, String taskDescription, TaskStatus taskStatus) {
        super(id, nameTask, taskDescription, taskStatus,  Duration.ZERO, LocalDateTime.MAX);
        this.subtasks = new ArrayList<>();
    }

    public Epic(Integer id, String nameTask, String taskDescription, TaskStatus taskStatus, Duration taskDuration, LocalDateTime taskStartTime) {
        super(id, nameTask, taskDescription, taskStatus, taskDuration, taskStartTime);
        this.subtasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void calculateTime() {
        this.taskStartTime = subtasks.stream()
                .map(Subtask::getTaskStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.MAX);
        this.taskDuration = subtasks.stream()
                .map(Subtask::getTaskDuration)
                .reduce(Duration.ZERO, Duration::plus);
        this.taskEndTime = subtasks.stream()
                .map(Subtask::getTaskEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.MAX);
    }

    @Override
    public LocalDateTime getTaskEndTime() {
        calculateTime();
        return taskEndTime;
    }
}
