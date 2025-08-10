package task;

import java.time.Duration;
import java.time.LocalDateTime;

public final class SubTask extends BaseTask {
    Integer epicTaskId;

    public SubTask(String title,
                   String description,
                   Integer id,
                   TaskStatus status,
                   Integer epicTaskId,
                   Duration duration,
                   LocalDateTime startTime) {
        super(title, description, id, status,  duration, startTime);
        this.epicTaskId = epicTaskId;
    }

    public Integer getEpicTaskId() {
        return epicTaskId;
    }

    public void setEpicTaskId(Integer epicTaskId) {
        this.epicTaskId = epicTaskId;
    }
}
