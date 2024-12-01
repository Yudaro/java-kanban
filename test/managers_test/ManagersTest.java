package managers_test;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ManagersTest {

    @Test
    public void managersRemoveClassNotNull(){
        TaskManager manager1 = Managers.getDefault();
        HistoryManager manager2 = Managers.getDefaultHistory();

        Assertions.assertNotNull(manager1, "Такс менеджер не должен быть равен null");
        Assertions.assertNotNull(manager2, "Хистори менеджер не должен быть равен null");
    }
}