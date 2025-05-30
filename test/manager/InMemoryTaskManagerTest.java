package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManagerTest {
    private final Managers managers = new Managers();
    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = managers.getDefault();
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
    void TestTaskEqualsTask() { // проверьте, что экземпляры класса Task равны друг другу, если равен их id;
        Task taskTest = new Task("Task1", "More information about task1", TaskStatus.NEW);
        taskManager.add(taskTest);
        Assertions.assertEquals(taskManager.get(taskManager.getSize()), taskTest);
        Assertions.assertNotEquals(taskManager.get(1), taskTest);
    }

    @Test
    void TestEpicEqualsEpic() {
        Epic epicTest = new Epic("Task1", "More information about task1");
        taskManager.add(epicTest);
        Assertions.assertEquals(taskManager.get(taskManager.getSize()), epicTest);
        Assertions.assertNotEquals(taskManager.get(1), epicTest);
    }

    @Test
    void TestSubTaskEqualsSubTask() {
        Subtask subTaskTest = new Subtask("Task1", "More information about task1", TaskStatus.NEW, new Epic("Epic1", "More information about epic1"));
        taskManager.add(subTaskTest);
        Assertions.assertEquals(taskManager.get(taskManager.getSize()), subTaskTest);
        Assertions.assertNotEquals(taskManager.get(1), subTaskTest);
    }

    @Test
    void TestErrorConvertEpicInSubTask() { // проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи
        Epic epic = new Epic("Task1", "More information about task1");
        taskManager.add(epic);
        Epic oldEpic = (Epic) taskManager.get(taskManager.getSize());
        ArrayList<Subtask> subtasks = oldEpic.getSubtasks();
        //subtasks.add(oldEpic); ошибка компиляции, под копотом реализациия которая не позволяет так сделать
    }

    @Test
    void TestErrorConvertSubTaskInEpic() { // проверьте, что объект Subtask нельзя сделать своим же эпиком;
        Subtask subtask = new Subtask("1", "1", TaskStatus.NEW, new Epic("Epic1", "More information about epic1"));
        taskManager.add(subtask);
        Subtask oldSubtask = (Subtask) taskManager.get(taskManager.getSize());
        //oldSubtask.setEpic(oldSubtask); ошибка компиляции, под копотом реализациия которая не позволяет так сделать
    }

    @Test
    void TestInitializedAndReady() { //убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;
        Managers managersTest = new Managers();
        Assertions.assertNotNull(managersTest);
        Assertions.assertNotNull(managersTest.getDefault());
        Assertions.assertNotNull(Managers.getDefaultHistory());
    }

    @Test
    void TestConflictId() { // Проверка на конфликт id
        Assertions.assertTrue(taskManager.remove(1));
        Assertions.assertTrue(taskManager.update(1, new Task("Task100", "about Task 100",  TaskStatus.NEW)));
        Assertions.assertTrue(taskManager.update(1, new Epic("Epic5", "More information about epic5")));
        Assertions.assertFalse(taskManager.update(1, new Subtask("Subtask1234", "Subtask", TaskStatus.NEW, new Epic("Epic5", "More information about epic5"))));
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
    void TestEmptyCollection() { //Проверка удаления всех задач
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
    void TestGetTaskUnknow() { //Проверка задача по id которого нет
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

    @Test
    void TestDeleteTaskById() { //Проверка удаления задачи по id
        Assertions.assertTrue(taskManager.remove(3));
        Assertions.assertNull(taskManager.get(3));
        Assertions.assertTrue(taskManager.remove(6)); // При удаления Epic, его SubTask так же должны удаляться
        Assertions.assertNull(taskManager.get(6));
        Assertions.assertEquals(8, taskManager.getSize());
    }

    @Test
    void TestDeleteTaskByIdIncorrectly() { //Проверка удаления задачи по id которого нет
        Assertions.assertFalse(taskManager.remove(0));
        Assertions.assertFalse(taskManager.remove(13));
        Assertions.assertEquals(12, taskManager.getSize());
    }

    @Test
    void TestCheckHistoryBy3() {
        taskManager.get(1);
        taskManager.get(5);
        taskManager.get(10);
        Assertions.assertEquals(3, taskManager.getHistory().size());
    }

    @Test
    void TestCheckHistoryBy0() {
        Assertions.assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    void TestCheckHistoryBy12() {
        for (int i = 1; i < taskManager.getSize() + 1; i++) {
            taskManager.get(i);
        }
        Assertions.assertEquals(10, taskManager.getHistory().size());
        Assertions.assertEquals(3, taskManager.getHistory().getFirst().getTaskID());

        taskManager.get(5);
        taskManager.get(10);

        Assertions.assertEquals(10, taskManager.getHistory().size());
        Assertions.assertEquals(10, taskManager.getHistory().getLast().getTaskID());
        Assertions.assertEquals(5, taskManager.getHistory().getFirst().getTaskID());
        Assertions.assertEquals(5, taskManager.getHistory().get(8).getTaskID());

    }

    @Test
    void TestPrintAll() { // Сценарий для проверки с сайта
        printAllTasks(taskManager);
    }

    // Сценарий для проверки с сайта
    private void printAllTasks(TaskManager taskManager) {
        System.out.println("Задачи:");
        for (Task task : taskManager.getOneType(Task.class)) {
            System.out.println(task);
        }

        System.out.println("Эпики:");
        for (Epic epic : taskManager.getOneType(Epic.class)) {
            System.out.println(epic);

            for (Subtask subtask : epic.getSubtasks()) {
                System.out.println("--> " + subtask);
            }
        }
        System.out.println("Подзадачи:");
        for (Subtask subtask : taskManager.getOneType(Subtask.class)) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
