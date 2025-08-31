package managers;

import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
import managers.task.FileBackedTaskManager;
import managers.task.TaskManager;

import java.io.File;

public class Managers {

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault() {
        return new FileBackedTaskManager(getDefaultHistory(),
                new File("resources%sdata.csv".formatted(File.separator)));
    }
}
