package history_manager_tests;

import entities.Epic;
import entities.Task;
import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class InMemoryHistoryManagerTest {

    @Test
    public void checkIfTasksAreSavedInHistory() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = taskManager.getHistoryManager();
        Task task = new Task("Уборка", "Убрать квартиру");

        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task.getId());


        List<Task> historyTasks = historyManager.getHistory();

        Assertions.assertEquals(1, historyTasks.size());
        Assertions.assertEquals(task, historyTasks.getFirst());
    }

    @Test
    public void checkThatThePreviousTaskIsSavedInTheHistoryWhenAddingANewTask() {
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
        Assertions.assertEquals(task, historyTasks.getFirst());
    }

    @Test
    public void checkThatTwoIdenticalTasksWillNotBeSavedInTheHistory() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = taskManager.getHistoryManager();
        Epic epic = new Epic("Ремонт", "Отремонтировать квартиру");

        taskManager.createEpic(epic);

        taskManager.getEpicById(epic.getId());
        taskManager.getEpicById(epic.getId());
        Assertions.assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    public void checkThatDeletingEpicAlsoDeletesHistory() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = taskManager.getHistoryManager();
        Epic epic = new Epic("Ремонт", "Отремонтировать квартиру");

        taskManager.createEpic(epic);

        Epic epic1 = taskManager.getEpicById(epic.getId()).orElseThrow(() -> new AssertionError("Epic not found"));
        taskManager.deleteEpicById(epic1.getId());
        Assertions.assertEquals(0, historyManager.getHistory().size());
    }
}