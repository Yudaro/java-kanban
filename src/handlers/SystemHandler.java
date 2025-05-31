package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import entities.Task;
import managers.HistoryManager;
import managers.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class SystemHandler extends BaseHttpHandler {
    private final HistoryManager historyManager;
    private final TaskManager manager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public SystemHandler(TaskManager manager) {
        this.manager = manager;
        this.historyManager = manager.getHistoryManager();
    }

    @Override
    protected void handleRequest(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        URI uri = httpExchange.getRequestURI();
        String path = uri.getPath();

        if (!method.equals("GET")) {
            sendNotFound(httpExchange, "Метод " + method + " не поддерживается");
            return;
        }

        switch (path) {
            case "/history":
                List<Task> history = historyManager.getHistory();
                sendText(httpExchange, gson.toJson(history)); // всегда JSON-массив
                return;

            case "/prioritized":
                Set<Task> prioritized = manager.getPrioritizedTasks();
                sendText(httpExchange, gson.toJson(prioritized)); // всегда JSON-массив
                return;

            default:
                sendNotFound(httpExchange, "Неподдерживаемый путь: " + path);
        }
    }
}