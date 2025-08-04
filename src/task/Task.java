package task;

import java.util.Objects;
import java.time.LocalDateTime;
import java.time.Duration;

public class Task {
    private final String taskName;
    private final String taskDescription;
    private int taskID = -1;
    private TaskStatus taskStatus;
    protected Duration taskDuration;
    protected LocalDateTime taskStartTime;

    public Task(String nameTask, String taskDescription, TaskStatus taskStatus,  Duration taskDuration, LocalDateTime taskStartTime) {
        this.taskName = nameTask;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.taskDuration = taskDuration;
        this.taskStartTime = taskStartTime;
    }

    public Task(Integer id, String nameTask, String taskDescription, TaskStatus taskStatus, Duration taskDuration, LocalDateTime taskStartTime) {
        this(nameTask, taskDescription, taskStatus, taskDuration, taskStartTime);
        this.taskID = id;

    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public String getTaskName() {
        return taskName;
    }

    public int getTaskID() {
        return taskID;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Duration getTaskDuration() {
        return taskDuration;
    }

    public LocalDateTime getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(LocalDateTime taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    public void setTaskDuration(Duration taskDuration) {
        this.taskDuration = taskDuration;
    }

    public LocalDateTime getTaskEndTime() {
        return taskStartTime.plus(taskDuration);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskID == task.taskID;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(taskID);
    }

    @Override
    public String toString() {
        return String.format(getClass().getSimpleName() + "{taskID='%d', taskName='%s', taskDescription='%s', taskStatus='%s', duration='%s', startTime='%s', endTime='%s'}",
                taskID, taskName, taskDescription, taskStatus,  taskDuration, taskStartTime, getTaskEndTime());
    }
}
