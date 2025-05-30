package manager;

public class Managers {
    private final TaskManager DEFAULT_MANAGER = new InMemoryTaskManager();

    public TaskManager getDefault() {
        return DEFAULT_MANAGER;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
