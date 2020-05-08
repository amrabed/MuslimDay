package amrabed.android.release.evaluation.utilities.preferences

import amrabed.android.release.evaluation.R
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

/**
 * Provides access to shared preferences globally
 */
object Preferences {
    fun getFastingDays(context: Context?): Int {
        return getSharedPreferences(context).getInt("fastingDays", 0)
    }

    fun getLastDayOfFasting(context: Context?): Long {
        return getSharedPreferences(context).getLong("ldof", 0)
    }

    fun setLastDayOfFasting(context: Context?, date: Long) {
        getSharedPreferences(context).edit().putLong("ldof", date).apply()
    }

    fun removeLastDayOfFasting(context: Context?) {
        getSharedPreferences(context).edit().remove("ldof").apply()
    }

    fun getDefaultTaskTitles(context: Context?): Array<String?> {
        return if (isMale(context)) context!!.resources.getStringArray(R.array.m_activities) else context!!.resources.getStringArray(R.array.f_activities)
    }

    private fun isMale(context: Context?): Boolean {
        return getSharedPreferences(context).getBoolean("gender", true)
    }

    private fun getSharedPreferences(context: Context?): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun getLastAddedDay(context: Context?): Long {
        return getSharedPreferences(context).getLong("last added day", 0)
    }

    fun setLastAddedDay(context: Context?, day: Long) {
        getSharedPreferences(context).edit().putLong("last added day", day).apply()
    }
}