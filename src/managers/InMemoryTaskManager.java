package managers;

import entities.Epic;
import entities.Subtask;
import entities.Task;
import enums.TaskStatus;
import exceptions.TaskTimeConflictException;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int counter;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Subtask> subtasks;
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private void checkIntersections(Task task) {
        boolean isNotIntersections;
        for (Task t : getPrioritizedTasks()) {
            isNotIntersections = t.isNotIntersectionWithExistingTask(task);

            if (isNotIntersections == false) {
                throw new TaskTimeConflictException("Задачу добавить невозможно: конфлик во времени между задачами. ");
            }
        }
    }

    @Override
    public void updateEpicStatus(int id) {
        Optional<Epic> optionalEpic = getEpicById(id);
        if (optionalEpic.isPresent()) {
            Epic epic = optionalEpic.get();
            epic.setStatus(epic.checkStatusEpic());
        }
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

//        for (Epic epic : allEpics) {
//            epic.clearSubtasks();
//        }

        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtask() {
//        for(Subtask s : subtasks.values()){
//            prioritizedTasks.remove(s);
//        }
        subtasks.values().stream()
                .forEach(prioritizedTasks::remove);

        subtasks.clear();

//        for (Epic epic : epics.values()) {
//            updateEpicStatus(epic.getId());
//        }

        epics.values().stream()
                .map(Epic::getId)
                .forEach(this::updateEpicStatus);
    }

    @Override
    public void deleteAllTasks() {
//        for (Task t : tasks.values()){
//            prioritizedTasks.remove(t);
//        }

        tasks.values().stream()
                .forEach(prioritizedTasks::remove);
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
        checkIntersections(task);
        tasks.put(counter, task);
        prioritizedTasks.add(task);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        counter++;
        subtask.setId(counter);
        checkIntersections(subtask);
        subtasks.put(counter, subtask);
        updateEpicStatus(subtask.getEpic());
        prioritizedTasks.add(subtask);
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        if (!epics.containsKey(id)) {
            return Optional.empty();
        }

        Epic epic = epics.get(id);
        historyManager.add(epic);
        return Optional.ofNullable(epic);
    }

    @Override
    public Optional<Task> getTaskById(int id) {
        if (!tasks.containsKey(id)) {
            return Optional.empty();
        }

        Task task = tasks.get(id);
        historyManager.add(task);
        return Optional.ofNullable(task);
    }

    @Override
    public Optional<Subtask> getSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            return Optional.empty();
        }

        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return Optional.ofNullable(subtask);
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
//        if (epics.containsKey(id)) {
//            Epic epic = epics.get(id);
//            for (int subtaskId : epic.getSubtasksId()) {
//                deleteSubtaskById(subtaskId);
//            }
//            epic.clearSubtasks();
//            historyManager.remove(id);
//            epics.remove(id);
//        }
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);

            epic.getSubtasksId().stream()
                    .forEach(this::deleteSubtaskById);

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
    public void updateStatusSubtask(Subtask subtask, TaskStatus taskStatus) {
        subtask.setStatus(taskStatus);
        updateEpicStatus(subtask.getEpic());
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }
}