package http_task_server_test;

import handlers.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.Task;
import managers.InMemoryTaskManager;
import org.junit.jupiter.api.*;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class SystemHandlerTest {

    private HttpTaskServer server;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @BeforeEach
    void startServer() throws IOException {
        server = new HttpTaskServer(new InMemoryTaskManager());
        server.start();
    }

    @AfterEach
    void stopServer() {
        server.stop();
    }

    @Test
    void checkingThatEmptyHistoryReturnsEmptyList() throws IOException {
        URL url = new URL("http://localhost:8080/history");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        String response;
        try (Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8)) {
            response = scanner.useDelimiter("\\A").next();
        }

        assertEquals(200, connection.getResponseCode());
        assertTrue(response.equals("[]") || response.contains("пуст"));
    }

    @Test
    void checkingThatOnlyViewedTasksAppearInHistory() throws IOException {
        Task task1 = new Task("Первая", "desc", 15, LocalDateTime.of(2025, 6, 1, 9, 0));
        Task task2 = new Task("Вторая", "desc", 15, LocalDateTime.of(2025, 6, 1, 10, 0));

        String json1 = gson.toJson(task1);
        String json2 = gson.toJson(task2);

        for (String json : new String[]{json1, json2}) {
            HttpURLConnection post = (HttpURLConnection) new URL("http://localhost:8080/tasks").openConnection();
            post.setRequestMethod("POST");
            post.setDoOutput(true);
            post.setRequestProperty("Content-Type", "application/json");
            post.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
            post.getResponseCode();
        }

        HttpURLConnection getAll = (HttpURLConnection) new URL("http://localhost:8080/tasks").openConnection();
        getAll.setRequestMethod("GET");

        int id;
        try (Scanner scanner = new Scanner(getAll.getInputStream(), StandardCharsets.UTF_8)) {
            String response = scanner.useDelimiter("\\A").next();
            id = gson.fromJson(response, Task[].class)[1].getId();
        }

        HttpURLConnection getById = (HttpURLConnection) new URL("http://localhost:8080/tasks/" + id).openConnection();
        getById.setRequestMethod("GET");
        getById.getResponseCode();

        HttpURLConnection history = (HttpURLConnection) new URL("http://localhost:8080/history").openConnection();
        history.setRequestMethod("GET");

        String response;
        try (Scanner scanner = new Scanner(history.getInputStream(), StandardCharsets.UTF_8)) {
            response = scanner.useDelimiter("\\A").next();
        }

        assertEquals(200, history.getResponseCode());
        assertTrue(response.contains("Вторая"));
        assertFalse(response.contains("Первая"));
    }

    @Test
    void checkingThatPrioritizedTasksReturnsTasksInCorrectOrder() throws IOException {
        Task early = new Task("Ранняя", "desc", 15, LocalDateTime.of(2025, 6, 1, 9, 0));
        Task late = new Task("Поздняя", "desc", 15, LocalDateTime.of(2025, 6, 1, 10, 0));

        for (Task task : new Task[]{late, early}) {
            HttpURLConnection post = (HttpURLConnection) new URL("http://localhost:8080/tasks").openConnection();
            post.setRequestMethod("POST");
            post.setDoOutput(true);
            post.setRequestProperty("Content-Type", "application/json");
            String json = gson.toJson(task);
            post.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
            post.getResponseCode();
        }

        HttpURLConnection con = (HttpURLConnection) new URL("http://localhost:8080/prioritized").openConnection();
        con.setRequestMethod("GET");

        String response;
        try (Scanner scanner = new Scanner(con.getInputStream(), StandardCharsets.UTF_8)) {
            response = scanner.useDelimiter("\\A").next();
        }

        assertEquals(200, con.getResponseCode());
        int earlyIndex = response.indexOf("Ранняя");
        int lateIndex = response.indexOf("Поздняя");
        assertTrue(earlyIndex >= 0 && lateIndex >= 0);
        assertTrue(earlyIndex < lateIndex);
    }

    @Test
    void checkingThatPrioritizedTasksReturnsEmptyArrayIfNoTasks() throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL("http://localhost:8080/prioritized").openConnection();
        con.setRequestMethod("GET");

        String response;
        try (Scanner scanner = new Scanner(con.getInputStream(), StandardCharsets.UTF_8)) {
            response = scanner.useDelimiter("\\A").next();
        }

        assertEquals(200, con.getResponseCode());
        assertTrue(response.equals("[]") || response.contains("пуст"));
    }
}