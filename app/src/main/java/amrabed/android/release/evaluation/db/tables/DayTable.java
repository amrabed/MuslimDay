package amrabed.android.release.evaluation.db.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;

import amrabed.android.release.evaluation.core.DayEntry;
import amrabed.android.release.evaluation.core.DayList;

/**
 * Day table info
 */

public class DayTable
{
	private static final String TABLE_NAME = "days";

	// Columns names
	private static final String DATE = "date";
	private static final String SELECTIONS = "selections";

	private static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
			"(" + DATE + " INTEGER PRIMARY KEY, " + SELECTIONS + " BLOB)";

	public static String getName()
	{
		return TABLE_NAME;
	}

	public static void create(SQLiteDatabase db)
	{
		db.execSQL(CREATE_STATEMENT);
	}

	public static void drop(SQLiteDatabase db)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	}

	public static void delete(SQLiteDatabase db, long key)
	{
		db.delete(TABLE_NAME, DATE + " = ?", new String[]{String.valueOf(key)});
	}

	public static DayEntry getEntry(SQLiteDatabase db, long date)
	{
		DayEntry entry = null;

		final Cursor cursor = db.query(TABLE_NAME, null, DATE + "=?", new String[]{String.valueOf(date)}, null, null, null, null);
		if (cursor.moveToFirst())
		{
			try
			{
				entry = new DayEntry(date, cursor.getBlob(cursor.getColumnIndexOrThrow(SELECTIONS)));
			}
			catch (IOException | ClassNotFoundException e)
			{
				Log.e(TABLE_NAME, e.toString());
			}
		}
		cursor.close();

		return entry;
	}

	public static void saveList(SQLiteDatabase db, DayList list)
	{
		for (DayEntry entry : list)
		{
			long id = insert(db, entry);
			Log.d(TABLE_NAME, "Added activity: " + entry.toString() + " - id = " + id);
		}
	}

	public static DayList loadList(SQLiteDatabase db)
	{
		final DayList list = new DayList();

		final Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_NAME, null);

		try
		{
			if (cursor.moveToFirst())
			{
				do
				{
					list.add(new DayEntry(cursor.getLong(cursor.getColumnIndexOrThrow(DATE)),
							cursor.getBlob(cursor.getColumnIndexOrThrow(SELECTIONS))));
				} while (cursor.moveToNext());
			}

		}
		catch (IOException | ClassNotFoundException e)
		{
			Log.e(TABLE_NAME, e.toString());
		}
		finally
		{
			cursor.close();
		}
		return list;
	}

	public static long insert(SQLiteDatabase db, DayEntry entry)
	{
		final ContentValues values = new ContentValues();
		try
		{
			values.put(DATE, entry.getDate());
			values.put(SELECTIONS, entry.getSelections());
			return db.insert(TABLE_NAME, null, values);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return -1;
	}

	public static int update(SQLiteDatabase db, DayEntry entry)
	{
		final ContentValues values = new ContentValues();
		try
		{
			values.put(SELECTIONS, entry.getSelections());
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		return db.update(TABLE_NAME, values, DATE + " = ?", new String[]{String.valueOf(entry.getDate())});
	}
}
