package amrabed.android.release.evaluation.core;

import java.util.ArrayList;

import amrabed.android.release.evaluation.ApplicationEvaluation;

/**
 * DayList to be read from the database on app start
 */

public class DayList extends ArrayList<DayEntry>
{
	public static DayList load()
	{
		return ApplicationEvaluation.getDatabase().loadDayList();
	}
}
