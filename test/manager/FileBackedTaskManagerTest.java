package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import task.*;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    //private final Managers managers = new Managers();
    private static TaskManager taskManager;
    static File file;

    @BeforeAll
    public static void setUp() throws IOException {
        file = File.createTempFile("data", "csv");
        taskManager = new FileBackedTaskManager(file);

        Task newTask = new Task("Title", "TaskDescription", TaskStatus.NEW);
        taskManager.add(newTask);

        Epic newEpic = new Epic("titleEpic", "epic description");
        taskManager.add(newEpic);

        Subtask newSubTask = new Subtask("title sub", "subtask description", TaskStatus.NEW, newEpic);
        taskManager.add(newSubTask);

        taskManager.get(3);
        taskManager.get(1);
    }

    @Test
    @DisplayName("Тест загрузки из файла")
    void testLoadFromFile() throws IOException {
        FileBackedTaskManager taskManagerFromFile = FileBackedTaskManager.loadFromFile(file);
        assertEquals(taskManager.getOneType(Task.class), taskManagerFromFile.getOneType(Task.class));
        assertEquals(taskManager.getOneType(Subtask.class), taskManagerFromFile.getOneType(Subtask.class));
        assertEquals(taskManager.getOneType(Epic.class), taskManagerFromFile.getOneType(Epic.class));
        assertEquals(taskManager.getHistory(), taskManagerFromFile.getHistory());
    }

}
