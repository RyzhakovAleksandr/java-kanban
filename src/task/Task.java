package task;

import java.util.Objects;

public class Task {
    private final String taskName;
    private final String taskDescription;
    private int taskID;
    private TaskStatus taskStatus;

    public Task(java.lang.String nameTask, java.lang.String taskDescription, TaskStatus taskStatus) {
        this.taskName = nameTask;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
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
        return String.format(getClass().getSimpleName() + "{taskID='%d', taskName='%s', taskDescription='%s', taskStatus='%s'}",
                taskID, taskName, taskDescription, taskStatus);
    }
}
