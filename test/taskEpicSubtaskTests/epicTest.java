package taskEpicSubtaskTests;

import entities.Epic;
import entities.Subtask;
import enums.TaskStatus;
import managers.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class epicTest {

    @Test
    public void checkTheStatusEpicIfAllSubtasksHaveTheStatusNew() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Епик", "проверяем статус epic");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Сабтаск1", "Проверяем статус", epic, 10, LocalDateTime.now());
        Subtask subtask2 = new Subtask("Сабтаск2", "Проверяем статус", epic, 10, LocalDateTime.now().plusSeconds(1000));
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        Assertions.assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void checkTheStatusEpicIfAllSubtasksHaveTheStatusDone() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Епик", "проверяем статус epic");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Сабтаск1", "Проверяем статус", epic, 10, LocalDateTime.now());
        Subtask subtask2 = new Subtask("Сабтаск2", "Проверяем статус", epic, 10, LocalDateTime.now().plusSeconds(1000));
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.updateStatusSubtask(subtask1, TaskStatus.DONE);
        manager.updateStatusSubtask(subtask2, TaskStatus.DONE);
        Assertions.assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void checkTheStatusEpicIfAllSubtasksHaveTheStatusInProgress() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Епик", "проверяем статус epic");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Сабтаск1", "Проверяем статус", epic, 10, LocalDateTime.now());
        Subtask subtask2 = new Subtask("Сабтаск2", "Проверяем статус", epic, 10, LocalDateTime.now().plusSeconds(1000));
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.updateStatusSubtask(subtask1, TaskStatus.IN_PROGRESS);
        manager.updateStatusSubtask(subtask2, TaskStatus.IN_PROGRESS);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void checkTheStatusEpicIfAllSubtasksHaveTheStatusNewAndInProgress() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Епик", "проверяем статус epic");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Сабтаск1", "Проверяем статус", epic, 10, LocalDateTime.now());
        Subtask subtask2 = new Subtask("Сабтаск2", "Проверяем статус", epic, 10, LocalDateTime.now().plusSeconds(1000));
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.updateStatusSubtask(subtask1, TaskStatus.NEW);
        manager.updateStatusSubtask(subtask2, TaskStatus.IN_PROGRESS);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }
}