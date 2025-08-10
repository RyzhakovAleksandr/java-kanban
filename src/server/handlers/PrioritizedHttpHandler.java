package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.task.TaskManager;
import task.BaseTask;

import java.util.List;

public class PrioritizedHttpHandler extends BaseHttpHandler {
    public PrioritizedHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) {
        if (exchange.getRequestMethod().equals("GET")) {
            handleGet(exchange);
        } else {
            sendResponseBadRequest(exchange);
        }
    }

    private void handleGet(HttpExchange exchange) {
        List<BaseTask> prioritizedTasks = taskManager.getPrioritizedTasks();
        String prioritizedTasksJson = gson.toJson(prioritizedTasks);
        sendResponse(exchange, prioritizedTasksJson, 200);
    }
}
