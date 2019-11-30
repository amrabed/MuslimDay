package amrabed.android.release.evaluation.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

import amrabed.android.release.evaluation.data.entities.Day;
import amrabed.android.release.evaluation.data.entities.Task;

public class Repository {
    private AppDatabase db;

    public Repository(Context context) {
        db = AppDatabase.get(context);
    }

    public LiveData<List<Task>> loadCurrentTaskList() {
        return db.taskTable().loadCurrentTasks();
    }

    public LiveData<List<Day>> loadAllDays() {
        return db.dayTable().getAll();
    }

    public void updateDay(Day day) {
        AppDatabase.writeExecutor.execute(() -> db.dayTable().updateDay(day));
    }

    public void addTasks(List<Task> tasks) {
        AppDatabase.writeExecutor.execute(() -> db.taskTable().insertTasks((Task[]) tasks.toArray()));
    }

    public void updateTask(Task task) {
        AppDatabase.writeExecutor.execute(() -> db.taskTable().updateTask(task));
    }

    public void deleteTask(Task task) {
        AppDatabase.writeExecutor.execute(() -> db.taskTable().deleteTask(task));
    }

    public void addTask(Task task) {
        AppDatabase.writeExecutor.execute(() -> db.taskTable().insertTasks(task));
    }
}