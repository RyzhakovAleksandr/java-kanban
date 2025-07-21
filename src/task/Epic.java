package task;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;

    public Epic(String nameTask, String taskDescription) {
        super(nameTask, taskDescription, TaskStatus.NEW);
        this.subtasks = new ArrayList<>();
    }

    public Epic(Integer id, String nameTask, String taskDescription, TaskStatus taskStatus) {
        super(id, nameTask, taskDescription, taskStatus);
        this.subtasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

}
