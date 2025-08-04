package manager;

import exceptions.TaskIntersectException;
import task.Task;
import task.Epic;
import task.Subtask;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int idForTasks;
    protected final Set<Task> prioritizedTasks;
    protected final HistoryManager historyManager;
    protected final HashMap<Integer, Task> tasksList;

    public InMemoryTaskManager() {
        historyManager = Managers.getDefaultHistory();
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getTaskStartTime));
        idForTasks = 1;
        tasksList = new HashMap<>();
    }

    @Override
    public boolean add(Task task) {
        return add(generateID(), task);
    }

    @Override
    public boolean add(int id, Task task) {
        if (task == null) {
            return false;
        }

        task.setTaskID(id);
        isNewTaskIntersects(task);
        tasksList.put(task.getTaskID(), task);
        prioritizedTasks.add(task);
        if (task instanceof Subtask) {
            ((Subtask) task).getEpic().getSubtasks().add((Subtask) task);
        }
        return true;
    }

    @Override
    public <T extends Task> List<T> getOneType(Class<T> type) {
        if (type == Epic.class) {
            checkTypeEpic();
        }
        List<T> result = new ArrayList<>();
        for (Task task : tasksList.values()) {
            if (type.equals(task.getClass())) {
                historyManager.add(task);
                result.add(type.cast(task));
            }
        }
        return result;
    }

    @Override
    public List<Task> getAll() {
        checkTypeEpic();
        for (Task task : tasksList.values()) {
            historyManager.add(task);
        }
        return new ArrayList<>(tasksList.values());
    }

    @Override
    public Task get(int id) {
        if (id <= 0 || id >= tasksList.size() + 1) {
            return new Task("-1", "-1", TaskStatus.UNKNOWN, Duration.ZERO, LocalDateTime.MAX);
        }
        if (tasksList.get(id) instanceof Epic) {
            updateEpicStatus((Epic) tasksList.get(id));
        }
        historyManager.add(tasksList.get(id));
        return tasksList.get(id);
    }

    @Override
    public <T extends Task> boolean update(int id, T newTask) {
        if (id <= 0 || id >= tasksList.size() + 1) {
            return false;
        }

        Task existingTask = tasksList.get(id);

        if (existingTask == null) {
            return add(id, newTask);
        }

        if (existingTask instanceof Epic && !(newTask instanceof Epic)) {
            return false;
        } else if (existingTask instanceof Subtask && !(newTask instanceof Subtask)) {
            return false;
        } else if (newTask instanceof Epic && !(existingTask instanceof Epic)) {
            return convertTaskToEpic(id, (Epic) newTask);
        }

        return switch (newTask) {
            case Subtask subtask -> updateSubTask(id, subtask);
            case Epic epic -> updateEpic(id, epic);
            case Task task -> updateTask(id, task);
            case null -> false;
        };
    }

    @Override
    public boolean remove(int id) {
        if (id <= 0 || id >= tasksList.size() + 1) {
            return false;
        } else if (tasksList.get(id) instanceof Epic) {
            return deleteEpic(id);
        } else {
            prioritizedTasks.remove(tasksList.get(id));
            tasksList.remove(id);
            historyManager.remove(id);
            return true;
        }
    }

    @Override
    public void deleteAllTask() {
        prioritizedTasks.clear();
        tasksList.clear();

        idForTasks = 1;
    }


    private int generateID() {
        return idForTasks++;
    }

    public void checkTypeEpic() {
        for (Task task : tasksList.values()) {
            if (task instanceof Epic) {
                updateEpicStatus((Epic) task);
            }
        }
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<Subtask> subtasksInEpic = epic.getSubtasks();
        if (subtasksInEpic == null || subtasksInEpic.isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);
            epic.setTaskDuration(Duration.ZERO);
            epic.setTaskStartTime(LocalDateTime.MAX);
        } else {
            epic.calculateTime();
            boolean allNew = true;
            boolean allDone = true;
            for (Subtask subtask : subtasksInEpic) {
                if (subtask.getTaskStatus() != TaskStatus.NEW) {
                    allNew = false;
                } else if (subtask.getTaskStatus() != TaskStatus.DONE) {
                    allDone = false;
                }
            }
            if (allNew) {
                epic.setTaskStatus(TaskStatus.NEW);
            } else if (allDone) {
                epic.setTaskStatus(TaskStatus.DONE);
            } else {
                epic.setTaskStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    public List<Task> getHistory() {
        return historyManager.getTasks();
    }

    private boolean deleteEpic(int id) {
        Epic oldEpic = (Epic) tasksList.get(id);
        ArrayList<Integer> keysForDelete = new ArrayList<>();
        for (Subtask subtask : oldEpic.getSubtasks()) {
            keysForDelete.add(subtask.getTaskID());
        }
        for (Integer key : keysForDelete) {
            prioritizedTasks.remove(tasksList.get(key));
            historyManager.remove(key);
            tasksList.remove(key);
        }

        prioritizedTasks.remove(oldEpic);
        tasksList.remove(id);
        historyManager.remove(id);
        return true;
    }

    private boolean updateTask(int id, Task task) {
        task.setTaskID(id);
        isNewTaskIntersects(task);
        prioritizedTasks.add(task);
        tasksList.put(task.getTaskID(), task);
        return true;
    }

    private boolean updateEpic(int id, Epic epic) {
        Epic oldEpic = (Epic) tasksList.get(id);
        epic.setSubtasks(oldEpic.getSubtasks());
        epic.setTaskID(id);
        tasksList.put(epic.getTaskID(), epic);
        prioritizedTasks.add(oldEpic);
        return true;
    }

    private boolean updateSubTask(int id, Subtask subtask) {
        Epic epic = subtask.getEpic();
        subtask.setTaskID(id);
        ArrayList<Subtask> subtasksInEpic = epic.getSubtasks();
        for (int i = 0; i < subtasksInEpic.size(); i++) {
            if (subtasksInEpic.get(i).equals(subtask)) {
                subtasksInEpic.set(i, subtask);
                break;
            }
        }

        epic.setSubtasks(subtasksInEpic);
        subtask.setEpic(epic);
        isNewTaskIntersects(subtask);
        prioritizedTasks.add(subtask);
        tasksList.put(subtask.getTaskID(), subtask);
        return true;
    }

    public int getSize() {
        return tasksList.size();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean convertTaskToEpic(int id, Epic newEpic) {
        Epic convertedEpic = new Epic(
                newEpic.getTaskName(),
                newEpic.getTaskDescription());
        convertedEpic.setTaskID(id);
        tasksList.put(convertedEpic.getTaskID(), convertedEpic);
        return true;
    }

    private void isNewTaskIntersects(Task newTask) {
        Optional<Task> intersectedTask = prioritizedTasks.stream()
                .filter(task -> !(task instanceof Epic))
                .filter(task -> isTwoTasksIntersect(newTask, task))
                .findFirst();
        if (intersectedTask.isPresent()) {
            throw new TaskIntersectException("Задача %s не может быть добавлена, так как пересекается с %s"
                    .formatted(newTask.getTaskName(), intersectedTask.get().getTaskName()));
        }
    }

    private boolean isTwoTasksIntersect(Task task1, Task task2) {
        if (task1.getTaskStartTime() == null || task2.getTaskStartTime() == null) {
            return false;
        }
        return !(task1.getTaskEndTime().isBefore(task2.getTaskStartTime()) ||
                task2.getTaskEndTime().isBefore(task1.getTaskStartTime()));
    }

}
