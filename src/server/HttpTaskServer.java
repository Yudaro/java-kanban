package server;

import handlers.EpicHandler;
import handlers.SubtaskHandler;
import handlers.SystemHandler;
import handlers.TaskHandler;
import com.sun.net.httpserver.HttpServer;
import entities.Epic;
import entities.Subtask;
import entities.Task;
import managers.InMemoryTaskManager;
import managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private final int PORT = 8080;
    private final TaskManager manager;
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;

        // Создаём HTTP-сервер
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Регистрируем обработчики
        httpServer.createContext("/tasks", new TaskHandler(manager));
        httpServer.createContext("/epics", new EpicHandler(manager));
        httpServer.createContext("/subtasks", new SubtaskHandler(manager));
        httpServer.createContext("/history", new SystemHandler(manager));
        httpServer.createContext("/prioritized", new SystemHandler(manager));
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на порту " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
    }

    public static void main(String[] args) {
        try {
            TaskManager manager = new InMemoryTaskManager();// Создаём менеджер
            Task task = new Task("ТестТаск1", "теститруем апи", 10, LocalDateTime.of(2025, 05, 26, 20, 00));
            Epic epic = new Epic("ТестЕпик1", "теститруем апи");
            manager.createEpic(epic);
            Subtask subtask = new Subtask("ТестСабтаск1", "теститруем апи", epic.getId(), 10, LocalDateTime.of(2025, 05, 26, 20, 30));
            manager.createTask(task);
            manager.createSubtask(subtask);
            HttpTaskServer server = new HttpTaskServer(manager); // Создаём сервер с этим менеджером
            server.start();                                      // Запускаем сервер
        } catch (IOException e) {
            System.out.println("Ошибка при запуске сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
