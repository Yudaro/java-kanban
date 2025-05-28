package http_task_server_test;

import entities.Subtask;
import handlers.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.Epic;
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

public class EpicHandlerTest {
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
    void createEpic() throws IOException {
        Epic epic = new Epic("API Epic", "testing creation");
        String json = gson.toJson(epic);

        URL url = new URL("http://localhost:8080/epics");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");
        con.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));

        assertEquals(201, con.getResponseCode());
    }

    @Test
    void getAllEpics() throws IOException {
        Epic epic = new Epic("Returned Epic", "desc");
        manager.createEpic(epic);

        URL url = new URL("http://localhost:8080/epics");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        String response;
        try (Scanner scanner = new Scanner(con.getInputStream(), StandardCharsets.UTF_8)) {
            response = scanner.useDelimiter("\\A").next();
        }

        assertEquals(200, con.getResponseCode());
        assertTrue(response.contains("Returned Epic"));
    }

    @Test
    void deleteEpic() throws IOException {
        Epic epic = new Epic("To Delete", "desc");
        manager.createEpic(epic);
        int id = epic.getId();

        URL url = new URL("http://localhost:8080/epics/" + id);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");
        assertEquals(200, con.getResponseCode());

        URL getUrl = new URL("http://localhost:8080/epics");
        HttpURLConnection getCon = (HttpURLConnection) getUrl.openConnection();
        getCon.setRequestMethod("GET");
        String response;
        try (Scanner scanner = new Scanner(getCon.getInputStream(), StandardCharsets.UTF_8)) {
            response = scanner.useDelimiter("\\A").next();
        }
        assertEquals("[]", response);
    }

    @Test
    void deleteAllEpicsAlsoDeletesAllSubtasks() throws IOException {
        Epic epic1 = new Epic("Epic 1", "desc");
        Epic epic2 = new Epic("Epic 2", "desc");
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask 1", "desc", epic1.getId(), 30,
                LocalDateTime.of(2025, 6, 2, 10, 0));
        Subtask subtask2 = new Subtask("Subtask 2", "desc", epic1.getId(), 45,
                LocalDateTime.of(2025, 6, 2, 11, 0));
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        HttpURLConnection delete = (HttpURLConnection) new URL("http://localhost:8080/epics").openConnection();
        delete.setRequestMethod("DELETE");
        assertEquals(200, delete.getResponseCode());

        HttpURLConnection getEpics = (HttpURLConnection) new URL("http://localhost:8080/epics").openConnection();
        getEpics.setRequestMethod("GET");
        String epicResponse;
        try (Scanner scanner = new Scanner(getEpics.getInputStream(), StandardCharsets.UTF_8)) {
            epicResponse = scanner.useDelimiter("\\A").next();
        }
        assertEquals("[]", epicResponse);

        HttpURLConnection getSubtasks = (HttpURLConnection) new URL("http://localhost:8080/subtasks").openConnection();
        getSubtasks.setRequestMethod("GET");
        String subtaskResponse;
        try (Scanner scanner = new Scanner(getSubtasks.getInputStream(), StandardCharsets.UTF_8)) {
            subtaskResponse = scanner.useDelimiter("\\A").next();
        }
        assertEquals("[]", subtaskResponse);
    }
}
