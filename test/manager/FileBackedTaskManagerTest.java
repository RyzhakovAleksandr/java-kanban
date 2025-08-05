package manager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import task.*;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private static TaskManager taskManager;
    static File file;

    @Test
    @DisplayName("Тест загрузки из файла")
    void testLoadFromFile() throws IOException {
        file = File.createTempFile("data", "csv");
        taskManager = new FileBackedTaskManager(file);
        FileBackedTaskManager taskManagerFromFile = FileBackedTaskManager.loadFromFile(file);
        assertEquals(taskManager.getOneType(Task.class), taskManagerFromFile.getOneType(Task.class));
        assertEquals(taskManager.getOneType(Subtask.class), taskManagerFromFile.getOneType(Subtask.class));
        assertEquals(taskManager.getOneType(Epic.class), taskManagerFromFile.getOneType(Epic.class));
        assertEquals(taskManager.getHistory(), taskManagerFromFile.getHistory());
    }

}
