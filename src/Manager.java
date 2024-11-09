import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager {
    private int counter;
    final private Map<Integer, Epic> epics;
    final private Map<Integer, Task> tasks;
    final private Map<Integer, Subtask> subtasks;

    public Manager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    private TaskStatus checkStatusEpic(Epic epic) {
        if (epic.isStatusSubtaskNew()) {
            return TaskStatus.NEW;
        } else if (epic.isStatusSubtaskDone()) {
            return TaskStatus.DONE;
        } else {
            return TaskStatus.IN_PROGRESS;
        }
    }

    private void updateEpicStatus(Epic epic) {
        epic.setStatus(checkStatusEpic(epic));
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllEpics() {
        List<Epic> allEpics = getAllEpics();

        for (Epic epic : allEpics) {
            epic.clearSubtasks();
        }
        epics.clear();
        subtasks.clear();
    }

    public void deleteAllSubtask() {
        subtasks.clear();

        for (Epic epic : epics.values()){
            updateEpicStatus(epic);
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void createEpic(Epic epic) {
        counter++;
        epic.setId(counter);
        epics.put(counter, epic);
    }

    public void createTask(Task task) {
        counter++;
        task.setId(counter);
        tasks.put(counter, task);
    }

    public void createSubtask(Subtask subtask) {
        counter++;
        subtask.setId(counter);
        subtasks.put(counter, subtask);
    }

    public Epic idSearchEpic(int id) {
        return epics.get(id);
    }

    public Task idSearchTask(int id) {
        return tasks.get(id);
    }

    public Subtask idSearchSubtask(int id) {
        return subtasks.get(id);
    }

    public void UpdateEpic(Epic epic) {
        if (epics.containsKey(epic.id)) {
            epics.put(epic.id, epic);
        }
    }

    public void UpdateTask(Task task) {
        if (tasks.containsKey(task.id)) {
            tasks.put(task.id, task);
        }
    }

    public void UpdateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.id)) {
            subtasks.put(subtask.id, subtask);
        }
    }

    public void deleteEpicById(int id) {
        List<Integer> subtasksId;
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (int subtaskId : subtasksId = epic.getSubtasksId()) {
                deleteSubtaskById(subtaskId);
            }
            epic.clearSubtasks();
            epics.remove(id);
        }
    }

    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        }
    }

    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            subtasks.remove(id);
        }
        updateEpicStatus(subtasks.get(id).getEpic());
    }

    public List<Subtask> returnAllSubtasks(Epic epic) {
        return epic.getSubtasks();
    }

    public void updateStatusSubtask(Subtask subtask, TaskStatus taskStatus ){
        subtask.setStatus(taskStatus);
        updateEpicStatus(subtask.getEpic());
    }
}