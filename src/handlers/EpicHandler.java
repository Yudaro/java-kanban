package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import entities.Epic;
import entities.Subtask;
import managers.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public EpicHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    protected void handleRequest(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        URI uri = httpExchange.getRequestURI();
        String path = uri.getPath();

        switch (method) {
            case "GET":
                if (path.equals("/epics")) {
                    List<Epic> epics = manager.getAllEpics();
                    sendText(httpExchange, gson.toJson(epics));
                    return;
                }

                if (path.startsWith("/epics/")) {
                    String[] parts = path.split("/");

                    // GET /epics/{id}/subtasks
                    if (parts.length == 4 && parts[3].equals("subtasks")) {
                        try {
                            int id = Integer.parseInt(parts[2]);
                            Optional<Epic> optionalEpic = manager.getEpicById(id);
                            if (optionalEpic.isPresent()) {
                                Epic epic = optionalEpic.get();
                                List<Subtask> subtasks = epic.getSubtasks();
                                sendText(httpExchange, gson.toJson(subtasks));
                            } else {
                                sendNotFound(httpExchange, "Эпик с id=" + id + " не найден");
                            }
                        } catch (NumberFormatException e) {
                            sendNotFound(httpExchange, "Неверный формат ID");
                        }
                        return;
                    }

                    // GET /epics/{id}
                    if (parts.length == 3) {
                        try {
                            int id = Integer.parseInt(parts[2]);
                            Optional<Epic> optionalEpic = manager.getEpicById(id);
                            if (optionalEpic.isPresent()) {
                                sendText(httpExchange, gson.toJson(optionalEpic.get()));
                            } else {
                                sendNotFound(httpExchange, "Эпик с id=" + id + " не найден");
                            }
                        } catch (NumberFormatException e) {
                            sendNotFound(httpExchange, "Неверный формат ID");
                        }
                        return;
                    }

                    sendNotFound(httpExchange, "Некорректный путь запроса");
                    return;
                }

                sendNotFound(httpExchange, "Неподдерживаемый путь: " + path);
                return;

            case "POST":
                String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Epic epic = gson.fromJson(body, Epic.class);

                if (epic == null) {
                    sendNotFound(httpExchange, "Некорректное тело запроса.");
                    return;
                }

                manager.createEpic(epic);
                String json = gson.toJson(epic);
                httpExchange.sendResponseHeaders(201, json.getBytes().length);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
                httpExchange.getResponseBody().write(json.getBytes());
                return;

            case "DELETE":
                if (path.equals("/epics")) {
                    manager.deleteAllEpics();
                    sendText(httpExchange, "Все эпики удалены");
                    return;
                }

                if (path.startsWith("/epics/")) {
                    String[] parts = path.split("/");
                    if (parts.length == 3) {
                        try {
                            int id = Integer.parseInt(parts[2]);
                            Optional<Epic> epicToDelete = manager.getEpicById(id);
                            if (epicToDelete.isPresent()) {
                                manager.deleteEpicById(id);
                                sendText(httpExchange, "Эпик удалён");
                            } else {
                                sendNotFound(httpExchange, "Эпик с id=" + id + " не найден");
                            }
                        } catch (NumberFormatException e) {
                            sendNotFound(httpExchange, "Неверный формат ID");
                        }
                        return;
                    }

                    sendNotFound(httpExchange, "Некорректный путь запроса");
                    return;
                }

                sendNotFound(httpExchange, "Неподдерживаемый путь: " + path);
                return;

            default:
                sendNotFound(httpExchange, "Метод " + method + " не поддерживается");
        }
    }
}