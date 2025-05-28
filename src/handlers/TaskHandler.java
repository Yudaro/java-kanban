package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import entities.Task;
import exceptions.TaskTimeConflictException;
import managers.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    protected void handleRequest(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        URI uri = httpExchange.getRequestURI();
        String path = uri.getPath();

        switch (method) {
            case "GET":
                if (path.equals("/tasks")) {
                    List<Task> tasks = manager.getAllTasks();
                    sendText(httpExchange, gson.toJson(tasks));
                    return;
                }

                if (path.startsWith("/tasks/")) {
                    String[] parts = path.split("/");
                    if (parts.length == 3) {
                        try {
                            int id = Integer.parseInt(parts[2]);
                            Optional<Task> task = manager.getTaskById(id);
                            if (task.isPresent()) {
                                sendText(httpExchange, gson.toJson(task.get()));
                            } else {
                                sendNotFound(httpExchange, "Задача с id=" + id + " не найдена");
                            }
                        } catch (NumberFormatException e) {
                            sendNotFound(httpExchange, "Неверный формат ID");
                        }
                    } else {
                        sendNotFound(httpExchange, "Некорректный путь запроса");
                    }
                    return;
                }

                sendNotFound(httpExchange, "Неподдерживаемый путь: " + path);
                return;

            case "POST":
                String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(body, Task.class);

                if (task == null) {
                    sendNotFound(httpExchange, "Невозможно разобрать тело запроса");
                    return;
                }

                try {
                    if (task.getId() == 0) {
                        manager.createTask(task);
                    } else {
                        manager.updateTask(task);
                    }

                    String json = gson.toJson(task);
                    httpExchange.sendResponseHeaders(201, json.getBytes().length);
                    httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
                    httpExchange.getResponseBody().write(json.getBytes());
                } catch (TaskTimeConflictException e) {
                    sendHasInteractions(httpExchange, "Ошибка: задача пересекается с другими");
                }
                return;

            case "DELETE":
                if (path.equals("/tasks")) {
                    manager.deleteAllTasks();
                    sendText(httpExchange, "Все задачи удалены");
                    return;
                }

                if (path.startsWith("/tasks/")) {
                    String[] parts = path.split("/");
                    if (parts.length == 3) {
                        try {
                            int id = Integer.parseInt(parts[2]);
                            Optional<Task> taskToDelete = manager.getTaskById(id);
                            if (taskToDelete.isPresent()) {
                                manager.deleteTaskById(id);
                                sendText(httpExchange, "Задача удалена");
                            } else {
                                sendNotFound(httpExchange, "Задача с id=" + id + " не найдена");
                            }
                        } catch (NumberFormatException e) {
                            sendNotFound(httpExchange, "Неверный формат ID");
                        }
                    } else {
                        sendNotFound(httpExchange, "Некорректный путь запроса");
                    }
                    return;
                }

                sendNotFound(httpExchange, "Неподдерживаемый путь: " + path);
                return;

            default:
                sendNotFound(httpExchange, "Метод " + method + " не поддерживается");
        }
    }
}