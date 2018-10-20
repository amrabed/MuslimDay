package amrabed.android.release.evaluation.core;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.chrono.IslamicChronology;

import java.util.ArrayList;
import java.util.Iterator;

import amrabed.android.release.evaluation.ApplicationEvaluation;
import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.preferences.Preferences;

/**
 * Task list
 */

public class TaskList extends ArrayList<Task>
{
	public static TaskList getDefault(Context context)
	{
		final TaskList list = new TaskList();
		for (int i = 0; i < entries.length; i++)
		{
			list.add(new Task(i, entries[i]).setActiveDays(context));
		}
		return list;
	}

	public static TaskList getCurrent(Context context)
	{
		TaskList list = ApplicationEvaluation.getDatabase().loadActivityList();
		if (list.isEmpty())
		{
			list = getDefault(context);
			// Save list for future use
			// ToDo: Not working on first run
			ApplicationEvaluation.getDatabase().saveList(list);
		}
		else
		{
			// Handle any updated preferences
			for (Task task : list)
			{
				task.setActiveDays(context);
			}
		}
		return list;
	}

	public static TaskList getDayList(Context context, long date)
	{
		return getDayList(context, new LocalDate(date));
	}

	private static TaskList getDayList(Context context, LocalDate date)
	{
		final TaskList list = getCurrent(context);
		final int day = date.getDayOfWeek();
		final Iterator<Task> iterator = list.iterator();

		while (iterator.hasNext())
		{
			final Task task = iterator.next();

			if (!task.isActiveDay(day) || (task.getGuideEntry() == R.raw.fasting &&
					!isFastingDay(context, date.toDateTimeAtStartOfDay().getMillis())))
			{
				// Remove fasting entry if not a fating day
				iterator.remove();
			}
		}
		return list;
	}

	private static boolean isFastingDay(Context context, long date)
	{
		final int fasting = Preferences.getFastingDays(context);
		final boolean dayAfterDay = ((fasting & 0x08) != 0);
		if (dayAfterDay)
		{
			final long lastDayOfFasting = Preferences.getLastDayOfFasting(context);
			final DateTime start = new DateTime(lastDayOfFasting);
			final DateTime end = new DateTime(date);
			final boolean isMoreThanOne = Days.daysBetween(start, end).isGreaterThan(Days.ONE);
			if (isMoreThanOne)
			{
				Preferences.setLastDayOfFasting(context, date);
				return true;
			}
		}
		else
		{
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
