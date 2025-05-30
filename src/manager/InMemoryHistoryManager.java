package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> historyUsedTask = new ArrayList<>(10);

    @Override
    public void addHistory(Task task) {
        if (historyUsedTask.size() >= 10) {
            List<Task> timeList = new ArrayList<>(historyUsedTask.subList(1, historyUsedTask.size()));
            timeList.addLast(task);
            historyUsedTask.clear();
            historyUsedTask.addAll(timeList);
        } else {
            historyUsedTask.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyUsedTask;
    }

}
