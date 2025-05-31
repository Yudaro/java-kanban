package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import entities.Subtask;
import exceptions.TaskTimeConflictException;
import managers.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public SubtaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    protected void handleRequest(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        URI uri = httpExchange.getRequestURI();
        String path = uri.getPath();

        switch (method) {
            case "GET":
                if (path.equals("/subtasks")) {
                    List<Subtask> subtasks = manager.getAllSubtasks();
                    sendText(httpExchange, gson.toJson(subtasks));
                    return;
                }

                if (path.startsWith("/subtasks/")) {
                    String[] parts = path.split("/");
                    if (parts.length == 3) {
                        try {
                            int id = Integer.parseInt(parts[2]);
                            Optional<Subtask> optionalSubtask = manager.getSubtaskById(id);
                            if (optionalSubtask.isPresent()) {
                                sendText(httpExchange, gson.toJson(optionalSubtask.get()));
                            } else {
                                sendNotFound(httpExchange, "Подзадача с id=" + id + " не найдена");
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
                Subtask subtask = gson.fromJson(body, Subtask.class);

                if (subtask == null) {
                    sendNotFound(httpExchange, "Некорректное тело запроса.");
                    return;
                }

                try {
                    if (subtask.getId() == 0) {
                        manager.createSubtask(subtask);
                    } else {
                        manager.updateSubtask(subtask);
                    }

                    String json = gson.toJson(subtask);
                    httpExchange.sendResponseHeaders(201, json.getBytes().length);
                    httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
                    httpExchange.getResponseBody().write(json.getBytes());
                } catch (TaskTimeConflictException e) {
                    sendHasInteractions(httpExchange, "Пересечение по времени с существующей задачей");
                } catch (NoSuchElementException | NullPointerException e) {
                    sendNotFound(httpExchange, "Указанный epic не существует");
                }
                return;

            case "DELETE":
                if (path.equals("/subtasks")) {
                    manager.deleteAllSubtask();
                    sendText(httpExchange, "Все подзадачи удалены");
                    return;
                }

                if (path.startsWith("/subtasks/")) {
                    String[] parts = path.split("/");
                    if (parts.length == 3) {
                        try {
                            int id = Integer.parseInt(parts[2]);
                            Optional<Subtask> subtaskToDelete = manager.getSubtaskById(id);
                            if (subtaskToDelete.isPresent()) {
                                manager.deleteSubtaskById(id);
                                sendText(httpExchange, "Подзадача удалена");
                            } else {
                                sendNotFound(httpExchange, "Подзадача с id=" + id + " не найдена");
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