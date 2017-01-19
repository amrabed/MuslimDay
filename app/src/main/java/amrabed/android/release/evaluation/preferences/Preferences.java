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
	public static String[] getActivities(Context context)
	{
		return isMale(context) ? context.getResources().getStringArray(R.array.m_activities) :
				context.getResources().getStringArray(R.array.f_activities);
	}

	public static boolean isMale(Context context)
	{
		return getSharedPreferences(context).getBoolean("gender", true);
	}

	public static SharedPreferences getSharedPreferences(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}
