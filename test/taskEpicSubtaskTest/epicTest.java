package taskEpicSubtaskTest;

import entities.Epic;
import entities.Subtask;
import enums.TaskStatus;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

public class epicTest {

    @Test
    public void checkThatTheStatusEpic() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Епик", "проверяем статус epic");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Сабтаск1", "Проверяем статус", epic, 10, Instant.now());
        Subtask subtask2 = new Subtask("Сабтаск2", "Проверяем статус", epic, 10, Instant.now().plusSeconds(1000));
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        Assertions.assertEquals(epic.getStatus(), TaskStatus.NEW);


        manager.updateStatusSubtask(subtask1, TaskStatus.DONE);
        manager.updateStatusSubtask(subtask2, TaskStatus.DONE);
        Assertions.assertEquals(epic.getStatus(), TaskStatus.DONE);

        manager.updateStatusSubtask(subtask1, TaskStatus.NEW);
        Assertions.assertEquals(epic.getStatus(), TaskStatus.IN_PROGRESS);

        manager.updateStatusSubtask(subtask1, TaskStatus.IN_PROGRESS);
        manager.updateStatusSubtask(subtask2, TaskStatus.IN_PROGRESS);
        Assertions.assertEquals(epic.getStatus(), TaskStatus.IN_PROGRESS);
    }
}