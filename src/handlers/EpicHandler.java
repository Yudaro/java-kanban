package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entities.Epic;
import entities.Subtask;
import managers.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager manager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public EpicHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        URI uri = httpExchange.getRequestURI();
        String path = uri.getPath();

        switch (method) {
            case "GET":
                // GET /epics — получить все эпики
                if (path.equals("/epics")) {
                    List<Epic> epics = manager.getAllEpics();
                    String json = gson.toJson(epics);
                    sendText(httpExchange, json);
                    break;
                }

                // GET /epics/{id} или /epics/{id}/subtasks
                if (path.startsWith("/epics/")) {
                    String[] parts = path.split("/");

                    // GET /epics/{id}/subtasks
                    if (parts.length == 4 && parts[3].equals("subtasks")) {
                        try {
                            int id = Integer.parseInt(parts[2]);
                            Optional<Epic> optionalEpic = manager.getEpicById(id);

                            if (optionalEpic.isPresent()) {
                                Epic epic = optionalEpic.get();
                                List<Subtask> subtasks = epic.getSubtasks(); // ✅
                                String json = gson.toJson(subtasks);
                                sendText(httpExchange, json);
                            } else {
                                sendNotFound(httpExchange, "Эпик с id=" + id + " не найден");
                            }
                        } catch (NumberFormatException e) {
                            sendNotFound(httpExchange, "Неверный формат ID");
                        }
                        break;
                    }

                    // GET /epics/{id}
                    if (parts.length == 3) {
                        try {
                            int id = Integer.parseInt(parts[2]);
                            Optional<Epic> optionalEpic = manager.getEpicById(id);
                            if (optionalEpic.isPresent()) {
                                Epic epic = optionalEpic.get();
                                sendText(httpExchange, gson.toJson(epic));
                            } else {
                                sendNotFound(httpExchange, "Задача с id=" + id + " не найдена");
                            }
                        } catch (NumberFormatException e) {
                            sendNotFound(httpExchange, "Неверный формат ID");
                        }
                        break;
                    }

                    sendNotFound(httpExchange, "Некорректный путь запроса");
                    break;
                }

                sendNotFound(httpExchange, "Неподдерживаемый путь: " + path);
                break;
            case "POST":
                try {
                    String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Epic epic = gson.fromJson(body, Epic.class);

                    if (epic == null) {
                        sendNotFound(httpExchange, "Некорректное тело запроса.");
                        break;
                    }

                    manager.createEpic(epic);

                    String json = gson.toJson(epic);
                    httpExchange.sendResponseHeaders(201, json.getBytes().length);
                    httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
                    httpExchange.getResponseBody().write(json.getBytes());

                } catch (Exception e) {
                    e.printStackTrace();
                    sendNotFound(httpExchange, "Ошибка при создании эпика");
                }
                break;
            case "DELETE":
                if (path.startsWith("/epics/")) {
                    String[] parts = path.split("/");
                    if (parts.length == 3) {
                        try {
                            int id = Integer.parseInt(parts[2]);
                            Optional<Epic> epic = manager.getEpicById(id);
                            if (epic.isPresent()) {
                                manager.deleteEpicById(id);
                                sendText(httpExchange, "Эпик удалён");
                            } else {
                                sendNotFound(httpExchange, "Эпик с id=" + id + " не найден");
                            }
                        } catch (NumberFormatException e) {
                            sendNotFound(httpExchange, "Неверный формат ID");
                        }
                    } else {
                        sendNotFound(httpExchange, "Некорректный путь запроса");
                    }
                    break;
                }

                // Чтобы GET /epics не сломался:
                if (path.equals("/epics")) {
                    sendNotFound(httpExchange, "Удаление всех эпиков не поддерживается");
                    break;
                }

                sendNotFound(httpExchange, "Неподдерживаемый путь: " + path);
                break;
            default:
                sendNotFound(httpExchange, "Метод " + method + " не поддерживается");
                break;
        }
    }
}
