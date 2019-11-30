package amrabed.android.release.evaluation.data.tables;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import amrabed.android.release.evaluation.data.entities.Day;

@Dao
public interface DayTable {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Day... days);

    @Update
    void updateDay(Day day);

    @Query("SELECT * FROM days WHERE date = :date")
    LiveData<Day> get(long date);

    @Query("SELECT * FROM days ORDER BY date ASC")
    LiveData<List<Day>> getAll();
}
