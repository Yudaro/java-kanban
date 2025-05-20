package entities;

import exceptions.TaskTimeConflictException;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private Epic epic;

    /*
    Если у нашей Subtask не установлено время старта. Мы пропустим метод epic.addSubtask(this)
     */
    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
    }

    public Subtask(String name, String description, Epic epic, int duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        this.epic = epic;

        for (Subtask s : epic.getSubtasks()) {
            if (this.isNotIntersectionWithExistingTask(s) != true) {
                throw new TaskTimeConflictException("Задачу добавить невозможно: конфликт во времени между задачами.");
            }
        }

        epic.addSubtask(this);
    }

    @Override
    public Integer getEpic() {
        return epic.getId();
    }

    @Override
    public Subtask setStartTime(LocalDateTime startTime) {
        Subtask newSubtask = new Subtask(this.name, this.description, this.epic, this.duration, startTime);
        newSubtask.setId(this.getId());
        return newSubtask;
    }
}