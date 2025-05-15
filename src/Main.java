import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        String line = "-".repeat(50);
        TaskManager taskManager = new TaskManager();

        
        //Тест добавления TASK, EPIC, SUBTASK
        System.out.println("Тест добавления TASK, EPIC, SUBTASK");
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

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createEpic(epic3);
        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);
        taskManager.createSubTask(subtask3);
        taskManager.createSubTask(subtask4);
        taskManager.createSubTask(subtask5);
        taskManager.createSubTask(subtask6);

        //Объеденил печать всех Task, Epic и SubTask в один метод, для удобства использованиия
        printAllTest(taskManager, line);

        //Тест на вывод любого типа задачи по вызову get() и так же тест на изменение задачи update()
        System.out.println("Вывод и изменение задачи");
        System.out.println(taskManager.getTask(2));
        System.out.println(taskManager.getEpic(5));
        System.out.println(taskManager.getEpic(6));
        System.out.println(taskManager.getSubTask(10));
        taskManager.updateTask(2, new Task("updateTask", "information about task", TaskStatus.IN_PROGRESS));
        taskManager.updateEpic(6, new Epic("updateTask", "information about task"));
        taskManager.updateSubTask(10, new Subtask("newSubTask", "little information about sub", TaskStatus.NEW, epic2));
        System.out.println(line);
        System.out.println(taskManager.getTask(2));
        System.out.println(taskManager.getEpic(5));
        System.out.println(taskManager.getEpic(6));
        System.out.println(taskManager.getSubTask(10));
        System.out.println(line);

        //Тест на изменение статусов, согласно условию задачи у Epic статус выбирается  зависимости от статусов подзадач
        System.out.println("Проверка  статусов");
        System.out.printf("Task2 = %s", taskManager.getTask(2).getTaskStatus());
        System.out.printf("\nSubtask4 = %s", taskManager.getSubTask(10).getTaskStatus());
        System.out.printf("\nEpic2 = %s", taskManager.getEpic(5).getTaskStatus());
        System.out.println();
        System.out.println("Изменение статусов у подзадач и проверка статуса у эпиков");
        System.out.printf("\nEpic1 = %s", taskManager.getEpic(4).getTaskStatus());
        System.out.printf("\nEpic2 = %s", taskManager.getEpic(5).getTaskStatus());
        System.out.printf("\nEpic3 = %s", taskManager.getEpic(6).getTaskStatus());
        taskManager.updateSubTask(7, new Subtask("Subtask1", "More information about subtask1", TaskStatus.IN_PROGRESS, epic1));
        taskManager.updateSubTask(11, new Subtask("Subtask5", "More information about subtask5", TaskStatus.DONE, epic3));
        taskManager.updateSubTask(12, new Subtask("Subtask6", "More information about subtask6", TaskStatus.DONE, epic3));
        System.out.println();
        System.out.printf("\nEpic1 = %s", taskManager.getEpic(4).getTaskStatus());
        System.out.printf("\nEpic2 = %s", taskManager.getEpic(5).getTaskStatus());
        System.out.printf("\nEpic3 = %s\n", taskManager.getEpic(6).getTaskStatus());

        //Тест на удаление по ID
        System.out.println(line);
        System.out.println("Удаление задачи");
        System.out.println(taskManager.printAllTasks());
        taskManager.deleteTask(1);
        System.out.println(line);
        System.out.println(taskManager.printAllTasks());
        System.out.println("Удаление эпика");
        System.out.println(taskManager.printAllEpics());
        taskManager.deleteEpic(6);
        System.out.println(line);
        System.out.println(taskManager.printAllEpics());
        System.out.println(taskManager.printAllSubtasks());
        System.out.println("Удаление подзадачи");
        System.out.println(taskManager.printAllSubtasks());
        taskManager.deleteSubTask(10);
        System.out.println(line);
        System.out.println(taskManager.printAllSubtasks());

        //Тест на удаление всех задач
        System.out.println("Удаление всех задач");
        taskManager.deleteAllTask();
        printAllTest(taskManager, line);

    }

    // Метод вывода всех задач(используется несколько раз в тестах)
    private static void printAllTest(TaskManager taskManager, String line) {
        System.out.println(taskManager.printAllTasks());
        System.out.println(line);
        System.out.println(taskManager.printAllEpics());
        System.out.println(line);
        System.out.println(taskManager.printAllSubtasks());
        System.out.println(line);
    }
}
