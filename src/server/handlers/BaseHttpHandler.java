package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.task.TaskManager;
import task.BaseTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {
    public static final Charset STANDARD_CHARSET = StandardCharsets.UTF_8;
    public static final Map.Entry<String, String> CONTENT_HEADER =
            Map.entry("Content-Type", "application/json;charset=utf-8");
    protected final TaskManager taskManager;
    protected final Gson gson;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    protected void sendResponse(HttpExchange exchange, String response, int statusCode) {
        byte[] responseInBytes = response.getBytes(STANDARD_CHARSET);
        exchange.getResponseHeaders().add(CONTENT_HEADER.getKey(), CONTENT_HEADER.getValue());
        try (OutputStream outputStream = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(statusCode, responseInBytes.length);
            outputStream.write(responseInBytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to send response", e);
        }
    }

    protected void sendResponseNotFound(HttpExchange exchange, String response) {
        int statusCode = 404;
        sendResponse(exchange, response, statusCode);
    }

    protected void sendResponseBadRequest(HttpExchange exchange) {
        int statusCode = 400;
        String response = "Unsupported method";
        sendResponse(exchange, response, statusCode);
    }

    protected void sendResponseHasOverlaps(HttpExchange exchange, String response) {
        int statusCode = 406;
        sendResponse(exchange, response, statusCode);
    }

    protected String getStringFromRequestBody(HttpExchange exchange) {
        try (InputStream inputStream = exchange.getRequestBody()) {
            return new String(inputStream.readAllBytes(), STANDARD_CHARSET);
        } catch (IOException e) {
            throw new RuntimeException("Error while reading request body", e);
        }
    }

    protected String[] splitURI(HttpExchange exchange) {
        return exchange.getRequestURI().getPath().split("/");
    }

    protected void handleDelete(HttpExchange exchange) {
        String[] pathParts = splitURI(exchange);
        if (pathParts.length != 3) {
            sendResponseNotFound(exchange, "Bad Request");
            return;
        }

        int taskId;
        try {
            taskId = Integer.parseInt(pathParts[2]);
        } catch (NumberFormatException e) {
            sendResponseNotFound(exchange, "Not a number");
            return;
        }

        try {
            taskManager.removeById(taskId);
        } catch (IllegalArgumentException e) {
            sendResponseNotFound(exchange, "No task with such id");
        }

        sendResponse(exchange, "task with id: %d successfully deleted".formatted(taskId), 200);
    }

    protected void handleGetTaskById(HttpExchange exchange, String[] pathParts) {
        int taskId;
        try {
            taskId = Integer.parseInt(pathParts[2]);
        } catch (NumberFormatException e) {
            sendResponseNotFound(exchange, "Not a number");
            return;
        }

        Optional<BaseTask> taskById = taskManager.getById(taskId);
        taskById.ifPresentOrElse(baseTask ->
                        sendResponse(exchange, gson.toJson(baseTask), 200),
                () -> sendResponseNotFound(exchange, "Not found"));
    }
}
