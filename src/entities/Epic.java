package entities;

import enums.TaskStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtasks;
    private Instant endTime;

    public Epic(String name, String description) {
        super(name, description, 0, null);
        subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        setStatus(checkStatusEpic());
        calculateEpicDuration(subtask);
        calculateEpicStartTime(subtask);
        calculateEpicEndTime(subtask);
    }

    public void clearSubtasks() {
        subtasks.clear();
        duration = 0;
        startTime = null;
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

    private void calculateEpicDuration(Subtask sabtask) {
        duration += sabtask.getDuration();
    }

    private void calculateEpicStartTime(Subtask subtask) {
        if (startTime == null) {
            startTime = subtask.getStartTime();
        } else {
            if (subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }
        }
    }

    private void calculateEpicEndTime(Subtask subtask) {
        if (endTime == null) {
            endTime = subtask.getEndTime();
        } else {
            if (endTime.isBefore(subtask.getEndTime())) {
                endTime = subtask.getEndTime();
            }
        }
    }
}