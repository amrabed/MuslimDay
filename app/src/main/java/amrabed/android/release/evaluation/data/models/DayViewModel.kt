package amrabed.android.release.evaluation.data.models

import amrabed.android.release.evaluation.data.entities.Day
import amrabed.android.release.evaluation.data.repositories.DayRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class DayViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DayRepository = DayRepository(application)
    val dayList: LiveData<List<Day?>?>? by lazy {
        repository.loadAllDays()
    }
    private val selectedDay = MutableLiveData<Day?>()

    fun select(day: Day?) {
        selectedDay.value = day
    }

    val selected: LiveData<Day?>
        get() = selectedDay

    fun updateDay(day: Day?) {
        repository.updateDay(day)
    }
}