package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.List;

public class InMemoryTaskManagerTest {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
        Task task1 = new Task("Task1", "More information about task1", TaskStatus.NEW);
        Task task2 = new Task("Task2", "More information about task2", TaskStatus.NEW);
        Task task3 = new Task("Task3", "More information about task3", TaskStatus.NEW);
        Epic epic1 = new Epic("Epic1", "More information about epic1");
        Epic epic2 = new Epic("Epic2", "More information about epic2");
        Epic epic3 = new Epic("Epic3", "More information about epic3");
        Subtask subtask1 = new Subtask("Subtask1", "More information about subtask1", TaskStatus.NEW, epic1);
        Subtask subtask2 = new Subtask("Subtask2", "More information about subtask2", TaskStatus.NEW, epic1);
        Subtask subtask3 = new Subtask("Subtask3", "More information about subtask3", TaskStatus.NEW, epic1);
        Subtask subtask4 = new Subtask("Subtask4", "More information about subtask4", TaskStatus.DONE, epic2);
        Subtask subtask5 = new Subtask("Subtask5", "More information about subtask5", TaskStatus.NEW, epic3);
        Subtask subtask6 = new Subtask("Subtask6", "More information about subtask6", TaskStatus.NEW, epic3);

        taskManager.add(task1);
        taskManager.add(task2);
        taskManager.add(task3);
        taskManager.add(epic1);
        taskManager.add(epic2);
        taskManager.add(epic3);
        taskManager.add(subtask1);
        taskManager.add(subtask2);
        taskManager.add(subtask3);
        taskManager.add(subtask4);
        taskManager.add(subtask5);
        taskManager.add(subtask6);
    }

    @Test
    void TestSizeAfterAddingSpecifiedAmount() { // Проверка на добавление
        Assertions.assertEquals(12, taskManager.getSizeMap());
        Assertions.assertTrue(taskManager.add(new Task("TaskTest", "More information about taskTest", TaskStatus.NEW)));
        Assertions.assertEquals(13, taskManager.getSizeMap());
    }

    @Test
    void TestFalseIfAddedNull() { // Проверка добавления пустой задачи
        Assertions.assertFalse(taskManager.add(null));
    }

    @Test
    void TestGetAllTasks() { //Проверка вывода всех задач типа Task
        List<Task> tasks = taskManager.getOneType(Task.class);
        Assertions.assertEquals(3, tasks.size());
        Assertions.assertEquals(taskManager.get(1), tasks.getFirst());
    }

    @Test
    void TestGetAllEpics() { //Проверка вывода всех задач типа Epic
        List<Epic> epics = taskManager.getOneType(Epic.class);
        Assertions.assertEquals(3, epics.size());
        Assertions.assertEquals(taskManager.get(5), epics.get(1));
    }

    @Test
    void TestGetAllSubTasks() { //Проверка вывода всех задач типа SubTask
        List<Subtask> subtasks = taskManager.getOneType(Subtask.class);
        Assertions.assertEquals(6, subtasks.size());
        Assertions.assertEquals(taskManager.get(10), subtasks.get(3));
    }

    @Test
    void TestEmptyCollectionAfterDeletedAllTasks() { //Проверка удаления всех задач
        taskManager.deleteAllTask();
        List<Epic> epics = taskManager.getOneType(Epic.class);
        Assertions.assertTrue(epics.isEmpty());
    }

    @Test
    void TestGetAll() { //Проверка вывода всех задач
        List<Task> taskList = taskManager.getAll();
        Assertions.assertEquals(12, taskList.size());
        Assertions.assertEquals(taskManager.get(1), taskList.getFirst());
        Assertions.assertEquals(taskManager.get(5), taskList.get(4));
        Assertions.assertEquals(taskManager.get(12), taskList.getLast());
    }

    @Test
    void TestGetTaskById() { //Проверка задача по id
        Assertions.assertEquals(1, taskManager.get(1).getTaskID());
        Assertions.assertEquals(5, taskManager.get(5).getTaskID());
        Assertions.assertEquals(12, taskManager.get(12).getTaskID());
        Assertions.assertEquals("Subtask5", taskManager.get(11).getTaskName());
    }

    @Test
    void TestGetTaskUnknowByIdIncorrectly() { //Проверка задача по id которого нет
        Assertions.assertEquals(TaskStatus.UNKNOWN, taskManager.get(0).getTaskStatus());
        Assertions.assertEquals(TaskStatus.UNKNOWN, taskManager.get(13).getTaskStatus());
    }

    @Test
    void TestUpdateTask() { // Проверка апгрейда Task
        Assertions.assertEquals("Task1", taskManager.get(1).getTaskName());
        Assertions.assertTrue(taskManager.update(1, new Task("TaskNew1", "More information about task1", TaskStatus.DONE)));
        Assertions.assertEquals(TaskStatus.DONE, taskManager.get(1).getTaskStatus());
        Assertions.assertEquals("TaskNew1", taskManager.get(1).getTaskName());
    }

    @Test
    void TestErrorUpdateTaskChangesEpic() { // Проверка апгрейда Epic на Task
        Assertions.assertEquals("Epic1", taskManager.get(4).getTaskName());
        Assertions.assertFalse(taskManager.update(4, new Task("TaskNew1", "More information about task1", TaskStatus.DONE)));
        Assertions.assertEquals("Epic1", taskManager.get(4).getTaskName());
    }

    @Test
    void TestErrorUpdateTaskChangesSubTask() { // Проверка апгрейда Task на SubTask
        Assertions.assertEquals("Subtask4", taskManager.get(10).getTaskName());
        Assertions.assertFalse(taskManager.update(4, new Task("TaskNew1", "More information about task1", TaskStatus.DONE)));
        Assertions.assertEquals("Subtask4", taskManager.get(10).getTaskName());
    }

    @Test
    void TestUpdateEpicChangesTask() { // Проверка апгрейда Task на Epic
        Assertions.assertEquals("Task1", taskManager.get(1).getTaskName());
        Assertions.assertTrue(taskManager.update(1, new Epic("Epic", "Epic changes task")));
        Assertions.assertEquals(TaskStatus.NEW, taskManager.get(1).getTaskStatus());
        Assertions.assertEquals("Epic", taskManager.get(1).getTaskName());
    }

    @Test
    void TestUpdateEpic() { // Проверка апгрейда Epic
        Assertions.assertEquals("Epic1", taskManager.get(4).getTaskName());
        Assertions.assertTrue(taskManager.update(4, new Epic("OtherEpic", "More information about OtherEpic")));
        Assertions.assertEquals(TaskStatus.NEW, taskManager.get(4).getTaskStatus());
        Assertions.assertEquals("OtherEpic", taskManager.get(4).getTaskName());
    }

    @Test
    void TestUpdateSubTask() { // Проверка апгрейда SubTask
        Assertions.assertEquals(TaskStatus.NEW, taskManager.get(11).getTaskStatus());
        Assertions.assertTrue(taskManager.update(11, new Subtask("SubSub", "More information about sub", TaskStatus.IN_PROGRESS, (Epic) taskManager.get(5))));
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.get(11).getTaskStatus());
        Assertions.assertEquals("SubSub", taskManager.get(11).getTaskName());
    }

    @Test
    void TestUpdateStatusEpic() { // Проверка апгрейда статуса у Epic
        Assertions.assertEquals(TaskStatus.NEW, taskManager.get(4).getTaskStatus()); //Статус Epic
        Assertions.assertEquals(TaskStatus.NEW, taskManager.get(7).getTaskStatus()); //Статус SubTask
        Assertions.assertEquals(TaskStatus.NEW, taskManager.get(8).getTaskStatus()); //Статус SubTask
        Assertions.assertEquals(TaskStatus.NEW, taskManager.get(9).getTaskStatus()); //Статус SubTask
        Assertions.assertTrue(taskManager.update(8, new Subtask("SubSub", "More information about sub", TaskStatus.DONE, (Epic) taskManager.get(4))));
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.get(4).getTaskStatus()); //Статус Epic стал IN_PROGRESS потому что изменился статус у подзадачи
        Assertions.assertTrue(taskManager.update(7, new Subtask("SubSub", "More information about sub", TaskStatus.DONE, (Epic) taskManager.get(4))));
        Assertions.assertTrue(taskManager.update(9, new Subtask("SubSub", "More information about sub", TaskStatus.DONE, (Epic) taskManager.get(4))));
        Assertions.assertEquals(TaskStatus.DONE, taskManager.get(4).getTaskStatus()); //Статус Epic стал DONE потому что все подзадачи выполнены
    }
}
