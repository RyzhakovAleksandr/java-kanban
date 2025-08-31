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

public class PrioritizedHttpHandlerTest {
    private HttpTaskServer httpTaskServer;
    private TaskManager taskManager;
    private Task task1;
    private Task task2;
    private static final String BASE_URI = "http://localhost:8080/history";

    @BeforeEach
    public void setup() {
        task1 = new Task("Task",
                "task desc",
                null,
                TaskStatus.NEW,
                Duration.ofMinutes(10),
                LocalDateTime.of(2021, 1, 1, 1, 1));
        task2 = new Task("Task2",
                "task2 desc",
                null,
                TaskStatus.NEW,
                Duration.ofMinutes(10),
                LocalDateTime.of(2022, 1, 1, 1, 1));
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    @AfterEach
    public void teardown() {
        httpTaskServer.stop();
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        taskManager.addTask(task1);
        taskManager.getById(task1.getId());
        taskManager.addTask(task2);
        taskManager.getById(task2.getId());
        URI uri = URI.create(BASE_URI);
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            List<Task> tasks = httpTaskServer.getGson().fromJson(response.body(),
                    new TypeToken<List<Task>>() {}.getType());
            assertEquals(taskManager.getPrioritizedTasks(), tasks);
        }
    }
}
