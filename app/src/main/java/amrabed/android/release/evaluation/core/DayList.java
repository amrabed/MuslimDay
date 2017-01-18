package amrabed.android.release.evaluation.core;

import java.util.ArrayList;

import amrabed.android.release.evaluation.db.Database;

/**
 * DayList to be read from the database on app start
 *
 * @author AmrAbed
 */

public class DayList extends ArrayList<DayEntry>
{
	public static DayList readList(Database db)
	{
		return db.loadDayList();
	}
}
