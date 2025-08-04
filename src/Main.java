import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.TaskStatus;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        //дополнительное задание
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();

        Epic epic1 = new Epic("Epic1", "More information about epic1");
        Epic epic2 = new Epic("Epic2", "More information about epic2");
        Subtask subtask1 = new Subtask("Subtask1", "More information about subtask1", TaskStatus.NEW, epic1, Duration.ofMinutes(30), LocalDateTime.of(2025,8,4,10,0));
        Subtask subtask2 = new Subtask("Subtask2", "More information about subtask2", TaskStatus.NEW, epic1, Duration.ofHours(2), LocalDateTime.of(2025,8,4,10,10));
        Subtask subtask3 = new Subtask("Subtask3", "More information about subtask3", TaskStatus.NEW, epic1, Duration.ofHours(1), LocalDateTime.of(2025,8,4,10,30));

        taskManager.add(epic1);
        taskManager.add(epic2);
        taskManager.add(subtask1);
        taskManager.add(subtask2);
        taskManager.add(subtask3);

        System.out.println(taskManager.get(1));
        System.out.println(taskManager.get(2));
        System.out.println(taskManager.get(4));
        System.out.println(taskManager.get(1));
        System.out.println(taskManager.get(2));

        System.out.println("Вывод истории 1-2-4-1-2 -> 4-1-2");
        System.out.println(taskManager.getHistory());

        taskManager.get(5);
        taskManager.get(5);
        taskManager.get(5);
        taskManager.get(1);

        System.out.println("Вывод истории 1-2-4-1-2-5-5-5-1 -> 4-2-5-1");
        System.out.println(taskManager.getHistory());

        System.out.println("Вывод истории после удаления эпик 1 содержащий подзадачи");
        taskManager.remove(1);
        System.out.println(taskManager.getHistory());

        //Проверял загрузку из файла
        taskManager = FileBackedTaskManager.loadFromFile(new File("resources%sdata.csv".formatted(File.separator)));

        System.out.println("Выгрузка из файла");
        System.out.println(taskManager.getAll());
        System.out.println(taskManager.getHistory());
    }
}
