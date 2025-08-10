package task;

import java.time.Duration;
import java.time.LocalDateTime;

public final class Task extends BaseTask {

    public Task(String title,
                String description,
                Integer id,
                TaskStatus status,
                Duration duration,
                LocalDateTime startTime) {
        super(title, description, id, status, duration, startTime);
    }

}
