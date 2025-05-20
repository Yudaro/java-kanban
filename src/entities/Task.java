package entities;

import enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected int id;
    protected int duration;
    protected LocalDateTime startTime;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.duration = 10;
        this.startTime = null;
    }

    public Task(String name, String description, int duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.duration = duration;
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public int getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(Duration.ofMinutes(duration));
    }

    public Task setStartTime(LocalDateTime startTime) {
        Task task = new Task(this.name, this.description, this.duration, startTime);
        task.setId(this.getId());
        return task;
    }

    public boolean isNotIntersectionWithExistingTask(Task task) {
        boolean isNewTaskEarlier = false;
        boolean isNewTaskLater = false;

        if (task.getEndTime().isBefore(this.getStartTime()) || task.getEndTime().equals(this.getStartTime())) {
            isNewTaskEarlier = true;
        }
        if (this.getEndTime().isBefore(task.getStartTime()) || this.getEndTime().equals(task.getStartTime())) {
            isNewTaskLater = true;
        }
        return (isNewTaskEarlier || isNewTaskLater);
        // вернет true сли хотя бы одно условие выполняется.
    }

    public void setId(int id) {
        if (id > 0) {
            this.id = id;
        }
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Integer getEpic() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "entities.Task{" +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                '}';
    }
}