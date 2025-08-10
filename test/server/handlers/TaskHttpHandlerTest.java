package server.handlers;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import managers.history.InMemoryHistoryManager;
import managers.task.InMemoryTaskManager;
import managers.task.TaskManager;
import server.HttpTaskServer;
import task.Task;
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

class TaskHttpHandlerTest {
    public static final String BASE_URI = "http://localhost:8080/tasks";
    private HttpTaskServer httpTaskServer;
    private TaskManager taskManager;
    private Task task;

    @BeforeEach
    public void setup() {
        task = new Task("Task",
                "task desc",
                null,
                TaskStatus.NEW,
                Duration.ofMinutes(10),
                LocalDateTime.of(2021, 1, 1, 1, 1));
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    @AfterEach
    public void teardown() {
        httpTaskServer.stop();
    }

    @Test
    public void testPostTaskWithOverlap() throws IOException, InterruptedException {
        taskManager.addTask(task);
        Task newTask = (Task) task.clone();
        newTask.setId(null);
        String taskJson = httpTaskServer.getGson().toJson(newTask);
        URI uri = URI.create(BASE_URI);
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(406, response.statusCode());
            List<Task> tasksFromTaskManager = taskManager.getAllStandardTasks();
            assertEquals(1, tasksFromTaskManager.size());
        }
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        taskManager.addTask(task);
        Task newTask = (Task) task.clone();
        newTask.setTitle("new title");
        String taskJson = httpTaskServer.getGson().toJson(newTask);
        URI uri = URI.create(BASE_URI);
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            List<Task> tasksFromTaskManager = taskManager.getAllStandardTasks();
            assertEquals(1, tasksFromTaskManager.size());
            assertNotEquals(task.getTitle(), tasksFromTaskManager.getFirst().getTitle());
        }
    }

    @Test
    public void testPostTask() throws IOException, InterruptedException {
        String taskJson = httpTaskServer.getGson().toJson(task);
        URI uri = URI.create(BASE_URI);
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());
            List<Task> tasksFromTaskManager = taskManager.getAllStandardTasks();
            assertEquals(1, tasksFromTaskManager.size());
            assertEquals(task.getTitle(), tasksFromTaskManager.getFirst().getTitle());
        }
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        taskManager.addTask(task);
        URI uri = URI.create(BASE_URI);
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            List<Task> tasks = httpTaskServer.getGson().fromJson(response.body(),
                    new TypeToken<List<Task>>() {
                    }.getType());
            assertEquals(taskManager.getAllStandardTasks(), tasks);
        }
    }

    @Test
    public void testGetTaskWithCorrectId() throws IOException, InterruptedException {
        taskManager.addTask(task);
        URI uri = URI.create(BASE_URI.concat("/0"));
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            Task taskFromResponse = httpTaskServer.getGson().fromJson(response.body(), Task.class);
            assertEquals(task.getTitle(), taskFromResponse.getTitle());
        }
    }

    @Test
    public void testGetTaskWithInvalidId() throws IOException, InterruptedException {
        taskManager.addTask(task);
        URI uri = URI.create(BASE_URI.concat("/1"));
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
        }
    }

    @Test
    public void testDeleteTaskWithCorrectId() throws IOException, InterruptedException {
        taskManager.addTask(task);
        URI uri = URI.create(BASE_URI.concat("/0"));
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .DELETE()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertEquals(0, taskManager.getAllStandardTasks().size());
        }
    }

    @Test
    public void testDeleteTaskWithInvalidId() throws IOException, InterruptedException {
        taskManager.addTask(task);
        URI uri = URI.create(BASE_URI.concat("/1"));
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .DELETE()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
            assertEquals(1, taskManager.getAllStandardTasks().size());
        }
    }
}