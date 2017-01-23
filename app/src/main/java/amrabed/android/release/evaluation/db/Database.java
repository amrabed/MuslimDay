package amrabed.android.release.evaluation.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import amrabed.android.release.evaluation.core.TaskList;
import amrabed.android.release.evaluation.core.DayEntry;
import amrabed.android.release.evaluation.core.DayList;
import amrabed.android.release.evaluation.db.tables.ActivityTable;
import amrabed.android.release.evaluation.db.tables.Day2Table;
import amrabed.android.release.evaluation.db.tables.DayTable;

public class Database extends SQLiteOpenHelper
{

	private static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "activities";

	public Database(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		DayTable.create(db);
		Day2Table.create(db);
		ActivityTable.create(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		if (oldVersion < newVersion)
		{
			// ToDo: Move data from old day table to new one
		}
		DayTable.drop(db);
		Day2Table.drop(db);
		ActivityTable.drop(db);
		onCreate(db);
	}

	public void saveList(TaskList taskList)
	{
		final SQLiteDatabase db = getWritableDatabase();
		ActivityTable.drop(db);
		ActivityTable.create(db);
		ActivityTable.saveList(db, taskList);
	}

	public TaskList loadActivityList()
	{
		return ActivityTable.loadList(getReadableDatabase());
	}

	public DayList loadDayList()
	{
		return Day2Table.loadList(getReadableDatabase());
	}

	public long insertDay(DayEntry entry)
	{
		return Day2Table.insert(getWritableDatabase(), entry);
	}

	public int updateDay(DayEntry entry)
	{
		return Day2Table.update(getWritableDatabase(), entry);
	}
}
