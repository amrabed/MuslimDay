package amrabed.android.release.evaluation.db.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import amrabed.android.release.evaluation.core.Task;
import amrabed.android.release.evaluation.core.TaskList;

/**
 * Table to hold list of activities
 *
 * @author AmrAbed
 */

public class ActivityTable
{
	private static final String TABLE_NAME = "list";

	// Columns names
	private static final String ID = "_ID";
	private static final String UUID = "uniqueId";
	private static final String CURRENT_INDEX = "currentIndex";
	private static final String DEFAULT_INDEX = "defaultIndex";
	private static final String CURRENT_TITLE = "currentTitle";
	private static final String DEFAULT_TITLE = "defaultTitle";
	private static final String ACTIVE_DAYS = "activeDays";
	private static final String GUIDE_ENTRY = "guideEntry";


	private static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
			"(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + UUID + " TEXT UNIQUE, " +
			CURRENT_TITLE + " TEXT, " + CURRENT_INDEX + " INTEGER, " +
			DEFAULT_TITLE + " INTEGER, " + DEFAULT_INDEX + " INTEGER, " +
			ACTIVE_DAYS + " INTEGER NOT NULL, " + GUIDE_ENTRY + " INTEGER)";

	public static void create(SQLiteDatabase db)
	{
		db.execSQL(CREATE_STATEMENT);
	}

	public static void drop(SQLiteDatabase db)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	}

	public static void saveList(SQLiteDatabase db, TaskList list)
	{
		for (Task task : list)
		{
			insert(db, task);
		}
	}

	private static void insert(SQLiteDatabase db, Task task)
	{
		final ContentValues values = new ContentValues();
		values.put(UUID, task.getId());
		values.put(CURRENT_INDEX, task.getCurrentIndex());
		values.put(DEFAULT_INDEX, task.getDefaultIndex());
		values.put(CURRENT_TITLE, task.getCurrentTitle());
		values.put(ACTIVE_DAYS, task.getActiveDaysByte());
		values.put(GUIDE_ENTRY, task.getGuideEntry());
		db.insert(TABLE_NAME, null, values);
	}

	public static TaskList loadList(SQLiteDatabase db)
	{
		final TaskList list = new TaskList();
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
		if (cursor.moveToFirst())
		{
			do
			{
				final String uuid = cursor.getString(cursor.getColumnIndexOrThrow(ID));
				final int defaultIndex = cursor.getInt(cursor.getColumnIndexOrThrow(DEFAULT_INDEX));
				final int guideEntry = cursor.getInt(cursor.getColumnIndexOrThrow(GUIDE_ENTRY));
				final String currentTitle = cursor
						.getString(cursor.getColumnIndexOrThrow(CURRENT_TITLE));
				final int currentIndex = cursor.getInt(cursor.getColumnIndexOrThrow(CURRENT_INDEX));
				final Byte activeDays = (byte) cursor
						.getInt(cursor.getColumnIndexOrThrow(ACTIVE_DAYS));
				list.add(new Task(uuid, defaultIndex, guideEntry)
						.setCurrentIndex(currentIndex)
						.setCurrentTitle(currentTitle)
						.setActiveDays(activeDays));
			}
			while (cursor.moveToNext());
		}
		cursor.close();
		return list;
	}


	public static String getName()
	{
		return TABLE_NAME;
	}
}
