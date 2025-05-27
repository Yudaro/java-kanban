package Handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
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

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager manager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public SubtaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        URI uri = httpExchange.getRequestURI();
        String path = uri.getPath();

        switch (method) {
            case "GET":
                if (path.equals("/subtasks")) {
                    List<Subtask> subtasks = manager.getAllSubtasks();
                    String json = gson.toJson(subtasks);
                    sendText(httpExchange, json);
                    break;
                }

                if (path.startsWith("/subtasks/")) {
                    String[] parts = path.split("/");
                    if (parts.length == 3) {
                        try {
                            int id = Integer.parseInt(parts[2]);
                            Optional<Subtask> optionalSubtask = manager.getSubtaskById(id);
                            if (optionalSubtask.isPresent()) {
                                Subtask subtask = optionalSubtask.get();
                                sendText(httpExchange, gson.toJson(subtask));
                            } else {
                                sendNotFound(httpExchange, "Задача с id=" + id + " не найдена");
                            }
                        } catch (NumberFormatException e) {
                            sendNotFound(httpExchange, "Неверный формат ID");
                        }
                    } else {
                        sendNotFound(httpExchange, "Некорректный путь запроса");
                    }
                    break;
                }
                sendNotFound(httpExchange, "Неподдерживаемый путь: " + path);
                break;
            case "POST":
                try {
                    String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Subtask subtask = gson.fromJson(body, Subtask.class);

                    if (subtask == null) {
                        sendNotFound(httpExchange, "Некорректное тело запроса.");
                        break;
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

                } catch (Exception e) {
                    e.printStackTrace();
                    sendNotFound(httpExchange, "Ошибка обработки подзадачи");
                }
                break;
            case "DELETE":
                if (path.startsWith("/subtasks/")) {
                    String[] parts = path.split("/");
                    if (parts.length == 3) {
                        try {
                            int id = Integer.parseInt(parts[2]);
                            Optional<Subtask> subtask = manager.getSubtaskById(id);
                            if (subtask.isPresent()) {
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
                    break;
                }
                break;
            default:
                sendNotFound(httpExchange, "Метод " + method + " не поддерживается");
                break;
        }
    }

}
