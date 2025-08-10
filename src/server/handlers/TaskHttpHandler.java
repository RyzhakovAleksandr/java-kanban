package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import managers.exceptions.TaskIntersectException;
import managers.task.TaskManager;
import task.Task;

import java.util.List;

public class TaskHttpHandler extends BaseHttpHandler {
    public TaskHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) {
        switch (exchange.getRequestMethod()) {
            case "GET" -> handleGet(exchange);
            case "POST" -> handlePost(exchange);
            case "DELETE" -> handleDelete(exchange);
            default -> sendResponseBadRequest(exchange);
        }
    }

    private void handleGet(HttpExchange exchange) {
        String[] pathParts = splitURI(exchange);
        switch (pathParts.length) {
            case 2 -> {
                List<Task> allTasks = taskManager.getAllStandardTasks();
                String allTasksJson = gson.toJson(allTasks);
                sendResponse(exchange, allTasksJson, 200);
            }
            case 3 -> handleGetTaskById(exchange, pathParts);
            default -> sendResponseNotFound(exchange, "Bad Request");
        }
    }

    private void handlePost(HttpExchange exchange) {
        String[] pathParts = splitURI(exchange);
        if (pathParts.length != 2) {
            sendResponseNotFound(exchange, "Bad Request");
            return;
        }
        String body = getStringFromRequestBody(exchange);
        Task taskFromRequest;
        try {
            taskFromRequest = gson.fromJson(body, Task.class);
        } catch (JsonSyntaxException e) {
            sendResponseNotFound(exchange, "No request body provided");
            return;
        }

        if (taskFromRequest.getId() != null) {
            try {
                taskManager.updateTask(taskFromRequest);
            } catch (TaskIntersectException e) {
                sendResponseHasOverlaps(exchange, e.getMessage());
            }
            sendResponse(exchange,
                    "Task with id %d successfully updated".formatted(taskFromRequest.getId()),
                    200);
        } else {
            try {
                taskManager.addTask(taskFromRequest);
            } catch (TaskIntersectException e) {
                sendResponseHasOverlaps(exchange, e.getMessage());
                return;
            }
            sendResponse(exchange,
                    "Task successfully added with id %d".formatted(taskFromRequest.getId()),
                    201);
        }
    }

}
