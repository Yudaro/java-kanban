package http_task_server_test;

import handlers.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.Task;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.*;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskHandlerTest {

    private HttpTaskServer server;
    private TaskManager manager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @BeforeEach
    void startServer() throws IOException {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        server.start();
    }

    @AfterEach
    void stopServer() {
        server.stop();
    }

    @Test
    void postAndGetTask() throws IOException {
        Task task = new Task("Задача через тест", "описание", 30, LocalDateTime.of(2025, 5, 30, 12, 0));
        String taskJson = gson.toJson(task);

        HttpURLConnection post = (HttpURLConnection) new URL("http://localhost:8080/tasks").openConnection();
        post.setRequestMethod("POST");
        post.setDoOutput(true);
        post.setRequestProperty("Content-Type", "application/json");
        post.getOutputStream().write(taskJson.getBytes(StandardCharsets.UTF_8));
        assertEquals(201, post.getResponseCode());

        HttpURLConnection get = (HttpURLConnection) new URL("http://localhost:8080/tasks").openConnection();
        get.setRequestMethod("GET");

        String response;
        try (Scanner scanner = new Scanner(get.getInputStream(), StandardCharsets.UTF_8)) {
            response = scanner.useDelimiter("\\A").next();
        }

        assertEquals(200, get.getResponseCode());
        assertTrue(response.contains("Задача через тест"));
    }

    @Test
    void getNotExistingTaskByIdReturns404() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/tasks/999").openConnection();
        connection.setRequestMethod("GET");
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void deleteTask() throws IOException {
        Task task = new Task("Удаляемая задача", "тест", 15, LocalDateTime.of(2025, 6, 1, 10, 0));
        String json = gson.toJson(task);

        HttpURLConnection post = (HttpURLConnection) new URL("http://localhost:8080/tasks").openConnection();
        post.setRequestMethod("POST");
        post.setDoOutput(true);
        post.setRequestProperty("Content-Type", "application/json");
        post.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
        post.getResponseCode();

        HttpURLConnection getAll = (HttpURLConnection) new URL("http://localhost:8080/tasks").openConnection();
        getAll.setRequestMethod("GET");

        int id;
        try (Scanner scanner = new Scanner(getAll.getInputStream(), StandardCharsets.UTF_8)) {
            String response = scanner.useDelimiter("\\A").next();
            id = gson.fromJson(response, Task[].class)[0].getId();
        }

        HttpURLConnection delete = (HttpURLConnection) new URL("http://localhost:8080/tasks/" + id).openConnection();
        delete.setRequestMethod("DELETE");
        assertEquals(200, delete.getResponseCode());

        HttpURLConnection check = (HttpURLConnection) new URL("http://localhost:8080/tasks").openConnection();
        check.setRequestMethod("GET");
        try (Scanner scanner = new Scanner(check.getInputStream(), StandardCharsets.UTF_8)) {
            String response = scanner.useDelimiter("\\A").next();
            assertEquals("[]", response);
        }
    }
}
