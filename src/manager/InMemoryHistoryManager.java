package manager;

import entities.Node;
import entities.Task;
import java.util.*;


public class InMemoryHistoryManager implements HistoryManager {

    public final Map<Integer, Node> historyMap = new HashMap<>();
    public Node head;
    public Node tail;


    public void linkLast(Node node) {
        if (head == null) {
            head = node;
            tail = node;
            historyMap.put(node.task.getId(), node);
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
            historyMap.put(node.task.getId(), node);
        }
    }

    public List<Node> getTask() {
        List<Node> nodes = new ArrayList<>(historyMap.values());
        return nodes;
    }

    @Override
    public void add(Task task) {
        Node node = new Node(task);

        if (historyMap.containsKey(task.getId())) {
            remove(task.getId());
        }

        linkLast(node);
    }

    @Override
    public void remove(int id) {
        Node node = historyMap.get(id);
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        List<Node> nodes = getTask();
        List<Task> historyTasks = new ArrayList<>();

        for (Node node : nodes) {
            historyTasks.add(node.task);
        }

        return historyTasks;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        if (node == head && node == tail) {
            head = null;
            tail = null;
        } else if (node == head) {
            head = node.next;
            if (head != null) {
                head.prev = null;
            }
        } else if (node == tail) {
            tail = node.prev;
            if (tail != null) {
                tail.next = null;
            }
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        historyMap.remove(node.task.getId());
    }
}