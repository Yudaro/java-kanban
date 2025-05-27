package task_manager_tests;


import entities.Epic;
import entities.Subtask;
import entities.Task;
import managers.FileBackedTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @BeforeEach
    void init() throws IOException {
        File tempFile = File.createTempFile("test-", ".csv");
        tempFile.deleteOnExit();
        manager = new FileBackedTaskManager(tempFile.getAbsolutePath());
    }

    @Test
    public void checkSaveTaskInFile() {
        Task task = new Task("Задача1", "Убрать мусор");

        manager.createTask(task);

        Assertions.assertEquals(1, manager.getAllTasks().size());
    }

    @Test
    public void checkSaveEpicInFile() {
        Epic epic = new Epic("Епик1", "Вынести мусор");

        manager.createEpic(epic);

        Assertions.assertEquals(1, manager.getAllEpics().size());
    }

    @Test
    public void chekSaveSubtaskInFile() {
        Epic epic = new Epic("Ремонт", "Доделать ремонт");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("ПодзадачаРемонт", "Починить кран", epic.getId(), 10, LocalDateTime.now());

        manager.createSubtask(subtask);

        Assertions.assertEquals(1, manager.getAllEpics().size());
        Assertions.assertEquals(1, manager.getAllSubtasks().size());
    }
}