package managers.history;

import task.BaseTask;

import java.util.List;

public interface HistoryManager {
    void add(BaseTask task);

    void remove(int id);

    List<BaseTask> getHistory();
}
