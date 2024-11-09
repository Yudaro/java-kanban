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
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

    public List<Integer> getSubtasksId() {
        ArrayList<Integer> subtasksId = new ArrayList<>();

        for (Subtask subtask : subtasks) {
            subtasksId.add(subtask.getId());
        }

        return subtasksId;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public boolean isStatusSubtaskNew() {
        boolean isStatusNew = true;

        for (Subtask subtask : subtasks) {
            if (subtask.status != TaskStatus.NEW) {
                return isStatusNew = false;
            }
        }
        return isStatusNew;
    }

    public boolean isStatusSubtaskDone() {
        boolean isStatusDone = true;

        for (Subtask subtask : subtasks) {
            if (subtask.status != TaskStatus.DONE) {
                return isStatusDone = false;
            }
        }
        return isStatusDone;
    }
}