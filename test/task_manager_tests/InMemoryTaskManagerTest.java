package task_manager_tests;

import entities.Epic;
import entities.Subtask;
import entities.Task;
import exceptions.TaskTimeConflictException;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

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

    @Test
    public void checkTheCorrectnessOfSortingTasksByStartTime() {
        Task task1 = new Task("Задача1", "Проверка сортировки", 10, LocalDateTime.of(2025, Month.MAY, 19, 7, 10));
        Task task2 = new Task("Задача2", "Проверка сортировки", 10, LocalDateTime.of(2025, Month.MAY, 19, 8, 10));
        Task task3 = new Task("Задача3", "Проверка сортировки", 10, LocalDateTime.of(2025, Month.MAY, 19, 6, 10));

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        Assertions.assertEquals(task3, manager.getPrioritizedTasks().toArray()[0]);
    }

    @Test
    public void checkThePossibilityOfChangingTheStartTimeTaskSubtaskaskAndItsFurtherFiltering() {
        Epic epic = new Epic("Епик", "Описание Епика");
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask("Сабтаск1", "Проверка сортировки", epic.getId(), 10, LocalDateTime.of(2025, Month.JUNE, 10, 6, 10));
        Subtask subtask2 = new Subtask("Сабтаск1", "Проверка сортировки", epic.getId(), 10, LocalDateTime.of(2025, Month.JUNE, 10, 7, 10));
        Task task1 = new Task("Тас1", "Описание таск1", 10, LocalDateTime.of(2025, Month.JUNE, 10, 8, 10));
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createTask(task1);

        Assertions.assertEquals(subtask1, manager.getPrioritizedTasks().toArray()[0]);

        Task newTask = task1.setStartTime(LocalDateTime.of(2025, Month.JUNE, 10, 5, 10));
        manager.updateTask(newTask);

        Assertions.assertEquals(newTask, manager.getPrioritizedTasks().toArray()[0]);
        Assertions.assertEquals(1, manager.getAllTasks().size());

        Subtask newSubtask = subtask1.setStartTime(LocalDateTime.of(2025, Month.JUNE, 10, 8, 10));
        manager.updateSubtask(newSubtask);

        Assertions.assertEquals(newSubtask, manager.getPrioritizedTasks().toArray()[2]);

        manager.getPrioritizedTasks().stream()
                .forEach(t -> System.out.println(t.getStartTime()));
    }

    @Test
    public void checkThatTaskAndSubtaskWithTheStartTimeNullFieldAreNotAddedToTheSortedList() {
        Epic epic = new Epic("Епик", "Описание Епика");
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask("Сабтаск1", "Проверка сортировки", epic.getId());
        Subtask subtask2 = new Subtask("Сабтаск1", "Проверка сортировки", epic.getId(), 10, LocalDateTime.of(2025, Month.JUNE, 10, 7, 10));
        Task task1 = new Task("Тас1", "Описание таск1");

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createTask(task1);

        Assertions.assertEquals(1, manager.getPrioritizedTasks().size());
    }

    @Test
    public void checkThatAfterChangingTheStartTimeFieldFromNullToTimeTaskAndSubtaskAddedAndSorted() {
        Epic epic = new Epic("Епик", "Описание Епика");
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask("Сабтаск1", "Проверка сортировки", epic.getId());
        Subtask subtask2 = new Subtask("Сабтаск2", "Проверка сортировки", epic.getId(), 10, LocalDateTime.of(2025, Month.JUNE, 10, 7, 10));
        Task task1 = new Task("Тас1", "Описание таск1");

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createTask(task1);

        Assertions.assertEquals(1, manager.getPrioritizedTasks().size());

        Task newTask = task1.setStartTime(LocalDateTime.of(2025, Month.JUNE, 10, 8, 10));
        manager.updateTask(newTask);
        Subtask newSubtask = subtask1.setStartTime(LocalDateTime.of(2025, Month.JUNE, 10, 9, 10));
        manager.updateSubtask(newSubtask);

        Assertions.assertEquals(3, manager.getPrioritizedTasks().size());
        Assertions.assertEquals(newSubtask, manager.getPrioritizedTasks().toArray()[2]);

        manager.getPrioritizedTasks().stream()
                .forEach(t -> System.out.println(t.getStartTime()));
    }
}