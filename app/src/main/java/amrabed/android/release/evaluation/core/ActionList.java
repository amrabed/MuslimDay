package amrabed.android.release.evaluation.core;

import android.content.Context;

import org.joda.time.DateTime;

import java.util.ArrayList;

import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.app.ApplicationEvaluation;

/**
 * Activity list
 */

public class ActionList extends ArrayList<Activity>
{

	public static ActionList getDefault(Context context, boolean isMale)
	{
		final ActionList list = new ActionList();

		final int array = isMale ? R.array.m_activities : R.array.f_activities;
		final String[] activities = context.getResources().getStringArray(array);
		for (int i = 0; i < entries.length; i++)
		{
			list.add(new Activity(i, activities[i], entries[i]));
		}
		return list;
	}

	public static ActionList getCurrent()
	{
		return ApplicationEvaluation.getDatabase().loadList();
	}

	public static ActionList getDayList(DateTime date, long selections)
	{
		final ActionList list = getCurrent();
		final int day = date.getDayOfWeek();
		for (Activity activity : list)
		{
//            activity.
			if (!activity.isActiveDay(day))
			{
				list.remove(activity);
			}
		}
		return list;
	}

	private final static int entries[] = {R.raw.wakeup, R.raw.brush, R.raw.night, R.raw.fasting,
			R.raw.sunna, R.raw.fajr, R.raw.quran, R.raw.memorize, R.raw.morning, R.raw.duha,
			R.raw.sports, R.raw.friday, R.raw.work, R.raw.cong, R.raw.prayer, R.raw.rawateb,
			R.raw.evening, R.raw.isha, R.raw.wetr, R.raw.diet, R.raw.manners, R.raw.honesty,
			R.raw.backbiting, R.raw.gaze, R.raw.wudu, R.raw.sleep};
}
