package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entities.Task;
import managers.HistoryManager;
import managers.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class SystemHandler extends BaseHttpHandler implements HttpHandler {
    private final HistoryManager historyManager;
    private final TaskManager manager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public SystemHandler(TaskManager manager) {
        this.manager = manager;
        historyManager = manager.getHistoryManager();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        URI uri = httpExchange.getRequestURI();
        String path = uri.getPath();
        System.out.println("SystemHandler вызван: " + path);

        if (!httpExchange.getRequestMethod().equals("GET")) {
            sendNotFound(httpExchange, "Метод " + httpExchange.getRequestMethod() + " не поддерживается");
            return;
        }

        if (path.equals("/history")) {
            List<Task> history = historyManager.getHistory();
            if (!history.isEmpty()) {
                String json = gson.toJson(history);
                sendText(httpExchange, json);
            } else {
                String message = "Список истории пуст.";
                sendText(httpExchange, gson.toJson(message));
            }
        } else if (path.equals("/prioritized")) {
            Set<Task> prioritizedTask = manager.getPrioritizedTasks();

            if (!prioritizedTask.isEmpty()) {
                String json = gson.toJson(prioritizedTask);
                sendText(httpExchange, json);
            } else {
                String message = "Список приоритетных задач пуст.";
                sendText(httpExchange, gson.toJson(message));
            }
        } else {
            sendNotFound(httpExchange, "Неподдерживаемый путь: " + path);
        }
    }
}
