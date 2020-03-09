package amrabed.android.release.evaluation.data.repositories

import amrabed.android.release.evaluation.data.AppDatabase
import amrabed.android.release.evaluation.data.entities.Day
import android.content.Context
import androidx.lifecycle.LiveData

class DayRepository(context: Context) {
    private val db: AppDatabase? = AppDatabase[context]
    fun loadAllDays(): LiveData<List<Day?>?>? {
        return db!!.dayTable().all
    }

    fun updateDay(day: Day?) {
        AppDatabase.writeExecutor.execute{ db!!.dayTable().updateDay(day) }
    }
}