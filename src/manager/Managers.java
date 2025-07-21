package manager;

import java.io.File;

public class Managers {
    private final TaskManager manager = new FileBackedTaskManager(new File("resources%sdata.csv".formatted(File.separator)));

    public TaskManager getDefault() {
        return manager;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
