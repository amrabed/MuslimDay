package amrabed.android.release.evaluation.data.entities;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.chrono.IslamicChronology;

import java.util.HashMap;

import amrabed.android.release.evaluation.preferences.Preferences;

@Entity(tableName = "days")
public class Day {

    @PrimaryKey
    public long date;

    public HashMap<String, Byte> selections = new HashMap<>();

    @Ignore
    public Day() {
        this(new DateTime().withTimeAtStartOfDay());
    }

    @Ignore
    public Day(DateTime date) {
        this.date = date.getMillis();
    }

    public Day(long date, @NonNull HashMap<String, Byte> selections) {
        this.date = date;
        this.selections = selections;
    }

    public byte getSelection(@NonNull String id) {
        final Byte value = selections.get(id);
        return value == null ? 0 : value;
    }

    public float[] getRatios() {
        final float[] ratios = {0, 0, 0, 0};
        for (Byte selection : selections.values()) {
            ratios[selection]++;
        }
        return ratios;
    }

    public void setSelectionAt(String id, byte selection) {
        selections.put(id, selection);
    }

    boolean isFastingDay(Context context) {
        final int fasting = Preferences.getFastingDays(context);
        final boolean dayAfterDay = ((fasting & 0x08) != 0);
        if (dayAfterDay) {
            final long lastDayOfFasting = Preferences.getLastDayOfFasting(context);
            final DateTime start = new DateTime(lastDayOfFasting);
            final DateTime end = new DateTime(date);
            final boolean isMoreThanOne = Days.daysBetween(start, end).isGreaterThan(Days.ONE);
            if (isMoreThanOne) {
                Preferences.setLastDayOfFasting(context, date);
                return true;
            }
        } else {
            Preferences.removeLastDayOfFasting(context);
        }

        final DateTime dateHijri = new DateTime(date).withChronology(IslamicChronology.getInstance());
        int month = dateHijri.monthOfYear().get();
        int dayOfMonth = dateHijri.dayOfMonth().get();
        if ((month == 1) && ((dayOfMonth == 9) || (dayOfMonth == 10))) // Aashoraa
        {
            return true;
        }
        if ((month == 12) && (dayOfMonth == 9)) // Arafaat
        {
            return true;
        }

        int dayOfWeek = dateHijri.dayOfWeek().get();
        boolean isFastingMonday = ((fasting & 0x01) != 0);
        boolean isFastingThursday = ((fasting & 0x02) != 0);
        boolean isFastingWhiteDays = ((fasting & 0x04) != 0);
        return (isFastingThursday && dayOfWeek == DateTimeConstants.THURSDAY) ||
                (isFastingMonday && dayOfWeek == DateTimeConstants.MONDAY) ||
                (isFastingWhiteDays && ((dayOfMonth == 13) || (dayOfMonth == 14) || (dayOfMonth == 15)));
    }
}