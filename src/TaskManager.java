public class TaskManager {
    private static int IDForTasks = 0;

    public TaskManager() {

    }

    private int generateID() {
        return IDForTasks++;
    }

}
