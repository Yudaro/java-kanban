import entities.Epic;
import entities.Subtask;
import manager.HistoryManager;
import manager.InMemoryTaskManager;

public class Main {
    public static void main(String[] args) {
        String descriptionEpic1 = "Необходимо пройтись по лесу и убрать несколько участков, " +
                "которые помечены желтыми флажками.";
        String descriptionEpic2 = "Необходимо навести порядок на территории 3 домов.";
        Epic epic1 = new Epic("Уборка муссора в лесу", descriptionEpic1);
        Subtask subtask1 = new Subtask("Уборка в лесу", "Сложить муссов в пакеты", epic1);
        Subtask subtask2 = new Subtask("Уборка в лесу", "Вывести пакеты из леса", epic1);

        InMemoryTaskManager manager = new InMemoryTaskManager();
        HistoryManager historyManager = manager.getHistoryManager();
        manager.createEpic(epic1);

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Epic epic = manager.getEpicById(epic1.getId());
        Subtask subtasktest1 = manager.getSubtaskById(subtask1.getId());
        Subtask subtasktest2 = manager.getSubtaskById(subtask2.getId());
        System.out.println("Количество задач в истории = " + historyManager.getHistory().size());

        manager.deleteEpicById(epic1.getId());

        System.out.println("Количество задач в истории = " + historyManager.getHistory().size());
    }
}