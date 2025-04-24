package task_manager_test;


import entities.Epic;
import entities.Subtask;
import entities.Task;
import manager.FileBackedTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileBackedTaskManagerTest {

    @Test
    public void checkSaveTaskInFile() throws IOException {
        File tempFile = File.createTempFile("test-", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile.getAbsolutePath());
        Task task = new Task("Задача1", "Убрать мусор");

        manager.createTask(task);

        Assertions.assertEquals(1, manager.getAllTasks().size());
    }

    @Test
    public void checkSaveEpicInFile() throws IOException {
        File tempFile = File.createTempFile("test-", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile.getAbsolutePath());
        Epic epic = new Epic("Епик1", "Вынести мусор");

        manager.createEpic(epic);

        Assertions.assertEquals(1, manager.getAllEpics().size());
        System.out.println(tempFile.getAbsolutePath());
    }

    @Test
    public void chekSaveSubtaskInFile() throws IOException {
        File tempFile = File.createTempFile("test-", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile.getAbsolutePath());
        Epic epic = new Epic("Ремонт", "Доделать ремонт");
        Subtask subtask = new Subtask("ПодзадачаРемонт", "Починить кран", epic);

        manager.createEpic(epic);
        manager.createSubtask(subtask);

        Assertions.assertEquals(1, manager.getAllEpics().size());
        Assertions.assertEquals(1, manager.getAllSubtasks().size());
    }

    @Test
    public void checkReadFileAndCreateTaskEpicSubtask() throws IOException {
        File tempFile = File.createTempFile("test-", ".csv");
        tempFile.deleteOnExit();

        try (BufferedWriter writer = Files.newBufferedWriter(tempFile.toPath())) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();
            writer.write("1,Epic,Имя,NEW,Проверка имени,");
            writer.newLine();
            writer.write("2,Subtask,ИмяСабтаски1,NEW,ИмяСабтаски1,1,");
            writer.newLine();
            writer.write("3,Subtask,ИмяСабтаски2,NEW,ИмяСабтаски2,1,");
        } catch (IOException e) {
            e.getMessage();
        }

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tempFile);

        Assertions.assertEquals(1, manager.getAllEpics().size());
        Assertions.assertEquals(2, manager.getAllSubtasks().size());
    }
}