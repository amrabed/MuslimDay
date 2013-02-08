package amrabed.android.release.evaluation;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper
{

	private static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "activities";

	private static final String TABLE_NAME = "daily";

	// Contacts Table Columns names
	private static final String DATE = "date";
	private static final String SELECTIONS = "selections";
	private static final String FLAGS = "flags";
	private static final String NUMBER = "number";
	private static final String RATIOS = "ratios";

	public Database(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + DATE + " INTEGER PRIMARY KEY, " + SELECTIONS + " INTEGER, " + FLAGS + " INTEGER, " + NUMBER + " INTEGER, " + RATIOS + " INTEGER)");
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	// Adding new entry
	long insert(long key, long value)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		DatabaseEntry e = new DatabaseEntry(key,value);
		values.put(DATE, key);
		values.put(SELECTIONS, value);
		values.put(FLAGS, 0);
		values.put(NUMBER, 0);
		values.put(RATIOS, e.ratios);

		long ret = db.insert(TABLE_NAME, null, values);
		db.close();
		return ret;
	}

	long insert(DatabaseEntry e)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(DATE, e.date);
		values.put(SELECTIONS, e.selections);
		values.put(FLAGS, e.flags);
		values.put(NUMBER, e.totalNumber);
		values.put(RATIOS, e.ratios);

		long ret = db.insert(TABLE_NAME, null, values);
		db.close();
		return ret;
	}

	// Getting single entry
	DatabaseEntry getEntry(long id)
	{
		DatabaseEntry e = null;
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_NAME, new String[] { DATE, SELECTIONS, FLAGS, NUMBER, RATIOS }, DATE + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor.moveToFirst())
		{
			e = new DatabaseEntry(id, Long.parseLong(cursor.getString(1)), Byte.parseByte(cursor.getString(2)), Short.parseShort(cursor.getString(3)), Short.parseShort(cursor.getString(4)));
		}

		return e;
	}

	// Getting All Entries
	public List<DatabaseEntry> getAllEntries()
	{
		List<DatabaseEntry> list = new ArrayList<DatabaseEntry>();
		String selectQuery = "SELECT  * FROM " + TABLE_NAME;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst())
		{
			do
			{
				DatabaseEntry entry = new DatabaseEntry(Long.parseLong(cursor.getString(0)), Long.parseLong(cursor.getString(1)), Byte.parseByte(cursor.getString(2)), Short.parseShort(cursor.getString(3)), Short
						.parseShort(cursor.getString(4)));
				list.add(entry);
			} while (cursor.moveToNext());
		}

		return list;
	}

	// Updating single entry
	public int update(long key, byte flags)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(FLAGS, flags);

		return db.update(TABLE_NAME, values, DATE + " = ?", new String[] { String.valueOf(key) });
	}

	public int update(long key, long value)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		DatabaseEntry e = new DatabaseEntry(key,value);
		values.put(SELECTIONS, value);
		values.put(RATIOS, e.ratios);

		return db.update(TABLE_NAME, values, DATE + " = ?", new String[] { String.valueOf(key) });
	}

	public int update(long key, short n)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(NUMBER, n);

		return db.update(TABLE_NAME, values, DATE + " = ?", new String[] { String.valueOf(key) });
	}

	// Deleting single entry
	public void delete(long key)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, DATE + " = ?", new String[] { String.valueOf(key) });
		db.close();
	}

	// Getting entries Count
	public int getCount()
	{
		String countQuery = "SELECT  * FROM " + TABLE_NAME;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int c = cursor.getCount();
		cursor.close();

		return c;
	}

}
