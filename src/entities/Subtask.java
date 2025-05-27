package entities;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    /*
    Если у нашей Subtask не установлено время старта. Мы пропустим метод epic.addSubtask(this)
     */
    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId, int duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        this.epicId = epicId;

//        for (Subtask s : epic.getSubtasks()) {
//            if (this.isNotIntersectionWithExistingTask(s) != true) {
//                throw new TaskTimeConflictException("Задачу добавить невозможно: конфликт во времени между задачами.");
//            }
//        }
//
//        epic.addSubtask(this);
    }

    @Override
    public Integer getEpic() {
        return epicId;
    }

    @Override
    public Subtask setStartTime(LocalDateTime startTime) {
        Subtask newSubtask = new Subtask(this.name, this.description, this.epicId, this.duration, startTime);
        newSubtask.setId(this.getId());
        return newSubtask;
    }
}