package amrabed.android.release.evaluation.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import amrabed.android.release.evaluation.R;

/**
 * Provides access to shared preferences globally
 */

public class Preferences {
    private Preferences() {
    }


    public static int getFastingDays(Context context) {
        return getSharedPreferences(context).getInt("fastingDays", 0);
    }

    public static long getLastDayOfFasting(Context context) {
        return getSharedPreferences(context).getLong("ldof", 0);
    }

    public static void setLastDayOfFasting(Context context, long date) {
        getSharedPreferences(context).edit().putLong("ldof", date).apply();
    }

    public static void removeLastDayOfFasting(Context context) {
        getSharedPreferences(context).edit().remove("ldof").apply();
    }

    public static String[] getDefaultTaskTitles(Context context) {
        return isMale(context) ? context.getResources().getStringArray(R.array.m_activities) :
                context.getResources().getStringArray(R.array.f_activities);
    }

    private static boolean isMale(Context context) {
        return getSharedPreferences(context).getBoolean("gender", true);
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static long getLastAddedDay(Context context) {
        return getSharedPreferences(context).getLong("last added day", 0);
    }

    public static void setLastAddedDay(Context context, long day) {
        getSharedPreferences(context).edit().putLong("last added day", day).apply();
    }
}