package manager;

import task.Task;
import task.Epic;
import task.Subtask;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int idForTasks;
    private final HistoryManager historyManager;
    private final HashMap<Integer, Task> tasksList;

    public InMemoryTaskManager() {
        historyManager = Managers.getDefaultHistory();
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
        tasksList.put(task.getTaskID(), task);
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
                historyManager.addHistory(task);
                result.add(type.cast(task));
            }
        }
        return result;
    }

    @Override
    public List<Task> getAll() {
        checkTypeEpic();
        for (Task task : tasksList.values()) {
            historyManager.addHistory(task);
        }
        return new ArrayList<>(tasksList.values());
    }

    @Override
    public Task get(int id) {
        if (id <= 0 || id >= tasksList.size() + 1) {
            return new Task("-1", "-1", TaskStatus.UNKNOWN);
        }
        if (tasksList.get(id) instanceof Epic) {
            updateEpicStatus((Epic) tasksList.get(id));
        }
        historyManager.addHistory(tasksList.get(id));
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
            tasksList.remove(id);
            return true;
        }
    }

    @Override
    public void deleteAllTask() {
        tasksList.clear();
        idForTasks = 1;
    }


    private int generateID() {
        return idForTasks++;
    }

    private void checkTypeEpic() {
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
        } else {
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
        return historyManager.getHistory();
    }

    private boolean deleteEpic(int id) {
        Epic oldEpic = (Epic) tasksList.get(id);
        ArrayList<Integer> keysForDelete = new ArrayList<>();
        for (Subtask subtask : oldEpic.getSubtasks()) {
            keysForDelete.add(subtask.getTaskID());
        }
        for (Integer key : keysForDelete) {
            tasksList.remove(key);
        }

        tasksList.remove(id);
        return true;
    }

    private boolean updateTask(int id, Task task) {
        task.setTaskID(id);
        tasksList.put(task.getTaskID(), task);
        return true;
    }

    private boolean updateEpic(int id, Epic epic) {
        Epic oldEpic = (Epic) tasksList.get(id);
        epic.setSubtasks(oldEpic.getSubtasks());
        epic.setTaskID(id);
        tasksList.put(epic.getTaskID(), epic);
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
        tasksList.put(subtask.getTaskID(), subtask);
        return true;
    }

    public int getSize() {
        return tasksList.size();
    }

    private boolean convertTaskToEpic(int id, Epic newEpic) {
        Epic convertedEpic = new Epic(
                newEpic.getTaskName(),
                newEpic.getTaskDescription());
        convertedEpic.setTaskID(id);
        tasksList.put(convertedEpic.getTaskID(), convertedEpic);
        return true;
    }
}
