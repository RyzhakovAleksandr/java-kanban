import task.Task;
import task.Epic;
import task.Subtask;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int IDForTasks;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        IDForTasks = 1;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    private int generateID() {
        return IDForTasks++;
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

    public void deleteAllTask() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
        IDForTasks = 1;
    }

    //methods for TASK
    public void createTask(Task task) {
        task.setTaskID(generateID());
        tasks.put(task.getTaskID(), task);
    }

    public ArrayList<Task> printAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task getTask(int taskID) {
        return tasks.get(taskID);
    }

    public void updateTask(int ID, Task task) {
        task.setTaskID(ID);
        tasks.put(task.getTaskID(), task);
    }

    public void deleteTask(int taskID) {
        tasks.remove(taskID);
    }

    //methods for EPIC
    public void createEpic(Epic epic) {
        epic.setTaskID(generateID());
        epics.put(epic.getTaskID(), epic);
    }

    public ArrayList<Epic> printAllEpics() {
        for (Epic epic : epics.values()) {
            updateEpicStatus(epic);
        }
        return new ArrayList<>(epics.values());
    }

    public Epic getEpic(int taskID) {
        Epic epic = epics.get(taskID);
        updateEpicStatus(epic);
        return epic;
    }

    public void updateEpic(int ID, Epic epic) {
        Epic oldEpic = epics.get(ID);
        epic.setSubtasks(oldEpic.getSubtasks());
        epic.setTaskID(ID);
        epics.put(epic.getTaskID(), epic);
    }

    public void deleteEpic(int taskID) {
        Epic oldEpic = epics.get(taskID);
        ArrayList<Integer> keysForDelete = new ArrayList<>();
        for (Subtask subtask : oldEpic.getSubtasks()) {
            keysForDelete.add(subtask.getTaskID());
        }
        for (Integer key : keysForDelete) {
            subtasks.remove(key);
        }

        epics.remove(taskID);
    }

    //methods for SUBTASK
    public void createSubTask(Subtask subtask) {
        subtask.getEpic().getSubtasks().add(subtask);
        subtask.setTaskID(generateID());
        subtasks.put(subtask.getTaskID(), subtask);
    }

    public ArrayList<Subtask> printAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public Task getSubTask(int taskID) {
        return subtasks.get(taskID);
    }

    public void updateSubTask(int ID, Subtask subtask) {
        Epic epic = subtask.getEpic();
        subtask.setTaskID(ID);
        ArrayList<Subtask> subtasksInEpic = epic.getSubtasks();
        for (int i = 0; i < subtasksInEpic.size(); i++) {
            if (subtasksInEpic.get(i).equals(subtask)) {
                subtasksInEpic.set(i, subtask);
            }
        }

        epic.setSubtasks(subtasksInEpic);
        subtask.setEpic(epic);
        subtasks.put(subtask.getTaskID(), subtask);
    }

    public void deleteSubTask(int taskID) {
        subtasks.remove(taskID);
    }
}
