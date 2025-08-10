package utils;

import org.junit.jupiter.api.Test;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CSVTaskFormatterTest {

    @Test
    void taskToCSVString() {
        Task task = new Task("Title", "Task Description", 3, TaskStatus.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2020, 1, 1, 1, 1));
        String csvString = CSVTaskFormatter.toCSVString(task);
        assertEquals("3,TASK,Title,NEW,Task Description,60,2020-01-01 01:01:00, ", csvString);
    }

    @Test
    void subTaskToCSVString() {
        SubTask task = new SubTask("Title", "Task Description", 3, TaskStatus.NEW, 11,
                Duration.ofHours(1),
                LocalDateTime.of(2020, 1, 1, 1, 1));
        String csvString = CSVTaskFormatter.toCSVString(task);
        assertEquals("3,SUBTASK,Title,NEW,Task Description,60,2020-01-01 01:01:00,11", csvString);
    }

    @Test
    void epicTaskToCSVString() {
        EpicTask task = new EpicTask("Title", "Epic Task Description", 3);
        SubTask subtask = new SubTask("Title", "Task Description", 2, TaskStatus.NEW, 3,
                Duration.ofHours(1),
                LocalDateTime.of(2020, 1, 1, 1, 1));
        task.addSubTask(subtask);
        String csvString = CSVTaskFormatter.toCSVString(task);
        assertEquals("3,EPICTASK,Title,NEW,Epic Task Description,60,2020-01-01 01:01:00, ", csvString);
    }

    @Test
    void taskFromCSVString() {
        Task task = new Task("Title", "Task Description", 3, TaskStatus.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2020, 1, 1, 1, 1));
        String csvString = CSVTaskFormatter.toCSVString(task);
        BaseTask taskFromCsv = CSVTaskFormatter.fromCSVString(csvString);
        assertEquals(task.getTitle(), taskFromCsv.getTitle());
        assertEquals(task.getDescription(), taskFromCsv.getDescription());
        assertEquals(task.getStatus(), taskFromCsv.getStatus());
        assertEquals(task.getId(), taskFromCsv.getId());
    }

    @Test
    void subTaskFromCSVString() {
        SubTask task = new SubTask("Title", "Task Description", 3, TaskStatus.NEW, 11,
                Duration.ofHours(1),
                LocalDateTime.of(2020, 1, 1, 1, 1));
        String csvString = CSVTaskFormatter.toCSVString(task);
        SubTask taskFromCsv = (SubTask) CSVTaskFormatter.fromCSVString(csvString);
        assertEquals(task.getTitle(), taskFromCsv.getTitle());
        assertEquals(task.getDescription(), taskFromCsv.getDescription());
        assertEquals(task.getStatus(), taskFromCsv.getStatus());
        assertEquals(task.getId(), taskFromCsv.getId());
        assertEquals(task.getEpicTaskId(), taskFromCsv.getEpicTaskId());
        assertEquals(task.getDuration(), taskFromCsv.getDuration());
        assertEquals(task.getStartTime(), taskFromCsv.getStartTime());

    }

    @Test
    void EpicTaskFromCSVString() {
        EpicTask task = new EpicTask("Title", "Epic Task Description", 3);
        String csvString = CSVTaskFormatter.toCSVString(task);
        BaseTask taskFromCsv = CSVTaskFormatter.fromCSVString(csvString);
        assertEquals(task.getTitle(), taskFromCsv.getTitle());
        assertEquals(task.getDescription(), taskFromCsv.getDescription());
        assertEquals(task.getStatus(), taskFromCsv.getStatus());
        assertEquals(task.getId(), taskFromCsv.getId());
        assertDoesNotThrow(() -> {
            EpicTask epic = (EpicTask) taskFromCsv;
        });
    }

    @Test
    void idStringToList() {
        String ids = "1,2,3,4";
        List<Integer> expectedIntegerList = List.of(1, 2, 3, 4);
        List<Integer> actualIntegerList = CSVTaskFormatter.idStringToList(ids);
        assertEquals(expectedIntegerList, actualIntegerList);
    }

    @Test
    void tasksToIdString() {
        Task task1 = new Task("Title", "Task Description", 2, TaskStatus.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2020, 1, 1, 1, 1));
        Task task2 = new Task("Title", "Task Description", 3, TaskStatus.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2020, 1, 1, 1, 1));
        Task task3 = new Task("Title", "Task Description", 4, TaskStatus.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2020, 1, 1, 1, 1));
        List<BaseTask> tasks = List.of(task1, task2, task3);

        String expectedString = "2,3,4";
        String actualString = CSVTaskFormatter.tasksToIdString(tasks);

        assertEquals(expectedString, actualString);
    }

    @Test
    void idStringToListWithEmptyString() {
        String ids = "";
        List<Integer> expectedIntegerList = List.of();
        List<Integer> actualIntegerList = CSVTaskFormatter.idStringToList(ids);
        assertEquals(expectedIntegerList, actualIntegerList);
    }

    @Test
    void tasksToIdStringWithEmptyList() {
        String ids = "";
        List<Integer> expectedIntegerList = List.of();
        List<Integer> actualIntegerList = CSVTaskFormatter.idStringToList(ids);
        assertEquals(expectedIntegerList, actualIntegerList);
    }
}