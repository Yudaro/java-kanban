package task_manager_tests;

import entities.Epic;
import entities.Subtask;
import entities.Task;
import enums.TaskStatus;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    // Проверяем создание и получение Task по ID.
    @Test
    public void checkAddAndSearchTaskById() {
        Task task = new Task("Уборка снега", "Уборка снега на территории платца");

        manager.createTask(task);
        List<Task> tasks = manager.getAllTasks();
        Assertions.assertEquals(1, tasks.size());

        Task task2 = manager.getTaskById(task.getId()).orElseThrow(() -> new AssertionError("Task not found"));
        Assertions.assertNotNull(task2);
    }

    // Проверяем создание и получение Subtask по ID.
    @Test
    public void checkAddAndSearchSubtaskById() {
        Epic epic = new Epic("Домашние дела", "Навести порядок дома");
        Subtask subtask1 = new Subtask("Посуда", "Помыть посуду", epic, 10, LocalDateTime.now());

        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        List<Subtask> subtasks = manager.getAllSubtasks();
        Assertions.assertEquals(1, subtasks.size());

        Subtask subtask2 = manager.getSubtaskById(subtask1.getId()).orElseThrow(() -> new AssertionError("Subtask not found"));
        Assertions.assertNotNull(subtask2);
    }

    // Проверяем создание и получение Epic по ID.
    @Test
    public void checkAddAndSearchEpicById() {
        Epic epic1 = new Epic("Домашние дела", "Навести порядок дома");

        manager.createEpic(epic1);
        List<Epic> epics = manager.getAllEpics();
        Assertions.assertEquals(1, epics.size());

        Epic epic2 = manager.getEpicById(epic1.getId()).orElseThrow(() -> new AssertionError("Epic not found"));
        Assertions.assertNotNull(epic2);
    }

    // Проверяем, что Manager самостоятельно генерирует id при создании Задачи.
    @Test
    public void checkThatTheManagerGeneratesTheIdByItself() {
        Task task1 = new Task("ДЗ", "Выполнить дз по 5 стринту");
        task1.setId(5);

        manager.createTask(task1);
        Assertions.assertNotEquals(5, task1.getId());
    }

    // Проверяем, что поля Task не изменяются при добавлении задачи через manager.
    @Test
    public void checkThatTaskFieldsAreNotChangedWhenAddedToTheManager() {
        Task task = new Task("Домашние дела", "Пропылесосить коредор");
        task.setId(10);
        manager.createTask(task);

        Assertions.assertEquals("Домашние дела", task.getName());
        Assertions.assertEquals("Пропылесосить коредор", task.getDescription());
        Assertions.assertNotEquals(10, task.getId());
        Assertions.assertEquals(TaskStatus.NEW, task.getStatus());
    }

    // Проверяем, что поля Subtask не изменяются при добавлении задачи через manager.
    @Test
    public void checkThatSubtaskFieldsAreNotChangedWhenAddedToTheManager() {
        Epic epic = new Epic("Домашние задачи", "Навести порядок дома");
        Subtask subtask = new Subtask("Домашние дела", "Убрать гардероб", epic, 10, LocalDateTime.now());
        subtask.setId(10);
        manager.createEpic(epic);
        manager.createSubtask(subtask);

        Assertions.assertEquals("Домашние дела", subtask.getName());
        Assertions.assertEquals("Убрать гардероб", subtask.getDescription());
        Assertions.assertNotEquals(10, subtask.getId());
        Epic epic2 = manager.getEpicById(subtask.getEpic())
                .orElseThrow(() -> new AssertionError("Epic not found"));
        Assertions.assertEquals(epic, epic2);
        Assertions.assertEquals(TaskStatus.NEW, subtask.getStatus());
    }

    // Проверяем, что поля Subtask не изменяются при добавлении задачи через manager.
    @Test
    public void checkThatEpicFieldsAreNotChangedWhenAddedToTheManager() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic("Домашние задачи", "Навести порядок дома");
        epic.setId(10);
        manager.createEpic(epic);

        Assertions.assertEquals("Домашние задачи", epic.getName());
        Assertions.assertEquals("Навести порядок дома", epic.getDescription());
        Assertions.assertNotEquals(10, epic.getId());
        Assertions.assertEquals(TaskStatus.NEW, epic.getStatus());
    }
}