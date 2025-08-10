package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

public class StandardTaskTest {

    @Test
    public void testStandardTasksEqualsWhenIdEquals() {
        Task task1 = new Task("Задача 1", "описание ", 1, TaskStatus.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2020, 1, 1, 1, 1));
        Task task2 = new Task("Задача 2", "описание 2123", 1, TaskStatus.DONE,
                Duration.ofHours(1),
                LocalDateTime.of(2020, 1, 1, 1, 1));

        Assertions.assertEquals(task1, task2);
    }

    @Test
    public void testStandardTasksNotEqualsWhenIdNotEqual() {
        Task task1 = new Task("Задача 1", "описание ", 1, TaskStatus.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2020, 1, 1, 1, 1));
        Task task2 = new Task("Задача 1", "описание ", 2, TaskStatus.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2020, 1, 1, 1, 1));

        Assertions.assertNotEquals(task1, task2);
    }

    @Test
    public void endtimeCalculating() {
        Task task1 = new Task("Задача 1", "описание ", 1, TaskStatus.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2020, 1, 1, 1, 1));
        LocalDateTime expectedEndtime = task1.getStartTime().plus(task1.getDuration());
        Assertions.assertEquals(expectedEndtime, task1.getEndTime());
    }
}
