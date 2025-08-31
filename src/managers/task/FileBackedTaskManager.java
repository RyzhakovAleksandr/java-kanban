package managers.task;

import managers.Managers;
import managers.exceptions.ManagerLoadException;
import managers.exceptions.ManagerSaveException;
import managers.history.HistoryManager;
import task.BaseTask;
import task.EpicTask;
import task.SubTask;
import task.Task;
import utils.CSVTaskFormatter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);

        String[] lines;
        try {
            lines = Files.readString(file.toPath()).split(System.lineSeparator());

        } catch (IOException e) {
            throw new ManagerLoadException("Error while loading from file");
        }

        String history = lines[lines.length - 1];
        for (int lineIndex = 1, linesLength = lines.length - 2; lineIndex < linesLength; lineIndex++) {
            String line = lines[lineIndex];

            BaseTask baseTask = CSVTaskFormatter.fromCSVString(line);

            if (baseTask.getId() > taskManager.idCounter) {
                taskManager.idCounter = baseTask.getId();
            }
            switch (baseTask) {
                case EpicTask epicTask -> taskManager.tasks.put(epicTask.getId(), epicTask);
                case SubTask subTask -> {
                    if (!(taskManager.tasks.get(subTask.getEpicTaskId()) instanceof EpicTask epicTask)) {
                        throw new ManagerLoadException("no epic for subtask");
                    }
                    if (subTask.getStartTime() != null) {
                        taskManager.prioritizedTasks.add(subTask);
                    }
                    taskManager.tasks.put(subTask.getId(), subTask);
                    epicTask.addSubTask(subTask);
                }
                case Task task -> {
                    if (task.getStartTime() != null) {
                        taskManager.prioritizedTasks.add(task);
                    }
                    taskManager.tasks.put(task.getId(), task);
                }
            }

        }
        try {
            List<Integer> historyIdList = CSVTaskFormatter.parseIdList(history);
            for (Integer id : historyIdList) {
                taskManager.getById(id);
            }

        } catch (NumberFormatException ignored) {
        }
        return taskManager;
    }

    @Override
    public Optional<BaseTask> getById(int id) {
        Optional<BaseTask> task = super.getById(id);
        save();
        return task;
    }

    @Override
    public void removeById(int id) {
        super.removeById(id);
        save();
    }

    @Override
    public void addTask(BaseTask task) {
        super.addTask(task);
        save();
    }

    @Override
    public void updateTask(BaseTask task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeAllEpicsTasks() {
        super.removeAllEpicsTasks();
        save();
    }

    @Override
    public void removeAllStandardTasks() {
        super.removeAllStandardTasks();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(CSVTaskFormatter.getHeader());
            writer.newLine();

            for (BaseTask task : tasks.values()) {
                writer.write(CSVTaskFormatter.toCSVString(task));
                writer.newLine();
            }

            writer.newLine();

            writer.write(CSVTaskFormatter.tasksToIdString(historyManager.getHistory()));
            writer.newLine();
        } catch (IOException ignored) {
            throw new ManagerSaveException("Cant save to file");
        }
    }
}
