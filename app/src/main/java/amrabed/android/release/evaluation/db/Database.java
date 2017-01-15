package amrabed.android.release.evaluation.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import amrabed.android.release.evaluation.core.ActionList;
import amrabed.android.release.evaluation.db.tables.ActivityTable;
import amrabed.android.release.evaluation.db.tables.DayTable;

public class Database extends SQLiteOpenHelper
{

	private static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "activities";

	public Database(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		DayTable.create(db);
		ActivityTable.create(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		DayTable.drop(db);
		ActivityTable.drop(db);
		onCreate(db);
	}

	public void saveList(ActionList actionList)
	{
		ActivityTable.saveList(getWritableDatabase(), actionList);
	}

	public ActionList loadList()
	{
		return ActivityTable.loadList(getReadableDatabase());
	}

	public long insert(DatabaseEntry entry)
	{
		return DayTable.insert(getWritableDatabase(), entry);
	}

	// Getting single entry
	public DatabaseEntry getEntry(long id)
	{
		return DayTable.getEntry(getReadableDatabase(), id);
	}

	// Getting All Entries
	public List<DatabaseEntry> getAllEntries()
	{
		return DayTable.getAllEntries(getReadableDatabase());
	}

	// Updating single entry
	public int update(long key, byte flags)
	{
		return DayTable.update(getWritableDatabase(), key, flags);
	}

	public int update(long key, long value)
	{
		return DayTable.update(getWritableDatabase(), key, value);
	}

	public int update(long key, short n)
	{
		return DayTable.update(getWritableDatabase(), key, n);
	}
}
