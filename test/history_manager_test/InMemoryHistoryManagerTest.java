package history_manager_test;

import entities.Epic;
import entities.Task;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class InMemoryHistoryManagerTest {

    @Test
    public void checkIfTasksAreSavedInHistory(){
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = taskManager.getHistoryManager();
        Task task = new Task("Уборка", "Убрать квартиру");

        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task.getId());


       List<Task> historyTasks = historyManager.getHistory();

        Assertions.assertEquals(2, historyTasks.size());
        Assertions.assertEquals(task, historyTasks.get(0));
    }

    @Test
    public void checkThatThePreviousTaskIsSavedInTheHistoryWhenAddingANewTask(){
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = taskManager.getHistoryManager();
        Task task = new Task("Уборка", "Убрать квартиру");
        Epic epic = new Epic("Ремонт", "Отремонтировать квартиру");

        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());

        List<Task> historyTasks = historyManager.getHistory();

        Assertions.assertEquals(2, historyTasks.size());
        Assertions.assertEquals(task, historyTasks.get(0));
    }

    @Test
    public void checkThatTheOldestTaskIsDeletedWhenTheHistorySizeIsExceeded(){
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = taskManager.getHistoryManager();
        Task task = new Task("Уборка", "Убрать квартиру");
        Epic epic = new Epic("Ремонт", "Отремонтировать квартиру");

        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.getTaskById(task.getId());

        for(int i = 0; i < 10; i++){
            taskManager.getEpicById(epic.getId());
        }

        List<Task> historyTasks = historyManager.getHistory();

        System.out.println(historyTasks.size());

        Assertions.assertEquals(10, historyTasks.size());
        Assertions.assertNotEquals(task, historyTasks.get(0));
    }
}