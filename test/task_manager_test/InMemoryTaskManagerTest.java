package task_manager_test;

import entities.Epic;
import entities.Subtask;
import entities.Task;
import enums.TaskStatus;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class InMemoryTaskManagerTest {

    @Test
    public void checkAddAndSearchTaskById(){
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Уборка снега", "Уборка снега на территории платца");

        taskManager.createTask(task);
        List<Task> tasks = taskManager.getAllTasks();
        Assertions.assertEquals(1, tasks.size());

        Task task2 = taskManager.getTaskById(task.getId());
        Assertions.assertNotNull(task2);
    }

    @Test
    public void checkAddAndSearchSubtaskById(){
        TaskManager taskManager = Managers.getDefault();
        Epic epic = new Epic("Домашние дела", "Навести порядок дома");
        Subtask subtask1 = new Subtask("Посуда", "Помыть посуду", epic);

        taskManager.createSubtask(subtask1);
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        Assertions.assertEquals(1, subtasks.size());

        Subtask subtask2 = taskManager.getSubtaskById(subtask1.getId());
        Assertions.assertNotNull(subtask2);
    }

    @Test
    public void checkAddAndSearchEpicById(){
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Домашние дела", "Навести порядок дома");

        taskManager.createEpic(epic1);
        List<Epic> epics = taskManager.getAllEpics();
        Assertions.assertEquals(1, epics.size());

        Epic epic2 = taskManager.getEpicById(epic1.getId());
        Assertions.assertNotNull(epic2);
    }

    @Test
    public void checkThatTheManagerGeneratesTheIdByItself(){
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task("ДЗ", "Выполнить дз по 5 стринту");
        task1.setId(5);

        manager.createTask(task1);
        Assertions.assertNotEquals(5, task1.getId());

        Task task2 = new Task("Уроки", "Делаем дз по русскому");
        task2.setId(1);
        manager.createTask(task2);
        List<Task> tasks = manager.getAllTasks();
        Assertions.assertEquals(2, tasks.size());
    }

    @Test
    public void checkThatTaskFieldsAreNotChangedWhenAddedToTheManager(){
        Task task = new Task("Домашние дела", "Пропылесосить коредор");
        TaskManager manager = Managers.getDefault();
        task.setId(10);
        manager.createTask(task);

        Assertions.assertEquals("Домашние дела", task.getName());
        Assertions.assertEquals("Пропылесосить коредор", task.getDescription());
        Assertions.assertNotEquals(10, task.getId());
        Assertions.assertEquals(TaskStatus.NEW, task.getStatus());
    }

    @Test
    public void checkThatSubtaskFieldsAreNotChangedWhenAddedToTheManager(){
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic("Домашние задачи", "Навести порядок дома");
        Subtask subtask = new Subtask("Домашние дела", "Убрать гардероб", epic);
        subtask.setId(10);
        manager.createSubtask(subtask);
        manager.createEpic(epic);

        Assertions.assertEquals("Домашние дела", subtask.getName());
        Assertions.assertEquals("Убрать гардероб", subtask.getDescription());
        Assertions.assertNotEquals(10, subtask.getId());
        Assertions.assertEquals(epic, subtask.getEpic());
        Assertions.assertEquals(TaskStatus.NEW, subtask.getStatus());
    }

    @Test
    public void checkThatEpicFieldsAreNotChangedWhenAddedToTheManager(){
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