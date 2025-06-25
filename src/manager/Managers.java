package manager;

public class Managers {
    private final TaskManager MANAGER = new InMemoryTaskManager();

    public TaskManager getDefault() {
        return MANAGER;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
