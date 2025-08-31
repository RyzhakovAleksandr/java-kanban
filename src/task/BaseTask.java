package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public abstract sealed class BaseTask implements Cloneable
        permits Task, SubTask, EpicTask {
    protected Duration duration;
    protected LocalDateTime startTime;
    private String title;
    private String description;
    private Integer id;
    private TaskStatus status;

    public BaseTask(String title, String description, Integer id, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public BaseTask(BaseTask baseTask) {
        this.title = baseTask.title;
        this.description = baseTask.description;
        this.id = baseTask.id;
        this.status = baseTask.status;
        this.duration = baseTask.duration;
        this.startTime = baseTask.startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        BaseTask task = (BaseTask) o;
        return Objects.equals(getId(), task.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "BaseTask{" +
                "duration=" + duration +
                ", startTime=" + startTime +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    @Override
    public BaseTask clone() {
        try {
            BaseTask clone = (BaseTask) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
