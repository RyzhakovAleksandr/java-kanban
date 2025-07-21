package task;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String nameTask, String taskDescription, TaskStatus taskStatus, Epic epic) {
        super(nameTask, taskDescription, taskStatus);
        this.epic = epic;
    }

    public Subtask(Integer id, String name, String description, TaskStatus taskStatus, Epic epic) {
        super(id, name, description, taskStatus);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }
}
