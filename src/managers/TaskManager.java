package managers;

import entities.Epic;
import entities.Subtask;
import entities.Task;
import enums.TaskStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TaskManager {
    void updateEpicStatus(int id);

    Set<Task> getPrioritizedTasks();

    List<Epic> getAllEpics();

    List<Task> getAllTasks();

    List<Subtask> getAllSubtasks();

    void deleteAllEpics();

    void deleteAllSubtask();

    void deleteAllTasks();

    void createEpic(Epic epic);

    void createTask(Task task);

    void createSubtask(Subtask subtask);

    Optional<Epic> getEpicById(int id);

    Optional<Task> getTaskById(int id);

    Optional<Subtask> getSubtaskById(int id);

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
