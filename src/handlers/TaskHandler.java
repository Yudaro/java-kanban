package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entities.Task;
import exceptions.TaskTimeConflictException;
import managers.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager manager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();// Получаем метод (GET, POST, DELETE)
        URI uri = httpExchange.getRequestURI();// Получаем URI запроса
        String path = uri.getPath();// Извлекаем путь из URI

        switch (method) {
            case "GET":
                // 1. Обработка запроса на получение всех задач
                if (path.equals("/tasks")) {
                    List<Task> tasks = manager.getAllTasks();          // Получаем все задачи
                    String json = gson.toJson(tasks);               // Преобразуем в JSON
                    sendText(httpExchange, json);                   // Отправляем ответ
                    break;
                }

                // 2. Обработка запроса на получение одной задачи по ID
                if (path.startsWith("/tasks/")) {
                    String[] parts = path.split("/");               // Например: ["", "tasks", "2"]
                    if (parts.length == 3) {
                        try {
                            int id = Integer.parseInt(parts[2]);    // Извлекаем ID из пути
                            Optional<Task> optionalTask = manager.getTaskById(id); // Получаем Optional<Task>
                            if (optionalTask.isPresent()) {
                                Task task = optionalTask.get();     // Извлекаем задачу
                                sendText(httpExchange, gson.toJson(task)); // Отправляем JSON
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

                // Если путь не соответствует известным шаблонам — отправляем 404
                sendNotFound(httpExchange, "Неподдерживаемый путь: " + path);
                break;
            case "POST":
                try {
                    // 1. Чтение тела запроса
                    String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    System.out.println("POST /tasks BODY = " + body);

                    // 2. Преобразуем JSON → Task
                    Task task = gson.fromJson(body, Task.class);

                    // 3. Проверка на null
                    if (task == null) {
                        sendNotFound(httpExchange, "Невозможно разобрать тело запроса");
                        break;
                    }

                    // 4. Создание или обновление
                    try {
                        if (task.getId() == 0) {
                            manager.createTask(task);
                        } else {
                            manager.updateTask(task);
                        }

                        // 5. Успешно создана/обновлена
                        String json = gson.toJson(task);
                        httpExchange.sendResponseHeaders(201, json.getBytes().length);
                        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
                        httpExchange.getResponseBody().write(json.getBytes());
                    } catch (TaskTimeConflictException e) {
                        // 6. Пересечение по времени — вернуть 406
                        sendHasInteractions(httpExchange, "Ошибка: задача пересекается с другими");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    sendNotFound(httpExchange, "Ошибка при обработке POST запроса");
                }
                break;
            case "DELETE":
                if (path.startsWith("/tasks/")) {
                    String[] parts = path.split("/");
                    if (parts.length == 3) {
                        try {
                            int id = Integer.parseInt(parts[2]);
                            Optional<Task> task = manager.getTaskById(id);
                            if (task.isPresent()) {
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
                    break;
                }
                break;
            default:
                sendNotFound(httpExchange, "Метод " + method + " не поддерживается");
                break;
        }
    }
}
