package amrabed.android.release.evaluation.data.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import amrabed.android.release.evaluation.data.entities.Task;
import amrabed.android.release.evaluation.data.repositories.TaskRepository;

public class TaskViewModel extends AndroidViewModel {
    private final TaskRepository repository;
    private LiveData<List<Task>> taskList;
    private MutableLiveData<Integer> selected = new MutableLiveData<>();

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
        taskList = repository.loadCurrentTaskList();
    }

    public LiveData<List<Task>> getTaskList() {
        return taskList;
    }

    public MutableLiveData<Integer> getSelected() {
        return selected;
    }

    public void updateTask(Task task) {
        repository.updateTask(task);
    }

    public void deleteTask(Task task) {
        repository.deleteTask(task);
    }

    public void addTask(Task task) {
        repository.addTask(task);
    }

    public void addTasks(List<Task> tasks) {
        repository.addTasks(tasks);
    }
}

