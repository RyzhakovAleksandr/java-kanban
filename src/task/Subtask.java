package task;

public class Subtask extends Task{
    private Epic epic;

    public Subtask(String nameTask, String taskDescription, TaskStatus taskStatus, Epic epic) {
        super(nameTask, taskDescription, taskStatus);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
