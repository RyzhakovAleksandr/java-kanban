package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import managers.task.TaskManager;
import task.EpicTask;
import task.SubTask;

import java.util.List;

public class EpicHttpHandler extends BaseHttpHandler {

    public EpicHttpHandler(TaskManager taskManager, Gson gson) {
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
                List<EpicTask> allEpics = taskManager.getAllEpicsTasks();
                String allEpicsJson = gson.toJson(allEpics);
                sendResponse(exchange, allEpicsJson, 200);
            }
            case 3 -> handleGetTaskById(exchange, pathParts);
            case 4 -> {
                int taskId;
                try {
                    taskId = Integer.parseInt(pathParts[2]);
                } catch (NumberFormatException e) {
                    sendResponseNotFound(exchange, "Not a number");
                    return;
                }
                try {
                    List<SubTask> allSubtasksOfEpic = taskManager.getAllSubtasksOfEpic(taskId);
                    String allSubtasksJson = gson.toJson(allSubtasksOfEpic);
                    sendResponse(exchange, allSubtasksJson, 200);
                } catch (IllegalArgumentException e) {
                    sendResponseNotFound(exchange, "Not an epic");
                }
            }
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
        EpicTask taskFromRequest;
        try {
            taskFromRequest = gson.fromJson(body, EpicTask.class);
            System.out.println(taskFromRequest.toString());
        } catch (JsonSyntaxException e) {
            sendResponseNotFound(exchange, "No request body provided");
            return;
        }

        EpicTask epic = new EpicTask(taskFromRequest.getTitle(), taskFromRequest.getDescription(), null);
        taskManager.addTask(epic);

        sendResponse(exchange, "ok", 201);
    }
}
