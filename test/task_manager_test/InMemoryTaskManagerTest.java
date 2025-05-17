package task_manager_test;

import entities.Epic;
import entities.Subtask;
import entities.Task;
import enums.TaskStatus;
import exception.TaskTimeConflictException;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

public class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {

    @BeforeEach
    void init() {
        manager = Managers.getDefault();
    }

    @Test
    public void checkThatManagerIsAbleToDetectIntersections() {
        Task task1 = new Task("Задача1", "Проверка пересечения", 10, Instant.now());
        Task task2 = new Task("Задача1", "Проверка пересечения", 10, Instant.now().plusSeconds(100));

        manager.createTask(task1);

        Assertions.assertThrows(TaskTimeConflictException.class, () -> {
            manager.createTask(task2);
        });
    }

    @Test
    public void checkingThatWeCanAddTasksWithoutIntersections() {
        Task task1 = new Task("Задача1", "Проверка пересечения", 10, Instant.now());
        Task task2 = new Task("Задача2", "Проверка пересечения", 10, Instant.now().plusSeconds(600));

        manager.createTask(task1);
        manager.createTask(task2);

        Task task3 = new Task("Задача1", "Проверка пересечения", 10, task1.getStartTime().minusSeconds(600));

        manager.createTask(task3);
    }
}