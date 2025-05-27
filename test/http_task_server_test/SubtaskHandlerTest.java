package http_task_server_test;

import Handlers.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.Epic;
import entities.Subtask;
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

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskHandlerTest {
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
    void createSubtask() throws IOException {
        Epic epic = new Epic("Epic for Subtask", "desc");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask test", "desc", epic.getId(), 30,
                LocalDateTime.of(2025, 6, 1, 12, 0));
        String json = gson.toJson(subtask);

        URL url = new URL("http://localhost:8080/subtasks");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");
        con.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));

        assertEquals(201, con.getResponseCode());
    }

    @Test
    void getAllSubtasks() throws IOException {
        Epic epic = new Epic("Epic", "desc");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "desc", epic.getId(), 30,
                LocalDateTime.of(2025, 6, 1, 12, 0));
        manager.createSubtask(subtask);

        URL url = new URL("http://localhost:8080/subtasks");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        String response;
        try (Scanner scanner = new Scanner(con.getInputStream(), StandardCharsets.UTF_8)) {
            response = scanner.useDelimiter("\\A").next();
        }

        assertEquals(200, con.getResponseCode());
        assertTrue(response.contains("Subtask"));
    }

    @Test
    void deleteSubtask() throws IOException {
        Epic epic = new Epic("Epic", "desc");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("To delete", "desc", epic.getId(), 30,
                LocalDateTime.of(2025, 6, 1, 12, 0));
        manager.createSubtask(subtask);
        int id = subtask.getId();

        URL url = new URL("http://localhost:8080/subtasks/" + id);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");

        assertEquals(200, con.getResponseCode());

        URL getAll = new URL("http://localhost:8080/subtasks");
        HttpURLConnection getCon = (HttpURLConnection) getAll.openConnection();
        getCon.setRequestMethod("GET");
        String response;
        try (Scanner scanner = new Scanner(getCon.getInputStream(), StandardCharsets.UTF_8)) {
            response = scanner.useDelimiter("\\A").next();
        }
        assertEquals("[]", response);
    }
}
