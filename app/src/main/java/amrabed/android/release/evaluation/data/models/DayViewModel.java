package amrabed.android.release.evaluation.data.models;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import amrabed.android.release.evaluation.data.Repository;
import amrabed.android.release.evaluation.data.entities.Day;

public class DayViewModel extends AndroidViewModel {
    private final Repository repository;
    private final LiveData<List<Day>> dayList;
    private MutableLiveData<Day> selectedDay = new MutableLiveData<>();

    public DayViewModel(Application application) {
        super(application);
        repository = new Repository(application);
        dayList = repository.loadAllDays();
    }

    public LiveData<List<Day>> getDayList() {
        return dayList;
    }

    public void select(Day day) {
        selectedDay.setValue(day);
    }

    public LiveData<Day> getSelected() {
        return selectedDay;
    }

    public void updateDay(Day day) {
        repository.updateDay(day);
    }
}