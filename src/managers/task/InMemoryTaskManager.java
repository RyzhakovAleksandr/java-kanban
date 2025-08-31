package managers.task;

import managers.exceptions.TaskIntersectException;
import managers.history.HistoryManager;
import task.BaseTask;
import task.EpicTask;
import task.SubTask;
import task.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, BaseTask> tasks;
    protected final Set<BaseTask> prioritizedTasks;
    protected HistoryManager historyManager;
    protected int idCounter = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks = new HashMap<>();
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(BaseTask::getStartTime));
        this.historyManager = historyManager;
    }

    @Override
    public List<BaseTask> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Optional<BaseTask> getById(int id) {
        BaseTask task = tasks.get(id);
        historyManager.add(task);

        return Optional.ofNullable(task);
    }

    @Override
    public void removeById(int id) {
        BaseTask removedTask = tasks.remove(id);
        if (removedTask == null) {
            throw new IllegalArgumentException("Task with id " + id + " does not exist");
        }

        switch (removedTask) {
            case EpicTask epicTask -> {
                for (SubTask subTask : epicTask.getSubTasks()) {
                    tasks.remove(subTask.getId());
                    prioritizedTasks.remove(subTask);
                }
            }
            case SubTask subTask -> {
                if (!(tasks.get(subTask.getEpicTaskId()) instanceof EpicTask epicTask)) {
                    throw new IllegalStateException("subtask with no epic in map");
                }
                epicTask.removeSubTask(subTask);
            }
            case Task standardTask -> prioritizedTasks.remove(standardTask);
        }

        historyManager.remove(removedTask.getId());
    }

    @Override
    public void addTask(BaseTask task) {
        isNewTaskIntersects(task);
        switch (task) {
            case EpicTask ignored -> {
            }
            case SubTask subTask -> {
                if (!(tasks.get(subTask.getEpicTaskId()) instanceof EpicTask epicTask)) {
                    throw new IllegalArgumentException("no epic for subtask");
                }
                if (subTask.getStartTime() != null) {
                    prioritizedTasks.add(task);
                }
                epicTask.addSubTask(subTask);
            }
            case Task standardTask -> {
                if (standardTask.getStartTime() != null) {
                    prioritizedTasks.add(standardTask);
                }
            }
        }
        int id = getNextId();

        task.setId(id);
        tasks.put(id, task);
    }

    @Override
    public void updateTask(BaseTask task) {
        isNewTaskIntersects(task);
        switch (task) {
            case EpicTask epicTask -> tasks.put(epicTask.getId(), epicTask);
            case SubTask subTask -> {
                if (!(tasks.get(subTask.getEpicTaskId()) instanceof EpicTask epicTaskOfSubtask)) {
                    throw new IllegalArgumentException();
                }
                if (subTask.getStartTime() != null) {
                    prioritizedTasks.remove(subTask);
                    prioritizedTasks.add(subTask);
                }
                epicTaskOfSubtask.addSubTask(subTask);
                tasks.put(subTask.getId(), subTask);
            }
            case Task standardTask -> {
                if (standardTask.getStartTime() != null) {
                    prioritizedTasks.remove(standardTask);
                    prioritizedTasks.add(standardTask);
                }
                tasks.put(standardTask.getId(), standardTask);
            }
        }
    }

    @Override
    public List<BaseTask> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public List<SubTask> getAllSubtasksOfEpic(int id) {
        return switch (tasks.get(id)) {
            case EpicTask epicTask -> epicTask.getSubTasks();
            case SubTask ignored -> throw new IllegalArgumentException();
            case Task ignored -> throw new IllegalArgumentException();
            case null -> throw new IllegalArgumentException();
        };
    }

    @Override
    public List<EpicTask> getAllEpicsTasks() {
        return getTasksOfCertainType(EpicTask.class);
    }

    @Override
    public List<Task> getAllStandardTasks() {
        return getTasksOfCertainType(Task.class);
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return getTasksOfCertainType(SubTask.class);
    }

    @Override
    public void removeAllEpicsTasks() {
        removeAllTasksOfCertainType(EpicTask.class);
        removeAllTasksOfCertainType(SubTask.class);
    }

    @Override
    public void removeAllStandardTasks() {
        removeAllTasksOfCertainType(Task.class);
    }

    @Override
    public void removeAllSubTasks() {
        for (BaseTask value : tasks.values()) {
            switch (value) {
                case EpicTask epicTask -> epicTask.clearSubTasks();
                case SubTask ignored -> {
                }
                case Task ignored -> {
                }
            }
        }
        removeAllTasksOfCertainType(SubTask.class);
    }

    private void isNewTaskIntersects(BaseTask newTask) {
        Optional<BaseTask> intersectedTask = prioritizedTasks.stream()
                .filter(task -> isTwoTasksIntersect(newTask, task))
                .findFirst();
        if (intersectedTask.isPresent()) {
            if (!intersectedTask.get().getId().equals(newTask.getId())) {
                throw new TaskIntersectException("Задача *%s* не может быть добавлена, так как пересекается с *%s*"
                        .formatted(newTask.getTitle(), intersectedTask.get().getTitle()));
            }
        }
    }

    private boolean isTwoTasksIntersect(BaseTask task1, BaseTask task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        return !(task1.getEndTime().isBefore(task2.getStartTime()) ||
                task2.getEndTime().isBefore(task1.getStartTime()));
    }

    private int getNextId() {
        return idCounter++;
    }

    private <T extends BaseTask> void removeAllTasksOfCertainType(Class<T> taskClass) {
        List<Map.Entry<Integer, BaseTask>> list = tasks.entrySet()
                .stream()
                .filter(pair -> taskClass.isInstance(pair.getValue()))
                .toList();
        for (var entry : list) {
            prioritizedTasks.remove(entry.getValue());
            tasks.remove(entry.getKey());
            historyManager.remove(entry.getKey());
        }
    }

    private <T extends BaseTask> List<T> getTasksOfCertainType(Class<T> taskClass) {
        return tasks.values().stream()
                .filter(taskClass::isInstance)
                .map(e -> (T) e)
                .toList();
    }

}
