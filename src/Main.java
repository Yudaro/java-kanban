public class Main {
    public static void main(String[] args){
        String descriptionEpic1 = "Необходимо пройтись по лесу и убрать несколько участков, " +
                "которые помечены желтыми флажками.";
        String descriptionEpic2 = "Необходимо навести порядок на территории 3 домов.";
        Epic epic1 = new Epic("Уборка муссора в лесу", descriptionEpic1);
        Epic epic2 = new Epic("Уборка мусора в городе", descriptionEpic2);
        Subtask subtask1 = new Subtask("Уборка в лесу", "Сложить муссов в пакеты", epic1);
        Subtask subtask2 = new Subtask("Уборка в лесу", "Вывести пакеты из леса", epic1);
        Subtask subtask1Epic2 = new Subtask("Уборка муссора в городе", "Сложить муссор " +
                "по пакетам", epic2);

        Manager manager = new Manager();
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask1Epic2);

        System.out.println(epic1);
        System.out.println(epic2);

        System.out.println(subtask1);
        System.out.println(subtask2);
        System.out.println(subtask1Epic2);

        manager.updateStatusSubtask(subtask1, TaskStatus.IN_PROGRESS);
        manager.updateStatusSubtask(subtask2, TaskStatus.IN_PROGRESS);
        manager.updateStatusSubtask(subtask1Epic2, TaskStatus.DONE);
        Subtask subtask2Epic2 = new Subtask("привет мир", "пока мир", epic2);
        manager.createSubtask(subtask2Epic2);
        System.out.println("Изменили статус подзадачи.");

        System.out.println(epic1);
        System.out.println(epic2);

        System.out.println(subtask1);
        System.out.println(subtask2);
        System.out.println(subtask1Epic2);
        System.out.println(subtask2Epic2);
    }
}