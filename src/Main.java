import entities.*;
import manager.FileBackedTaskManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Main {
    public static void main(String[] args) {
        Task task = new Task("Имя1", "Описание1", 20, Instant.ofEpochSecond(1000000));
//        Task task2 = new Task("Имя2", "Описание2", 10, Instant.ofEpochSecond(10000000));
//        Task task3 = new Task("Имя3", "Описание3", 10, Instant.ofEpochSecond(100000000));
//        Epic epic = new Epic("ИмяЕпика", "ОписаниеЕпика");
//        Subtask subtask1 = new Subtask("Имя1", "Описание1", epic, 10, Instant.ofEpochSecond(1100000));
//        Subtask subtask2 = new Subtask("Имя2", "Описание2", epic, 20, Instant.ofEpochSecond(1100000));


        InMemoryTaskManager manager = new InMemoryTaskManager();
//
////        System.out.println(Instant.ofEpochSecond(1000000));
////        System.out.println(Instant.ofEpochSecond(10000000));
////        System.out.println(Instant.ofEpochSecond(100000000));
////        System.out.println(Instant.ofEpochSecond(1100000));
////        System.out.println(Instant.ofEpochSecond(1100000));
//
        manager.createTask(task);
//        manager.createTask(task2);
//        manager.createTask(task3);
//        manager.createEpic(epic);
//        manager.createSubtask(subtask1);
////        manager.createSubtask(subtask2);
//
//        for (Task t : manager.getPrioritizedTasks()){
//            System.out.println(t.getStartTime());
//        }
//
//        manager.deleteAllTasks();
//        manager.deleteAllSubtask();
//
//        System.out.println(manager.getPrioritizedTasks().size());
//
//        for (Task t : manager.getPrioritizedTasks()){
//            System.out.println(t.getStartTime());
//        }
    }
}