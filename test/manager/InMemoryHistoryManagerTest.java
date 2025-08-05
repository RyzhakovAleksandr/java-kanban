package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ofMinutes(47), LocalDateTime.of(2025, 8, 4, 10, 32));
        task2 = new Task("Task 2", "Description 2", TaskStatus.IN_PROGRESS, Duration.ofMinutes(27), LocalDateTime.of(2025, 8, 5, 10, 38));
        task3 = new Task("Task 3", "Description 3", TaskStatus.DONE, Duration.ofMinutes(27), LocalDateTime.of(2025, 8, 6, 10, 39));

        // Устанавливаем ID для предсказуемости тестов
        task1.setTaskID(1);
        task2.setTaskID(2);
        task3.setTaskID(3);
    }

    @Test
    void shouldAddTaskToHistory() {
        historyManager.add(task1);
        List<Task> history = historyManager.getTasks();

        assertEquals(1, history.size());
        assertEquals(task1, history.getFirst());
    }

    @Test
    void shouldNotAddNullTask() {
        historyManager.add(null);
        assertEquals(0, historyManager.getTasks().size());
    }

    @Test
    void shouldMoveTaskToEnd() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getTasks();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task1, history.get(1));
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getTaskID());

        List<Task> history = historyManager.getTasks();
        assertEquals(1, history.size());
        assertEquals(task2, history.getFirst());
    }

    @Test
    void shouldNotFailWhenRemoving() {
        historyManager.add(task1);
        historyManager.remove(999); // Несуществующий ID

        assertEquals(1, historyManager.getTasks().size());
    }

    @Test
    void shouldReturnEmptyList() {
        List<Task> history = historyManager.getTasks();
        assertTrue(history.isEmpty());
    }

    @Test
    void shouldReturnTasks() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getTasks();
        assertEquals(3, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
        assertEquals(task3, history.get(2));
    }


    @Test
    void integrationTest() {
        // Добавляем задачи
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        // Проверяем начальное состояние
        assertEquals(3, historyManager.getTasks().size());

        // Удаляем задачу из середины
        historyManager.remove(task2.getTaskID());
        List<Task> history = historyManager.getTasks();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));

        // Добавляем задачу снова (должна переместиться в конец)
        historyManager.add(task1);
        history = historyManager.getTasks();
        assertEquals(2, history.size());
        assertEquals(task3, history.get(0));
        assertEquals(task1, history.get(1));

        // Добавляем новую задачу
        historyManager.add(task2);
        history = historyManager.getTasks();
        assertEquals(3, history.size());
        assertEquals(task3, history.get(0));
        assertEquals(task1, history.get(1));
        assertEquals(task2, history.get(2));
    }
}