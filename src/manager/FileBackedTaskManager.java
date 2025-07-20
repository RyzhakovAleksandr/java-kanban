package manager;

import exceptions.LoadException;
import exceptions.SaveException;
import task.Task;
import utils.CSVFormatter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;

        try {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
        } catch (Exception ex) {
            throw new LoadException("No correct opened file");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        String[] lines;

        try {
            lines = Files.readString(file.toPath()).split(System.lineSeparator());
        } catch (IOException e) {
            throw new LoadException("No correct opened file");
        }

        String history = lines[lines.length - 1];

        for (int i = 1; i < lines.length - 2; i++) {
            String str = lines[i];

            Task task = CSVFormatter.fromCSVToString(str, taskManager);

            if (task.getTaskID() >= taskManager.idForTasks) {
                taskManager.idForTasks = task.getTaskID();
            }
            taskManager.add(task);
        }

        for (Integer id : CSVFormatter.idStringToListHistory(history)) {
            taskManager.get(id);
        }

        return taskManager;
    }

    @Override
    public boolean add(Task task) {
        boolean result = super.add(task);
        save();
        return result;
    }

    @Override
    public boolean add(int id, Task task) {
        boolean result = super.add(id, task);
        save();
        return result;
    }

    @Override
    public <T extends Task> List<T> getOneType(Class<T> type) {
        List<T> list = super.getOneType(type);
        save();
        return list;
    }

    @Override
    public List<Task> getAll() {
        List<Task> tasks = super.getAll();
        //save();
        return tasks;
    }

    @Override
    public Task get(int id) {
        Task task = super.get(id);
        save();
        return task;
    }

    @Override
    public <T extends Task> boolean update(int id, T newTask) {
        boolean result = super.update(id, newTask);
        save();
        return result;
    }

    @Override
    public boolean remove(int id) {
        boolean result = super.remove(id);
        save();
        return result;
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(CSVFormatter.getHead());
            writer.newLine();

            for (Task task : tasksList.values()) {
                writer.write(CSVFormatter.toCSVString(task));
                writer.newLine();
            }

            writer.newLine();
            writer.write(CSVFormatter.historyToCSVString(historyManager.getTasks()));
        } catch (IOException e) {
            throw new SaveException("No correct saved file");
        }
    }
}
