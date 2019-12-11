package amrabed.android.release.evaluation.data.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

import amrabed.android.release.evaluation.data.AppDatabase;
import amrabed.android.release.evaluation.data.entities.Day;

public class DayRepository {
    private AppDatabase db;

    public DayRepository(Context context) {
        db = AppDatabase.get(context);
    }

    public LiveData<List<Day>> loadAllDays() {
        return db.dayTable().getAll();
    }

    public void updateDay(Day day) {
        AppDatabase.writeExecutor.execute(() -> db.dayTable().updateDay(day));
    }
}