package task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String nameTask, String taskDescription, TaskStatus taskStatus, Epic epic, Duration taskDuration, LocalDateTime taskStartTime) {
        super(nameTask, taskDescription, taskStatus, taskDuration, taskStartTime);
        this.epic = epic;
    }

    public Subtask(Integer id, String name, String description, TaskStatus taskStatus, Epic epic, Duration taskDuration, LocalDateTime taskStartTime) {
        super(id, name, description, taskStatus, taskDuration, taskStartTime);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }
}
