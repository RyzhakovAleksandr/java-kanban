package manager;

public class Managers {
    private final TaskManager manager = new InMemoryTaskManager();

    public TaskManager getDefault() {
        return manager;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
