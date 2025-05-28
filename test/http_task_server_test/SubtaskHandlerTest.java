package http_task_server_test;

import handlers.LocalDateTimeAdapter;
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

    @Test
    void deleteAllSubtasks() throws IOException {
        Epic epic = new Epic("Эпик для подзадач", "описание");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "desc", epic.getId(), 30, LocalDateTime.of(2025, 6, 1, 12, 0));
        String json1 = gson.toJson(subtask1);
        HttpURLConnection post1 = (HttpURLConnection) new URL("http://localhost:8080/subtasks").openConnection();
        post1.setRequestMethod("POST");
        post1.setDoOutput(true);
        post1.setRequestProperty("Content-Type", "application/json");
        post1.getOutputStream().write(json1.getBytes(StandardCharsets.UTF_8));
        post1.getResponseCode();

        Subtask subtask2 = new Subtask("Подзадача 2", "desc", epic.getId(), 45, LocalDateTime.of(2025, 6, 1, 13, 0));
        String json2 = gson.toJson(subtask2);
        HttpURLConnection post2 = (HttpURLConnection) new URL("http://localhost:8080/subtasks").openConnection();
        post2.setRequestMethod("POST");
        post2.setDoOutput(true);
        post2.setRequestProperty("Content-Type", "application/json");
        post2.getOutputStream().write(json2.getBytes(StandardCharsets.UTF_8));
        post2.getResponseCode();

        HttpURLConnection delete = (HttpURLConnection) new URL("http://localhost:8080/subtasks").openConnection();
        delete.setRequestMethod("DELETE");
        assertEquals(200, delete.getResponseCode());

        HttpURLConnection getAll = (HttpURLConnection) new URL("http://localhost:8080/subtasks").openConnection();
        getAll.setRequestMethod("GET");
        String response;
        try (Scanner scanner = new Scanner(getAll.getInputStream(), StandardCharsets.UTF_8)) {
            response = scanner.useDelimiter("\\A").next();
        }

        assertEquals("[]", response);
    }
}
