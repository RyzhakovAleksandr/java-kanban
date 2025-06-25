package manager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> historyUsedTask = new ArrayList<>();
    private final HashMap<Integer, Node> history = new HashMap<>();
    private Node firstNode;
    private Node lastNode;

    @Override
    public void add(Task task) {
        if(task != null) {
            remove(task.getTaskID());
            history.put(task.getTaskID(), linkLast(task));
        }
    }

    @Override
    public void remove(int id) {
        if (history.containsKey(id)) {
            removeNode(history.get(id));
            history.remove(id);
        }
    }

    private boolean removeNode(Node node) {
        Node nodePrevious = node.previous;
        Node nodeNext = node.next;

        if(nodeNext != null) {
            nodeNext.previous = nodePrevious;
        } else {
            lastNode = nodePrevious;
        }

        if(nodePrevious != null) {
            nodePrevious.next = nodeNext;
        }else {
            firstNode = nodeNext;
        }
        return true;
    }

    @Override
    public List<Task> getTasks() {
        historyUsedTask.clear();
        Node node = firstNode;
        while (node != null) {
            historyUsedTask.add(node.value);
            node = node.next;
        }
        return historyUsedTask;
    }

    private Node linkLast(Task task) {
        if (firstNode == null) {
            firstNode = new Node(null, task, null);
            lastNode = firstNode;
        } else {
            Node secondLast = lastNode;
            lastNode = new Node(secondLast, task, null);
            secondLast.next = lastNode;
        }
        return lastNode;
    }

    private static class Node {
        private Node previous;
        private Task value;
        private Node next;

        private Node(Node previous, Task value, Node next) {
            this.previous = previous;
            this.value = value;
            this.next = next;
        }
    }

}
