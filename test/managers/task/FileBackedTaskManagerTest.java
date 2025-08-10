package managers.task;

import org.junit.jupiter.api.Test;
import managers.history.InMemoryHistoryManager;
import task.EpicTask;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    File file;


    @Override
    public FileBackedTaskManager createTaskManager() {
        try {
            file = File.createTempFile("data", "csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new FileBackedTaskManager(new InMemoryHistoryManager(), file);
    }

    @Test
    void testLoadFromFile() {
        FileBackedTaskManager fileBackedTaskManager = getFileBackedTaskManager();

        fileBackedTaskManager.getById(1);
        fileBackedTaskManager.getById(2);
        FileBackedTaskManager taskManagerFromFile = FileBackedTaskManager.loadFromFile(file);
        assertEquals(fileBackedTaskManager.getAllStandardTasks(), taskManagerFromFile.getAllStandardTasks());
        assertEquals(fileBackedTaskManager.getAllEpicsTasks(), taskManagerFromFile.getAllEpicsTasks());
        assertEquals(fileBackedTaskManager.getAllSubTasks(), taskManagerFromFile.getAllSubTasks());
        assertEquals(fileBackedTaskManager.getHistory(), taskManagerFromFile.getHistory());
    }

    private FileBackedTaskManager getFileBackedTaskManager() {
        try {
            file = File.createTempFile("data", "csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new InMemoryHistoryManager(), file);
        Task newTask = new Task("Title", "Task Description", null, TaskStatus.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2021, 1, 1, 1, 1));
        fileBackedTaskManager.addTask(newTask);
        EpicTask epic = new EpicTask("Epic Title", "Epic desc", null);
        fileBackedTaskManager.addTask(epic);
        SubTask subTask = new SubTask("Sub Title", "Sub desc", null,
                TaskStatus.DONE, epic.getId(), Duration.ofHours(1),
                LocalDateTime.of(2020, 1, 1, 1, 1));
        fileBackedTaskManager.addTask(subTask);
        return fileBackedTaskManager;
    }
}