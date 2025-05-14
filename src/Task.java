import java.util.Objects;

public class Task {
    private final String taskName;
    private final String taskDetails;
    private int taskId;
    private TaskStatus taskStatus;

    public Task(String taskName, String taskDetails, TaskStatus taskStatus) {
        this.taskName = taskName;
        this.taskDetails = taskDetails;
        this.taskStatus = TaskStatus.NEW;
        taskId++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(taskId);
    }
}
