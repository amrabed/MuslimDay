package amrabed.android.release.evaluation.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import amrabed.android.release.evaluation.core.DayEntry;
import amrabed.android.release.evaluation.core.TaskList;
import amrabed.android.release.evaluation.db.tables.DayTable;
import amrabed.android.release.evaluation.db.tables.TaskTable;

public class Database extends SQLiteOpenHelper
{

	private static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "activities";
	private static String path;

	public Database(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		path = context.getDatabasePath(Database.DATABASE_NAME).getAbsolutePath();
	}

	public static String getPath() {
		return path;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		DayTable.create(db);
		TaskTable.create(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		DayTable.drop(db);
		TaskTable.drop(db);
		onCreate(db);
	}

	public void saveList(TaskList taskList)
	{
		final SQLiteDatabase db = getWritableDatabase();
		TaskTable.drop(db);
		TaskTable.create(db);
		TaskTable.saveList(db, taskList);
	}

	public TaskList loadActivityList()
	{
		return TaskTable.loadList(getReadableDatabase());
	}

	public List<DayEntry> loadDayList()
	{
		return DayTable.loadList(getReadableDatabase());
	}

	void insertDay(DayEntry entry)
	{
		DayTable.insert(getWritableDatabase(), entry);
	}

	public void updateDay(DayEntry entry)
	{
		DayTable.update(getWritableDatabase(), entry);
	}
}
