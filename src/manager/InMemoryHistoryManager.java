package manager;

import entities.Task;
import java.util.*;


public class InMemoryHistoryManager implements HistoryManager {
    final private List<Task> historyTasks;

    public InMemoryHistoryManager(){
        historyTasks = new ArrayList<>();
    }


    @Override
    public void add(Task task){
        if (task == null){
            return;
        } else if(historyTasks.size() < 10){
            historyTasks.add(task);
        }else{
            historyTasks.remove(0);
            historyTasks.add(task);
        }
    }

    @Override
    public List<Task> getHistory(){
        return List.copyOf(historyTasks);
    }
}
