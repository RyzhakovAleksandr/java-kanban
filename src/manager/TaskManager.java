package manager;

import task.Task;

import java.util.List;

public interface TaskManager {

    <T extends Task> boolean add(T task);

    <T extends Task> List<T> getOneType(Class<T> task);

    List<Task> getAll();

    Task get(int id);

    <T extends Task> boolean update(int id, T task);

    boolean remove(int id);

}
