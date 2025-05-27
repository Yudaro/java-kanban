package managers;

import entities.Epic;
import entities.Subtask;
import entities.Task;
import enums.TaskStatus;
import exceptions.ManagerSaveException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class FileBackedTaskManager extends InMemoryTaskManager {
    public static void main(String[] args) {
        Epic epic = new Epic("Епик1", "Доделать 7 фз");
        Subtask subtask = new Subtask("Сабтаск1", "Реализовать метод main и протестировать фз", epic.getId(), 10, LocalDateTime.now());
        Subtask subtask2 = new Subtask("Сабтаск2", "Реализовать метод main и протестировать фз8", epic.getId(), 20, LocalDateTime.now().plusSeconds(700));
        FileBackedTaskManager manager1 = new FileBackedTaskManager("tasks.csv");
        manager1.createEpic(epic);
        manager1.createSubtask(subtask);
        manager1.createSubtask(subtask2);

        System.out.println(epic.getStartTime());
        System.out.println(epic.getEndTime());
        System.out.println(epic.getDuration());

        FileBackedTaskManager manager2 = new FileBackedTaskManager(manager1.saveFile);
        System.out.println(manager2.getAllEpics().size());
        System.out.println(manager2.getAllSubtasks().size());
    }

    File saveFile;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    /*
    Конструктор класса, мы получаем имя файла для дальнейшего удобства, после чего сами создаем объект Path для
    дальнейше работы
     */
    public FileBackedTaskManager(String fileName) {
        Path path = Paths.get(fileName);
        if (Files.exists(path)) {
            saveFile = path.toFile();
            loadFromFile();
        } else {
            //если файла нет, мы будем его создавать.
            try { //На всякий случай обрабатываем возможное исключение.
                Files.createFile(path);
                saveFile = path.toFile();
                System.out.println("Файл создан");
            } catch (IOException e) {
                throw new ManagerSaveException("Файл уже существует.");
            }
        }
    }

    public FileBackedTaskManager(File file) {
        saveFile = file;
        loadFromFile();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        return new FileBackedTaskManager(file);
    }

    /*
    Вообщем мы тут все перелопатили, теперь когда мы создаем Task и ее наследников, при считывании из файла, мы так же работаем с датами.
    Единственное есть пока большая проблема при работе с Epic, ему нужно задавать поля высчитывая их из всех его сабтаск.
    ВАЖНО!!!!!!!!!!!!!(На данный момент не реализовано)
    */
    public void loadFromFile() {
        try (BufferedReader reader = Files.newBufferedReader(saveFile.toPath())) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                int id = Integer.parseInt(parts[0]);
                String type = parts[1];
                String name = parts[2];
                TaskStatus status = TaskStatus.valueOf(parts[3]);
                String description = parts[4];
                int duration = Integer.parseInt(parts[6]);
                LocalDateTime startTime = LocalDateTime.parse(parts[7], formatter);

                switch (type) {
                    case "Task":
                        Task task = new Task(name, description, duration, startTime);
                        super.createTask(task);
                        task.setId(id);
                        task.setStatus(status);
                        break;
                    case "Epic":
                        Epic epic = new Epic(name, description);
                        super.createEpic(epic);
                        epic.setId(id);
                        epic.setStatus(status);
                        break;
                    case "Subtask":
                        Optional<Epic> optionalEpic = super.getEpicById(Integer.parseInt(parts[5]));
                        if (optionalEpic.isPresent()) {
                            Epic epicForSubtask = optionalEpic.get();
                            Subtask subtask = new Subtask(name, description, epicForSubtask.getId(), duration, startTime);
                            super.createSubtask(subtask);
                            subtask.setId(id);
                            subtask.setStatus(status);
                        }
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении из файла.");
        }
    }

    //Мы должны пройтись по всем задачам из имеющихся у нас hashMap и записать их все в файл, файл будем пересоздавать
    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(saveFile.toPath(), StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write("id,type,name,status,description,epic,duration,startTime,");
            writer.newLine();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл.");
        }

        try (BufferedWriter writer = Files.newBufferedWriter(saveFile.toPath(), StandardOpenOption.APPEND)) {
            for (Task task : tasks.values()) {
                writer.write(standartTaskToString(task));
                writer.newLine();
            }
            for (Epic epic : epics.values()) {
                writer.write(standartTaskToString(epic));
                writer.newLine();
            }
            for (Subtask subtask : subtasks.values()) {
                writer.write(standartTaskToString(subtask));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл.");
        }
    }

    private String standartTaskToString(Task task) {
        StringBuilder builder = new StringBuilder();
        builder.append(task.getId());
        builder.append(",");
        builder.append(task.getClass().getSimpleName());
        builder.append(",");
        builder.append(task.getName());
        builder.append(",");
        builder.append(task.getStatus());
        builder.append(",");
        builder.append(task.getDescription());
        builder.append(",");
        builder.append(task.getEpic());
        builder.append(",");
        builder.append(task.getDuration());
        builder.append(",");
        if (task.getStartTime() != null) {
            builder.append(task.getStartTime().format(formatter));
        } else {
            builder.append("null");
        }
        builder.append(",");
        return builder.toString();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }
}