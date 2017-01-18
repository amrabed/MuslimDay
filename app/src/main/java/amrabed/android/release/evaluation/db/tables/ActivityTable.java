package amrabed.android.release.evaluation.db.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import amrabed.android.release.evaluation.core.Activity;
import amrabed.android.release.evaluation.core.ActivityList;

/**
 * Table to hold list of activities
 *
 * @author AmrAbed
 */

public class ActivityTable
{
	private static final String TABLE_NAME = "list";

	// Columns names
	private static final String ID = "id";
	private static final String CURRENT_INDEX = "currentIndex";
	private static final String DEFAULT_INDEX = "defaultIndex";
	private static final String CURRENT_TITLE = "currentTitle";
	private static final String DEFAULT_TITLE = "defaultTitle";
	private static final String ACTIVE_DAYS = "activeDays";
	private static final String GUIDE_ENTRY = "guideEntry";


	private static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
			"(" + ID + " INTEGER PRIMARY KEY, " +
			CURRENT_TITLE + " TEXT NOT NULL, " + CURRENT_INDEX + " INTEGER, " +
			DEFAULT_TITLE + " TEXT, " + DEFAULT_INDEX + " INTEGER, " +
			ACTIVE_DAYS + " INTEGER NOT NULL DEFAULT 0, " + GUIDE_ENTRY + " INTEGER)";

	public static void create(SQLiteDatabase db)
	{
		db.execSQL(CREATE_STATEMENT);
	}

	public static void drop(SQLiteDatabase db)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	}

	public static void saveList(SQLiteDatabase db, ActivityList list)
	{
		for (Activity activity : list)
		{
			long id = insert(db, activity);
			Log.d(TABLE_NAME, "Added activity: " + activity.toString() + " - id = " + id);
		}
	}

	private static long insert(SQLiteDatabase db, Activity activity)
	{
		final ContentValues values = new ContentValues();
		values.put(ID, activity.getUniqueId());
		values.put(CURRENT_INDEX, activity.getCurrentIndex());
		values.put(DEFAULT_INDEX, activity.getDefaultIndex());
		values.put(CURRENT_TITLE, activity.getCurrentTitle());
		values.put(DEFAULT_TITLE, activity.getDefaultTitle());
		values.put(ACTIVE_DAYS, activity.getActiveDays());
		values.put(GUIDE_ENTRY, activity.getGuideEntry());
		return db.insert(TABLE_NAME, null, values);
	}

	public static ActivityList loadList(SQLiteDatabase db)
	{
		final ActivityList list = new ActivityList();
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
		Log.d(TABLE_NAME, "Cursor size: " + cursor.getCount());
		if (cursor.moveToFirst())
		{
			do
			{
				final long id = cursor.getLong(cursor.getColumnIndexOrThrow(ID));
				final int currentIndex = cursor.getInt(cursor.getColumnIndexOrThrow(CURRENT_INDEX));
				final int defaultIndex = cursor.getInt(cursor.getColumnIndexOrThrow(DEFAULT_INDEX));
				final String currentTitle = cursor.getString(cursor.getColumnIndexOrThrow(CURRENT_TITLE));
				final String defaultTitle = cursor.getString(cursor.getColumnIndexOrThrow(DEFAULT_TITLE));
				final Byte activeDays = (byte) cursor.getInt(cursor.getColumnIndexOrThrow(ACTIVE_DAYS));
				final int guideEntry = cursor.getInt(cursor.getColumnIndexOrThrow(GUIDE_ENTRY));
				list.add(new Activity(id, defaultIndex, defaultTitle, guideEntry)
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
