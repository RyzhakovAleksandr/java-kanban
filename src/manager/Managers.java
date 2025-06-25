package manager;

public class Managers {
    private final TaskManager DEFAULTMANAGER = new InMemoryTaskManager();

    public TaskManager getDefault() {
        return DEFAULTMANAGER;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
