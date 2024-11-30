package task_tast;

import entities.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void checkForEqualityTaskForId(){
        Task task1 = new Task("Уборка в лесу", "Убрать мусор в лесу");
        Task task2 = new Task("Уборка в лесу", "Убрать мусор в лесу");

        task1.setId(1);
        task2.setId(1);

        Assertions.assertEquals(task1, task2);
    }

    @Test
    public void checkForNotEqualsTaskForId(){
        Task task1 = new Task("Уборка в лесу", "Убрать мусор в лесу");
        Task task2 = new Task("Уборка в лесу", "Убрать мусор в лесу");

        task1.setId(1);
        task2.setId(2);

        Assertions.assertNotEquals(task1, task2);
    }
}