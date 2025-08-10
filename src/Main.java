import managers.Managers;
import managers.task.TaskManager;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Задача 1", "описание ", null, TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.of(2020, 1, 1, 1, 1));
        Task task2 = new Task("Задача 2", "описание ", null, TaskStatus.IN_PROGRESS,
                Duration.ofHours(1), LocalDateTime.of(2020, 1, 1, 1, 1));

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        printAllTasks(taskManager);
        taskManager.getById(1);
        taskManager.updateTask(new Task("Задача 1", "новое описание", 0, TaskStatus.DONE,
                Duration.ofHours(1), LocalDateTime.of(2020, 1, 1, 1, 1)));
        taskManager.removeById(1);
        printAllTasks(taskManager);

        EpicTask epic1 = new EpicTask("Эпик 1", "описание", null);
        EpicTask epic2 = new EpicTask("Эпик 2", "описание", null);
        taskManager.addTask(epic1);
        taskManager.addTask(epic2);
        printAllTasks(taskManager);

        SubTask subTask1 = new SubTask("Подзадача 1", "описание", null, TaskStatus.NEW, 2,
                Duration.ofHours(1), LocalDateTime.of(2020, 1, 1, 1, 1));
        SubTask subTask2 = new SubTask("Подзадача 2", "описание", null, TaskStatus.NEW, 2,
                Duration.ofHours(1), LocalDateTime.of(2020, 1, 1, 1, 1));
        SubTask subTask3 = new SubTask("Подзадача 3", "описание", null, TaskStatus.NEW, 3,
                Duration.ofHours(1), LocalDateTime.of(2020, 1, 1, 1, 1));
        taskManager.getById(10);
        taskManager.getById(2);
        taskManager.addTask(subTask1);
        taskManager.addTask(subTask2);
        taskManager.addTask(subTask3);
        printAllTasks(taskManager);

        SubTask updatedSubTask3 = new SubTask("Подзадача 3", "описание", subTask3.getId(), TaskStatus.DONE, 3,
                Duration.ofHours(1), LocalDateTime.of(2020, 1, 1, 1, 1));
        taskManager.updateTask(updatedSubTask3);
        printAllTasks(taskManager);

        taskManager.removeById(2);
        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {

        System.out.println("\nЗадачи:");
        for (Task task : manager.getAllStandardTasks()) {
            System.out.println(task);
        }

        System.out.println("Эпики:");
        for (EpicTask epic : manager.getAllEpicsTasks()) {
            System.out.println(epic);

            for (SubTask task : manager.getAllSubtasksOfEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (SubTask subtask : manager.getAllSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (BaseTask task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
