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

public class EpicHttpHandlerTest {
    private HttpTaskServer httpTaskServer;
    private TaskManager taskManager;
    private EpicTask epic;
    private static final String BASE_URI = "http://localhost:8080/epics";

    @BeforeEach
    public void setup() {
        epic = new EpicTask("Epic",
                "epic desc",
                null);
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    @AfterEach
    public void teardown() {
        httpTaskServer.stop();
    }


    @Test
    public void testPostEpic() throws IOException, InterruptedException {
        String taskJson = httpTaskServer.getGson().toJson(epic);
        URI uri = URI.create(BASE_URI);
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());
            List<EpicTask> tasksFromTaskManager = taskManager.getAllEpicsTasks();
            assertEquals(1, tasksFromTaskManager.size());
            assertEquals(epic.getTitle(), tasksFromTaskManager.getFirst().getTitle());
        }
    }

    @Test
    public void testGetAllSubtasksOfEpic() throws IOException, InterruptedException {
        taskManager.addTask(epic);
        taskManager.addTask(new SubTask("Task",
                "task desc",
                null,
                TaskStatus.NEW,
                0,
                Duration.ofMinutes(10),
                LocalDateTime.of(2021, 1, 1, 1, 1)));

        URI uri = URI.create(BASE_URI.concat("/0/subtasks"));
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            List<SubTask> tasks = httpTaskServer.getGson().fromJson(response.body(),
                    new TypeToken<List<SubTask>>() {
                    }.getType());
            assertEquals(taskManager.getAllSubtasksOfEpic(0), tasks);
        }
    }

    @Test
    public void getSubtasksByInvalidEpic() throws IOException, InterruptedException {
        taskManager.addTask(epic);
        URI uri = URI.create(BASE_URI.concat("/112"));
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
        }
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        taskManager.addTask(epic);
        URI uri = URI.create(BASE_URI);
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            List<EpicTask> tasks = httpTaskServer.getGson().fromJson(response.body(),
                    new TypeToken<List<EpicTask>>() {}.getType());
            assertEquals(taskManager.getAllEpicsTasks(), tasks);
        }
    }

    @Test
    public void testGetEpicWithCorrectId() throws IOException, InterruptedException {
        taskManager.addTask(epic);
        URI uri = URI.create(BASE_URI.concat("/0"));
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            Task taskFromResponse = httpTaskServer.getGson().fromJson(response.body(), Task.class);
            assertEquals(epic.getTitle(), taskFromResponse.getTitle());
        }
    }

    @Test
    public void testGetEpicWithInvalidId() throws IOException, InterruptedException {
        taskManager.addTask(epic);
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
    public void testDeleteEpicWithCorrectId() throws IOException, InterruptedException {
        taskManager.addTask(epic);
        URI uri = URI.create(BASE_URI.concat("/0"));
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .DELETE()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertEquals(0, taskManager.getAllEpicsTasks().size());
        }
    }

    @Test
    public void testDeleteEpicWithInvalidId() throws IOException, InterruptedException {
        taskManager.addTask(epic);
        URI uri = URI.create(BASE_URI.concat("/1"));
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .DELETE()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
            assertEquals(1, taskManager.getAllEpicsTasks().size());
        }
    }
}
