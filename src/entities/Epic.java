package entities;

import enums.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    final private List<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        setStatus(checkStatusEpic());
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

    public List<Integer> getSubtasksId() {
        List<Integer> subtasksId = new ArrayList<>();

        for (Subtask subtask : subtasks) {
            subtasksId.add(subtask.getId());
        }

        return subtasksId;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    private boolean isStatusSubtaskNew() {
        boolean isStatusNew = true;

        for (Subtask subtask : subtasks) {
            if (subtask.status != TaskStatus.NEW) {
                return false;
            }
        }
        return isStatusNew;
    }

    private boolean isStatusSubtaskDone() {
        boolean isStatusDone = true;

        for (Subtask subtask : subtasks) {
            if (subtask.status != TaskStatus.DONE) {
                return false;
            }
        }
        return isStatusDone;
    }


    public TaskStatus checkStatusEpic() {
        if (isStatusSubtaskNew()) {
            return TaskStatus.NEW;
        } else if (isStatusSubtaskDone()) {
            return TaskStatus.DONE;
        } else {
            return TaskStatus.IN_PROGRESS;
        }
    }
}