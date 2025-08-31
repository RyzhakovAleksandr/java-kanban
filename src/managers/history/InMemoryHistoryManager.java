package managers.history;

import task.BaseTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<BaseTask>> history;
    private Node<BaseTask> head;
    private Node<BaseTask> tail;

    private static class Node<T> {
        public Node<T> previous;
        public T value;
        public Node<T> next;

        public Node(Node<T> previous, T value, Node<T> next) {
            this.previous = previous;
            this.value = value;
            this.next = next;
        }
    }

    public InMemoryHistoryManager() {
        this.history = new HashMap<>();
        head = null;
        tail = null;
    }

    @Override
    public void add(BaseTask task) {
        if (task == null) {
            return;
        }
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        removeNode(history.get(id));
    }

    @Override
    public List<BaseTask> getHistory() {
        return getTasks();
    }

    private List<BaseTask> getTasks() {
        List<BaseTask> tasks = new ArrayList<>();
        Node<BaseTask> currentNode = head;

        while (currentNode != null) {
            tasks.add(currentNode.value);
            currentNode = currentNode.next;
        }

        return tasks;
    }

    private void linkLast(BaseTask task) {
        final Node<BaseTask> oldTail = tail;
        final Node<BaseTask> newTail = new Node<>(tail, task, null);
        tail = newTail;
        history.put(task.getId(), newTail);

        if (oldTail == null) {
            head = newTail;
        } else {
            oldTail.next = newTail;
        }
    }

    private void removeNode(Node<BaseTask> node) {
        if (node == null) {
            return;
        }

        Node<BaseTask> previous = node.previous;
        Node<BaseTask> next = node.next;
        node.value = null;

        if (head == node && tail == node) {
            head = null;
            tail = null;
        } else if (head == node) {
            head = next;
            head.previous = null;
        } else if (tail == node) {
            tail = previous;
            tail.next = null;
        } else {
            previous.next = next;
            next.previous = null;
        }

    }
}