package amrabed.android.release.evaluation.data.entities;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.joda.time.LocalDate;

import java.util.UUID;

import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.data.converters.ActiveDaysConverter;
import amrabed.android.release.evaluation.preferences.Preferences;

@Entity(tableName = "tasks")
public class Task {
    private static final byte ACTIVE_FRIDAY = (byte) 0x10;
    private static final byte ACTIVE_EVERYDAY = (byte) 0x7F;

    @PrimaryKey
    @NonNull
    public String id;

    /**
     * Original index of an item of the default list
     */
    @ColumnInfo(defaultValue = "-1")
    public int defaultIndex;

    @Ignore
    public int guideEntry;

    public int currentIndex;

    public String currentTitle;

    @NonNull
    @ColumnInfo(defaultValue = "0x7f")
    public boolean[] activeDays;

    @Ignore
    public Task() {
        this(-1);
    }

    @Ignore
    public Task(int defaultIndex) {
        this(UUID.randomUUID().toString(), defaultIndex);
    }

    public Task(@NonNull String id, int defaultIndex) {
        this.id = id;
        this.defaultIndex = defaultIndex;
        this.guideEntry = defaultIndex == -1 ? 0 : DEFAULT_LIST[defaultIndex];
        this.activeDays = new ActiveDaysConverter().setActiveDays(guideEntry == R.raw.friday ?
                ACTIVE_FRIDAY : ACTIVE_EVERYDAY);
    }

    public String getTitle(Context context) {
        return currentTitle != null ? currentTitle : getDefaultTitle(context);
    }

    private String getDefaultTitle(Context context) {
        return Preferences.getDefaultTaskTitles(context)[defaultIndex];
    }

    private boolean isActiveDay(int day) {
        return activeDays[day - 1];
    }

    public Task setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
        return this;
    }

    public Task setCurrentTitle(String currentTitle) {
        this.currentTitle = currentTitle;
        return this;
    }

    /**
     * Get shifted version of active days for Arabic list of days
     * (Mon, Tue, ..., Fri) -> (Sat, Sun, ..., Fri)
     *
     * @param shift number of days
     * @return shifted version of active days
     */
    public boolean[] getActiveDays(int shift) {
        if (shift == 0) return activeDays;
        boolean[] shifted = new boolean[7];
        for (int i = 0; i < activeDays.length; i++) {
            shifted[(i + shift) % 7] = activeDays[i];
        }
        return shifted;
    }

    public void setActiveDay(int day, boolean isActive) {
        activeDays[day - 1] = isActive;
    }

    public boolean isVisible(Context context, Day day) {
        return guideEntry == R.raw.fasting ? day.isFastingDay(context) :
                isActiveDay(new LocalDate(day.date).getDayOfWeek());
    }

    public static final int[] DEFAULT_LIST = {R.raw.wakeup, R.raw.brush, R.raw.night, R.raw.fasting,
            R.raw.sunna, R.raw.fajr, R.raw.fajr_azkar,
            R.raw.quran, R.raw.memorize,
            R.raw.morning, R.raw.duha,
            R.raw.sports, R.raw.friday, R.raw.work,
            R.raw.cong, R.raw.prayer_azkar, R.raw.rawateb,
            R.raw.cong, R.raw.prayer_azkar, R.raw.evening,
            R.raw.cong, R.raw.fajr_azkar, R.raw.rawateb,
            R.raw.isha, R.raw.prayer_azkar, R.raw.rawateb, R.raw.wetr,
            R.raw.diet, R.raw.manners, R.raw.honesty, R.raw.backbiting, R.raw.gaze,
            R.raw.wudu, R.raw.sleep};
}
