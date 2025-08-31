package managers.task;

import task.BaseTask;
import task.EpicTask;
import task.SubTask;
import task.Task;

import java.util.List;
import java.util.Optional;

public interface TaskManager {
    List<BaseTask> getHistory();

    Optional<BaseTask> getById(int id);

    void removeById(int id);

    void addTask(BaseTask task);

    void updateTask(BaseTask task);

    List<BaseTask> getPrioritizedTasks();

    List<SubTask> getAllSubtasksOfEpic(int id);

    List<EpicTask> getAllEpicsTasks();

    List<Task> getAllStandardTasks();

    List<SubTask> getAllSubTasks();

    void removeAllEpicsTasks();

    void removeAllStandardTasks();

    void removeAllSubTasks();
}
