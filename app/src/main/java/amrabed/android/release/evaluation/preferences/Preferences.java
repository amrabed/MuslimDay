package amrabed.android.release.evaluation.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import amrabed.android.release.evaluation.R;

/**
 * Provides access to shared preferences globally
 *
 * @author AmrAbed
 */

public class Preferences
{
	public static boolean isSyncEnabled(Context context)
	{
		return getSharedPreferences(context).getBoolean("sync", false);
	}

	public static void setSyncEnabled(Context context, boolean isEnabled)
	{
		getSharedPreferences(context).edit().putBoolean("sync", isEnabled).apply();
	}

	public static String getLanguage(Context context)
	{
		return getSharedPreferences(context).getString("language", "");
	}
	public static byte getActiveDays(Context context, String key)
	{
		return (byte) getSharedPreferences(context).getInt(key, 0);
	}

	public static int getFastingDays(Context context)
	{
		return getSharedPreferences(context).getInt("fastingDays", 0);
	}

	public static long getLastDayOfFasting(Context context)
	{
		return getSharedPreferences(context).getLong("ldof", 0);
	}

	public static void setLastDayOfFasting(Context context, long date)
	{
		getSharedPreferences(context).edit().putLong("ldof", date).apply();
	}

	public static void removeLastDayOfFasting(Context context)
	{
		getSharedPreferences(context).edit().remove("ldof").apply();
	}

	public static String[] getActivities(Context context)
	{
		return isMale(context) ? context.getResources().getStringArray(R.array.m_activities) :
				context.getResources().getStringArray(R.array.f_activities);
	}

	private static boolean isMale(Context context)
	{
		return getSharedPreferences(context).getBoolean("gender", true);
	}

	private static SharedPreferences getSharedPreferences(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}
