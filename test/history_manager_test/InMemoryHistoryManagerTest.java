package history_manager_test;

import entities.Epic;
import entities.Subtask;
import entities.Task;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
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
        Assertions.assertEquals(task, historyTasks.get(0));
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
        Assertions.assertEquals(task, historyTasks.get(0));
    }

    @Test
    public void checkThatTwoIdenticalTasksWillNotBeSavedInTheHistory() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = taskManager.getHistoryManager();
        Epic epic = new Epic("Ремонт", "Отремонтировать квартиру");

        taskManager.createEpic(epic);

        Epic epic1 = taskManager.getEpicById(epic.getId());
        Epic epic2 = taskManager.getEpicById(epic.getId());
        Assertions.assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    public void checkThatDeletingEpicAlsoDeletesHistory() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = taskManager.getHistoryManager();
        Epic epic = new Epic("Ремонт", "Отремонтировать квартиру");

        taskManager.createEpic(epic);

        Epic epic1 = taskManager.getEpicById(epic.getId());
        taskManager.deleteEpicById(epic1.getId());
        Assertions.assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    public void checkThatWhenAnEpicIsDeletedFromTheHistoryItsSubtaskIsDeleted() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = taskManager.getHistoryManager();
        Epic epic = new Epic("Ремонт", "Отремонтировать квартиру");
        Subtask subtask = new Subtask("Кухня", "Отодрать обои", epic);
        Subtask subtask1 = new Subtask("Кухня", "Починить раковину", epic);

        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask1);

        Epic testEpic = taskManager.getEpicById(epic.getId());
        Subtask testSubtask1 = taskManager.getSubtaskById(subtask.getId());
        Subtask testSubtask2 = taskManager.getSubtaskById(subtask1.getId());

        Assertions.assertEquals(3, historyManager.getHistory().size());

        taskManager.deleteEpicById(testEpic.getId());

        Assertions.assertEquals(0, historyManager.getHistory().size());
    }
}