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
		values.put(UUID, activity.getId());
		values.put(CURRENT_INDEX, activity.getCurrentIndex());
		values.put(DEFAULT_INDEX, activity.getDefaultIndex());
		values.put(CURRENT_TITLE, activity.getCurrentTitle());
		values.put(ACTIVE_DAYS, activity.getActiveDaysByte());
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
				final String uuid = cursor.getString(cursor.getColumnIndexOrThrow(ID));
				final int defaultIndex = cursor.getInt(cursor.getColumnIndexOrThrow(DEFAULT_INDEX));
				final int guideEntry = cursor.getInt(cursor.getColumnIndexOrThrow(GUIDE_ENTRY));
				final String currentTitle = cursor.getString(cursor.getColumnIndexOrThrow(CURRENT_TITLE));
				final int currentIndex = cursor.getInt(cursor.getColumnIndexOrThrow(CURRENT_INDEX));
				final Byte activeDays = (byte) cursor.getInt(cursor.getColumnIndexOrThrow(ACTIVE_DAYS));
				list.add(new Activity(uuid, defaultIndex, guideEntry)
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
