package task_manager_tests;

import entities.Task;
import exceptions.TaskTimeConflictException;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {

    @BeforeEach
    void init() {
        manager = Managers.getDefault();
    }

    @Test
    public void checkThatManagerIsAbleToDetectIntersections() {
        Task task1 = new Task("Задача1", "Проверка пересечения", 10, LocalDateTime.now());
        Task task2 = new Task("Задача1", "Проверка пересечения", 10, LocalDateTime.now().plusSeconds(100));

        manager.createTask(task1);

        Assertions.assertThrows(TaskTimeConflictException.class, () -> {
            manager.createTask(task2);
        });
    }

    @Test
    public void checkingThatWeCanAddTasksWithoutIntersections() {
        Task task1 = new Task("Задача1", "Проверка пересечения", 10, LocalDateTime.now());
        Task task2 = new Task("Задача2", "Проверка пересечения", 10, LocalDateTime.now().plusSeconds(600));

        manager.createTask(task1);
        manager.createTask(task2);

        Task task3 = new Task("Задача1", "Проверка пересечения", 10, task1.getStartTime().minusSeconds(600));

        manager.createTask(task3);
    }
}