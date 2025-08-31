package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

public class EpicTaskTest {

    @Test
    void testEpicTasksEqualsWhenIdEquals() {
        EpicTask epicTask1 = new EpicTask("Эпик 1", "Описание 1", 1);
        EpicTask epicTask2 = new EpicTask("Эпик 2", "Описание 2", 1);

        Assertions.assertEquals(epicTask1, epicTask2);
    }

    @Test
    void testEpicTasksNotEqualsWhenIdNotEqual() {
        EpicTask epicTask1 = new EpicTask("Эпик 1", "Описание 1", 1);
        EpicTask epicTask2 = new EpicTask("Эпик 1", "Описание 1", 2);

        Assertions.assertNotEquals(epicTask1, epicTask2);
    }

    @Test
    public void checkCalculationOfInnerState() {
        EpicTask epicTask = getEpicTaskWithTwoSubTasks(TaskStatus.DONE, TaskStatus.DONE);
        Duration expectedDuration = Duration.ofHours(2).plus(Duration.ofHours(3));
        LocalDateTime expectedStartTime = LocalDateTime.of(2015, 1,1,1, 1, 1);
        LocalDateTime expectedEndTime = LocalDateTime.of(2020, 1,1,1, 1, 1)
                .plus(Duration.ofHours(2));
        Assertions.assertEquals(expectedDuration, epicTask.getDuration());
        Assertions.assertEquals(expectedEndTime, epicTask.getEndTime());
        Assertions.assertEquals(expectedStartTime, epicTask.getStartTime());
    }

    @Test
    public void statusShouldBeNew() {
        EpicTask epicTask = getEpicTaskWithTwoSubTasks(TaskStatus.NEW, TaskStatus.NEW);
        Assertions.assertEquals(TaskStatus.NEW, epicTask.getStatus());
    }

    @Test
    public void statusShouldBeDone() {
        EpicTask epicTask = getEpicTaskWithTwoSubTasks(TaskStatus.DONE, TaskStatus.DONE);
        Assertions.assertEquals(TaskStatus.DONE, epicTask.getStatus());
    }

    @Test
    public void statusShouldBeInProgressWithNewAndDoneSubs() {
        EpicTask epicTask = getEpicTaskWithTwoSubTasks(TaskStatus.NEW, TaskStatus.DONE);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epicTask.getStatus());
    }

    @Test
    public void statusShouldBeInProgress() {
        EpicTask epicTask = getEpicTaskWithTwoSubTasks(TaskStatus.IN_PROGRESS, TaskStatus.IN_PROGRESS);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epicTask.getStatus());
    }

    private static EpicTask getEpicTaskWithTwoSubTasks(TaskStatus status1, TaskStatus status2) {
        EpicTask epicTask = new EpicTask("Эпик 1", "Описание 1", 1);
        SubTask subTask1 = new SubTask("Подзадача 1",
                "Описание 1",
                2,
                status1,
                1,
                Duration.ofHours(2),
                LocalDateTime.of(2020, 1,1,1, 1, 1));
        SubTask subTask2 = new SubTask("Подзадача 1",
                "Описание 1",
                3,
                status2,
                1,
                Duration.ofHours(3),
                LocalDateTime.of(2015, 1,1,1, 1, 1));
        epicTask.addSubTask(subTask1);
        epicTask.addSubTask(subTask2);
        return epicTask;
    }
}
