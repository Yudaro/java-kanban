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
    final private Map<Integer, Epic> epics;
    final private Map<Integer, Task> tasks;
    final private Map<Integer, Subtask> subtasks;
    final private HistoryManager historyManager;

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
    public Epic idSearchEpic(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Task idSearchTask(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask idSearchSubtask(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
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

    @Override
    public void deleteTaskById(int id) {
            tasks.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        subtasks.remove(id);
        updateEpicStatus(subtasks.get(id).getEpic());
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