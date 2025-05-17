package entities;

import enums.TaskStatus;

import java.time.*;
import java.util.*;

public class Task {
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected int id;
    protected int duration;
    protected Instant startTime;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.duration = 10;
        this.startTime = Instant.now();
    }

    public Task(String name, String description, int duration, Instant startTime) {
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

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return startTime.plus(Duration.ofMinutes(duration));
    }

    public void setDuration(int duration) {
        if (duration > 0) {
            this.duration = duration;
        }
    }

    public void setStartTime(Instant startTime) {
        if (Instant.now().isBefore(startTime)) {
            this.startTime = startTime;
        }
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