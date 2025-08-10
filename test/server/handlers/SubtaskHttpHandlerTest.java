package server.handlers;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import managers.history.InMemoryHistoryManager;
import managers.task.InMemoryTaskManager;
import managers.task.TaskManager;
import server.HttpTaskServer;
import task.EpicTask;
import task.SubTask;
import task.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SubtaskHttpHandlerTest {
    private HttpTaskServer httpTaskServer;
    private TaskManager taskManager;
    private SubTask subtask;
    private static final String BASE_URI = "http://localhost:8080/subtasks";

    @BeforeEach
    public void setup() {
        subtask = new SubTask("Task",
                "task desc",
                null,
                TaskStatus.NEW,
                0,
                Duration.ofMinutes(10),
                LocalDateTime.of(2021, 1, 1, 1, 1));
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        taskManager.addTask(new EpicTask("epic", "epic desc", null));
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    @AfterEach
    public void teardown() {
        httpTaskServer.stop();
    }

    @Test
    public void PostSubTask_return406() throws IOException, InterruptedException {
        taskManager.addTask(subtask);
        SubTask newTask = (SubTask) subtask.clone();
        newTask.setId(null);
        String taskJson = httpTaskServer.getGson().toJson(newTask);
        URI uri = URI.create(BASE_URI);
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(406, response.statusCode());
            List<SubTask> tasksFromTaskManager = taskManager.getAllSubTasks();
            assertEquals(1, tasksFromTaskManager.size());
        }
    }

    @Test
    public void UpdateSubTask_return200() throws IOException, InterruptedException {
        taskManager.addTask(subtask);
        SubTask newTask = (SubTask) subtask.clone();
        newTask.setTitle("new title");
        String taskJson = httpTaskServer.getGson().toJson(newTask);
        URI uri = URI.create(BASE_URI);
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            List<SubTask> tasksFromTaskManager = taskManager.getAllSubTasks();
            assertEquals(1, tasksFromTaskManager.size());
            assertNotEquals(subtask.getTitle(), tasksFromTaskManager.getFirst().getTitle());
        }
    }

    @Test
    public void PostSubTask_return201() throws IOException, InterruptedException {
        String taskJson = httpTaskServer.getGson().toJson(subtask);
        URI uri = URI.create(BASE_URI);
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());
            List<SubTask> tasksFromTaskManager = taskManager.getAllSubTasks();
            assertEquals(1, tasksFromTaskManager.size());
            assertEquals(subtask.getTitle(), tasksFromTaskManager.getFirst().getTitle());
        }
    }

    @Test
    public void testGetSubTasks() throws IOException, InterruptedException {
        taskManager.addTask(subtask);
        URI uri = URI.create(BASE_URI);
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            List<SubTask> tasks = httpTaskServer.getGson().fromJson(response.body(),
                    new TypeToken<List<SubTask>>() {
                    }.getType());
            assertEquals(taskManager.getAllSubTasks(), tasks);
        }
    }

    @Test
    public void getSubTaskById() throws IOException, InterruptedException {
        taskManager.addTask(subtask);
        URI uri = URI.create(BASE_URI.concat("/1"));
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            SubTask taskFromResponse = httpTaskServer.getGson().fromJson(response.body(), SubTask.class);
            assertEquals(subtask.getTitle(), taskFromResponse.getTitle());
        }
    }

    @Test
    public void getInvalidSubTask() throws IOException, InterruptedException {
        taskManager.addTask(subtask);
        URI uri = URI.create(BASE_URI.concat("/2"));
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
        }
    }

    @Test
    public void deleteSubTask() throws IOException, InterruptedException {
        taskManager.addTask(subtask);
        URI uri = URI.create(BASE_URI.concat("/1"));
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .DELETE()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertEquals(0, taskManager.getAllSubTasks().size());
        }
    }

    @Test
    public void deleteInvalidSubTask() throws IOException, InterruptedException {
        taskManager.addTask(subtask);
        URI uri = URI.create(BASE_URI.concat("/2"));
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .DELETE()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
            assertEquals(1, taskManager.getAllSubTasks().size());
        }
    }
}
