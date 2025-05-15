package task;

import java.util.ArrayList;

public class Epic extends Task{
    private final ArrayList<Subtask> subtasks;

    public Epic(String nameTask, String taskDescription, TaskStatus taskStatus) {
        super(nameTask, taskDescription, taskStatus);
        this.subtasks = new ArrayList<Subtask>();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        return String.format(super.toString() + "subtasks=%s", subtasks.toString());
    }
}
