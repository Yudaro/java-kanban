package manager;

import entities.Epic;
import entities.Subtask;
import entities.Task;
import enums.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int counter;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Subtask> subtasks;
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }


    @Override
    public void updateEpicStatus(Epic epic) {
        epic.setStatus(epic.checkStatusEpic());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllEpics() {
        List<Epic> allEpics = getAllEpics();

        for (Epic epic : allEpics) {
            epic.clearSubtasks();
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtask() {
        subtasks.clear();

        for (Epic epic : epics.values()){
            updateEpicStatus(epic);
        }
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void createEpic(Epic epic) {
        counter++;
        epic.setId(counter);
        epics.put(counter, epic);
    }

    @Override
    public void createTask(Task task) {
        counter++;
        task.setId(counter);
        tasks.put(counter, task);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        counter++;
        subtask.setId(counter);
        subtasks.put(counter, subtask);
        updateEpicStatus(subtask.getEpic());
    }

    @Override
    public Epic getEpicById(int id) {
        if (!epics.containsKey(id)){
            return null;
        }

        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Task getTaskById(int id) {
        if (!tasks.containsKey(id)){
            return null;
        }

        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (!subtasks.containsKey(id)){
            return null;
        }

        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (int subtaskId : epic.getSubtasksId()) {
                deleteSubtaskById(subtaskId);
            }
            epic.clearSubtasks();
            historyManager.remove(id);
            epics.remove(id);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        historyManager.remove(id);
            tasks.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        historyManager.remove(id);
        updateEpicStatus(subtasks.get(id).getEpic());
        subtasks.remove(id);
    }

    @Override
    public List<Subtask> returnAllSubtasks(Epic epic) {
        return epic.getSubtasks();
    }

    @Override
    public void updateStatusSubtask(Subtask subtask, TaskStatus taskStatus){
        subtask.setStatus(taskStatus);
        updateEpicStatus(subtask.getEpic());
    }

    @Override
    public HistoryManager getHistoryManager(){
        return historyManager;
    }
}