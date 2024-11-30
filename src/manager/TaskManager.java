package manager;

import entities.Epic;
import entities.Subtask;
import entities.Task;
import enums.TaskStatus;

import java.util.List;

public interface TaskManager {
    void updateEpicStatus(Epic epic);

    List<Epic> getAllEpics();

    List<Task> getAllTasks();

    List<Subtask> getAllSubtasks();

    void deleteAllEpics();

    void deleteAllSubtask();

    void deleteAllTasks();

    void createEpic(Epic epic);

    void createTask(Task task);

    void createSubtask(Subtask subtask);

    Epic idSearchEpic(int id);

    Task idSearchTask(int id);

    Subtask idSearchSubtask(int id);

    void updateEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void deleteEpicById(int id);

    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    List<Subtask> returnAllSubtasks(Epic epic);

    void updateStatusSubtask(Subtask subtask, TaskStatus taskStatus);

    HistoryManager getHistoryManager();
}
