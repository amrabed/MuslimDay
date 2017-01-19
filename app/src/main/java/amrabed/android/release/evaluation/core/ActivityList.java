package amrabed.android.release.evaluation.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.RawRes;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Iterator;

import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.app.ApplicationEvaluation;

/**
 * Activity list
 */

public class ActivityList extends ArrayList<Activity>
{
	public static ActivityList getDefault(Context context)
	{
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

		final ActivityList list = new ActivityList();
		for (int i = 0; i < entries.length; i++)
		{
			final String prefKey = getPreferenceKey(entries[i]);
			final Activity activity = new Activity(i, entries[i]);
			if (prefKey != null)
			{
				byte activeDays = (byte) preferences.getInt(prefKey, 0);
				activity.setActiveDays(activeDays);
			}
			if (entries[i] == R.raw.friday)
			{
				activity.setActiveDays(Activity.ACTIVE_FRIDAY);
			}
			list.add(activity);
		}
		return list;
	}

//	public static ActivityList getCurrent()
//	{
//		return ApplicationEvaluation.getDatabase().loadList();
//	}

	public static ActivityList getCurrent(Context context)
	{
		final ActivityList list = ApplicationEvaluation.getDatabase().loadList();
		return list.isEmpty() ? getDefault(context) : list;
	}

	public static ActivityList getDayList(Context context, long date)
	{
		return getDayList(context, new LocalDate(date));
	}

	public static ActivityList getDayList(Context context, LocalDate date)
	{
		final ActivityList list = getCurrent(context);
		final int day = date.getDayOfWeek();
		final Iterator<Activity> iterator = list.iterator();

		while (iterator.hasNext())
		{
			final Activity activity = iterator.next();

			if (!activity.isActiveDay(day))
			{
				iterator.remove();
			}
		}
		return list;
	}

	/**
	 * Get preference key based on Raw resource ID
	 *
	 * @param entry raw resource ID
	 * @return preference key name
	 * @see amrabed.android.release.evaluation.PreferenceSection
	 */
	private static String getPreferenceKey(@RawRes int entry)
	{
		switch (entry)
		{
			case R.raw.memorize:
				return "memorizeDays";
			case R.raw.quran:
				return "reciteDays";
//			case R.raw.fasting:
//				return "fastingDays";
			case R.raw.diet:
				return "dietDays";
		}
		return null;
	}

	private final static int entries[] = {R.raw.wakeup, R.raw.brush, R.raw.night, R.raw.fasting,
			R.raw.sunna, R.raw.fajr, R.raw.prayer,
			R.raw.quran, R.raw.memorize,
			R.raw.morning, R.raw.duha,
			R.raw.sports, R.raw.friday, R.raw.work,
			R.raw.cong, R.raw.prayer, R.raw.rawateb,
			R.raw.cong, R.raw.prayer, R.raw.evening,
			R.raw.cong, R.raw.prayer, R.raw.rawateb,
			R.raw.isha, R.raw.prayer, R.raw.rawateb, R.raw.wetr,
			R.raw.diet, R.raw.manners, R.raw.honesty, R.raw.backbiting, R.raw.gaze,
			R.raw.wudu, R.raw.sleep};
}
