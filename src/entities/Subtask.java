package entities;

import exception.TaskTimeConflictException;

import java.time.Instant;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
        epic.addSubtask(this);
    }

    public Subtask(String name, String description, Epic epic, int duration, Instant startTime) {
        super(name, description, duration, startTime);
        this.epic = epic;

        for (Subtask s : epic.getSubtasks()) {
            if (this.isNotIntersectionWithExistingTask(s) != true) {
                throw new TaskTimeConflictException("Задачу добавить невозможно: конфлик во времени между задачами.");
            }
        }

        epic.addSubtask(this);
    }

    @Override
    public Integer getEpic() {
        return epic.getId();
    }
}