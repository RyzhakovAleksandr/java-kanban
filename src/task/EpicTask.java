package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class EpicTask extends BaseTask {
    private final List<SubTask> subTasks;
    private LocalDateTime endTime;

    public EpicTask(String title, String description, Integer id) {
        super(title, description, id, TaskStatus.NEW, Duration.ZERO, LocalDateTime.MAX);
        this.subTasks = new ArrayList<>();
        calculateEpicState();
    }

    public void addSubTask(SubTask subTask) {
        int i = subTasks.indexOf(subTask);
        if (i != -1) {
            subTasks.set(i, subTask);
        } else {
            subTasks.add(subTask);
        }

        calculateEpicState();
    }

    public void clearSubTasks() {
        subTasks.clear();

        calculateEpicState();
    }

    public void removeSubTask(SubTask subTask) {
        subTasks.remove(subTask);

        calculateEpicState();
    }

    public void removeSubTaskById(Integer id) {
        subTasks.removeIf(subTask -> subTask.getId().equals(id));

        calculateEpicState();
    }

    public List<SubTask> getSubTasks() {
        return List.copyOf(subTasks);
    }

    @Override
    public void setStatus(TaskStatus status) {
        calculateEpicStatus();
    }

    @Override
    public LocalDateTime getEndTime() {
        calculateEpicEndTime();
        return endTime;
    }

    private void calculateEpicState() {
        calculateEpicStartTime();
        calculateEpicEndTime();
        calculateEpicDuration();
        calculateEpicStatus();
    }

    private void calculateEpicDuration() {
        this.duration = subTasks.stream()
               .map(SubTask::getDuration)
               .reduce(Duration.ZERO, Duration::plus);
    }

    private void calculateEpicStartTime() {
        this.startTime = subTasks.stream()
                .map(SubTask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.MAX);
    }

    private void calculateEpicEndTime() {
        this.endTime = subTasks.stream()
                .map(SubTask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.MAX);
    }

    private void calculateEpicStatus() {
        if (subTasks.isEmpty()) {
            super.setStatus(TaskStatus.NEW);
            return;
        }

        boolean isAllSubTasksDone = true;
        boolean isAllSubTasksNew = true;
        for (SubTask subTask : subTasks) {
            if (subTask.getStatus() != TaskStatus.DONE) {
                isAllSubTasksDone = false;
            }
            if (subTask.getStatus() != TaskStatus.NEW) {
                isAllSubTasksNew = false;
            }
        }

        if (isAllSubTasksNew) {
            super.setStatus(TaskStatus.NEW);
        } else if (isAllSubTasksDone) {
            super.setStatus(TaskStatus.DONE);
        } else {
            super.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
