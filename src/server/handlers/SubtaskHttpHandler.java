package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import managers.exceptions.TaskIntersectException;
import managers.task.TaskManager;
import task.SubTask;

import java.util.List;

public class SubtaskHttpHandler extends BaseHttpHandler {
    public SubtaskHttpHandler(TaskManager taskManager, Gson gson) {
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

    private void handlePost(HttpExchange exchange) {
        String[] pathParts = splitURI(exchange);
        if (pathParts.length != 2) {
            sendResponseNotFound(exchange, "Bad Request");
            return;
        }
        String body = getStringFromRequestBody(exchange);
        SubTask taskFromRequest;
        try {
            taskFromRequest = gson.fromJson(body, SubTask.class);
        } catch (JsonSyntaxException e) {
            sendResponseNotFound(exchange, "No request body provided");
            return;
        }

        if (taskFromRequest.getId() != null) {
            try {
                taskManager.updateTask(taskFromRequest);
            } catch (TaskIntersectException | IllegalArgumentException e) {
                sendResponseHasOverlaps(exchange, e.getMessage());
            }
            sendResponse(exchange,
                    "SubTask with id %d successfully updated".formatted(taskFromRequest.getId()),
                    200);
        } else {
            try {
                taskManager.addTask(taskFromRequest);
            } catch (TaskIntersectException | IllegalArgumentException e) {
                sendResponseHasOverlaps(exchange, e.getMessage());
                return;
            }
            sendResponse(exchange,
                    "SubTask successfully added with id %d".formatted(taskFromRequest.getId()),
                    201);
        }
    }

    private void handleGet(HttpExchange exchange) {
        String[] pathParts = splitURI(exchange);
        switch (pathParts.length) {
            case 2 -> {
                List<SubTask> allSubTasks = taskManager.getAllSubTasks();
                String allSubTasksJson = gson.toJson(allSubTasks);
                sendResponse(exchange, allSubTasksJson, 200);
            }
            case 3 -> handleGetTaskById(exchange, pathParts);
            default -> sendResponseNotFound(exchange, "Bad Request");
        }
    }
}
