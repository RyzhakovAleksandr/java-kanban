package managers.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import managers.exceptions.TaskIntersectException;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected BaseTask task1;
    protected BaseTask task2;


    @BeforeEach
    void setup() {
        task1 = new Task("Задача 1",
                "описание",
                null,
                TaskStatus.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2020, 1, 1, 1, 1));
        task2 = new Task("Задача 2",
                "описание",
                null,
                TaskStatus.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2021, 1, 1, 1, 1));
        taskManager = createTaskManager();
    }

    public abstract T createTaskManager();


    @Test
    public void subTaskWithEpicId() {
        SubTask subTask = new SubTask("Подзадача 1", "описание",
                null, TaskStatus.NEW, 0, Duration.ofHours(1),
                LocalDateTime.of(2020, 1, 1, 1, 1));

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                taskManager.addTask(subTask)
        );
    }

    @Test
    public void subTaskWithNullEpicId() {
        SubTask subTask = new SubTask("Подзадача 1", "описание",
                null, TaskStatus.NEW, null, Duration.ofHours(1),
                LocalDateTime.of(2020, 1, 1, 1, 1));
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                taskManager.addTask(subTask)
        );
    }

    @Test
    public void taskUnchangedAfterAdd() {
        taskManager.addTask(task1);
        Assertions.assertEquals(TaskStatus.NEW, task1.getStatus());
        Assertions.assertEquals("Задача 1", task1.getTitle());
        Assertions.assertEquals("описание", task1.getDescription());
        Assertions.assertEquals(Duration.ofHours(1), task1.getDuration());
        Assertions.assertEquals(LocalDateTime.of(2020, 1, 1, 1, 1), task1.getStartTime());
        Assertions.assertNotNull(task1.getId());
    }

    @Test
    public void standardTaskAdded() {
        taskManager.addTask(task1);

        List<Task> standardTasks = taskManager.getAllStandardTasks();
        Assertions.assertEquals(1, standardTasks.size());
    }

    @Test
    public void epicTaskAdded() {
        EpicTask epic = new EpicTask("Задача 1", "описание", null);
        taskManager.addTask(epic);

        List<EpicTask> standardTasks = taskManager.getAllEpicsTasks();
        Assertions.assertEquals(1, standardTasks.size());
        Assertions.assertEquals(epic, taskManager.getById(0).get());
    }

    @Test
    public void taskWithCustomId() {
        Task task = new Task("Задача 1", "описание", 123, TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2020, 1, 1, 1, 1));
        taskManager.addTask(task);

        Assertions.assertEquals(0, task.getId());
    }

    @Test
    public void getTaskById() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Assertions.assertEquals(task2, taskManager.getById(1).get());
    }

    @Test
    public void removeInvalidId() {
        taskManager.addTask(task1);

        Assertions.assertThrows(IllegalArgumentException.class, ()
                -> taskManager.removeById(123));
        Assertions.assertEquals(1, taskManager.getAllStandardTasks().size());
    }

    @Test
    public void historyOnGetTask() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.getById(task1.getId());
        taskManager.getById(task2.getId());
        List<BaseTask> history = taskManager.getHistory();

        Assertions.assertEquals(2, history.size());
    }

    @Test
    public void historyOnRemoveTask() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.getById(task1.getId());
        taskManager.getById(task2.getId());
        taskManager.removeById(task2.getId());
        List<BaseTask> history = taskManager.getHistory();
        Assertions.assertEquals(1, history.size());
    }

    @Test
    public void taskWithoutTime_notPrioritized() {
        Task task1 = new Task("Задача 1", "описание", null, TaskStatus.NEW, Duration.ofHours(1),
                null);
        taskManager.addTask(task1);
        int expectedSize = 0;
        int actualSize = taskManager.getPrioritizedTasks().size();
        Assertions.assertEquals(expectedSize, actualSize);
    }

    @Test
    public void tasksPrioritized() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        List<BaseTask> prioritizedTasks = taskManager.getPrioritizedTasks();
        Assertions.assertEquals(task1, prioritizedTasks.get(0));
        Assertions.assertEquals(task2, prioritizedTasks.get(1));
    }

    @Test
    public void intersectingTasks() {
        Task task1 = new Task("Задача 1", "описание", null, TaskStatus.NEW, Duration.ofHours(4),
                LocalDateTime.of(2020, 1, 1, 1, 1));
        Task task2 = new Task("Задача 2", "описание", null, TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2020, 1, 1, 1, 1));
        taskManager.addTask(task1);
        Assertions.assertThrows(TaskIntersectException.class, () -> taskManager.addTask(task2));
    }

    @Test
    public void intersectingSubTasks() {
        EpicTask epic = new EpicTask("Эпик", "описание", null);
        taskManager.addTask(epic);
        SubTask subTask1 = new SubTask("Задача 1", "описание", null, TaskStatus.NEW, 0,
                Duration.ofHours(4),
                LocalDateTime.of(2020, 1, 1, 1, 1));
        SubTask subTask2 = new SubTask("Задача 2", "описание", null, TaskStatus.NEW, 0,
                Duration.ofHours(1),
                LocalDateTime.of(2020, 1, 1, 1, 1));
        taskManager.addTask(subTask1);
        Assertions.assertThrows(TaskIntersectException.class, () -> taskManager.addTask(subTask2));
    }
}