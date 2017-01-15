package amrabed.android.release.evaluation.db.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import amrabed.android.release.evaluation.db.DatabaseEntry;

/**
 * Day table info
 */

public class DayTable
{
	private static final String TABLE_NAME = "daily";

	// Columns names
	private static final String DATE = "date";
	private static final String SELECTIONS = "selections";
	private static final String FLAGS = "flags";
	private static final String NUMBER = "number";
	private static final String RATIOS = "ratios";

	private static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
			"(" + DATE + " INTEGER PRIMARY KEY, " + SELECTIONS + " INTEGER, " +
			FLAGS + " INTEGER, " + NUMBER + " INTEGER, " + RATIOS + " INTEGER)";
	
	public static void create(SQLiteDatabase db)
	{
		db.execSQL(CREATE_STATEMENT);
	}

	public static void drop(SQLiteDatabase db)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	}

	public static long insert(SQLiteDatabase db, DatabaseEntry entry)
	{
		final ContentValues values = new ContentValues();
		values.put(DATE, entry.getDate());
		values.put(SELECTIONS, entry.getSelections());
		values.put(FLAGS, entry.getFlags());
		values.put(NUMBER, entry.getTotalNumber());
		values.put(RATIOS, entry.getRatios());
		return db.insert(TABLE_NAME, null, values);
	}

	public static int update(SQLiteDatabase db, long key, byte flags)
	{
		final ContentValues values = new ContentValues();
		values.put(FLAGS, flags);
		return db.update(TABLE_NAME, values, DATE + " = ?", new String[]{String.valueOf(key)});
	}

	public static int update(SQLiteDatabase db, long key, long value)
	{
		final ContentValues values = new ContentValues();
		final DatabaseEntry e = new DatabaseEntry(key, value);
		values.put(SELECTIONS, value);
		values.put(RATIOS, e.getRatios());
		return db.update(TABLE_NAME, values, DATE + " = ?", new String[]{String.valueOf(key)});
	}

	public static int update(SQLiteDatabase db, long key, short n)
	{
		final ContentValues values = new ContentValues();
		values.put(NUMBER, n);
		return db.update(TABLE_NAME, values, DATE + " = ?", new String[]{String.valueOf(key)});
	}

	// Deleting single entry
	public void delete(SQLiteDatabase db, long key)
	{
		db.delete(TABLE_NAME, DATE + " = ?", new String[]{String.valueOf(key)});
	}

	public static DatabaseEntry getEntry(SQLiteDatabase db, long id)
	{
		DatabaseEntry entry = null;

		final Cursor cursor = db.query(TABLE_NAME, null, DATE + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
		if (cursor.moveToFirst())
		{
			entry = new DatabaseEntry(id, Long.parseLong(cursor.getString(1)), Byte.parseByte(cursor.getString(2)), Short.parseShort(cursor.getString(3)), Short.parseShort(cursor.getString(4)));
		}
		cursor.close();

		return entry;
	}

	public static List<DatabaseEntry> getAllEntries(SQLiteDatabase db)
	{
		final List<DatabaseEntry> list = new ArrayList<DatabaseEntry>();

		Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_NAME, null);

		if (cursor.moveToFirst())
		{
			do
			{
				DatabaseEntry entry = new DatabaseEntry(
						Long.parseLong(cursor.getString(0)),
						Long.parseLong(cursor.getString(1)),
						Byte.parseByte(cursor.getString(2)),
						Short.parseShort(cursor.getString(3)),
						Short.parseShort(cursor.getString(4)));
				list.add(entry);
			} while (cursor.moveToNext());
		}

		cursor.close();
		return list;
	}
}
