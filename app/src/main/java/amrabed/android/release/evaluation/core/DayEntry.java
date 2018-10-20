package amrabed.android.release.evaluation.core;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import androidx.annotation.NonNull;

/**
 * Entry of a day in the database
 */
public class DayEntry implements Parcelable
{
	private final long date;
	private final Selections selections;


	public DayEntry(long date)
	{
		this.date = new DateTime(date).withTimeAtStartOfDay().getMillis();
		this.selections = new Selections();
	}

	public DayEntry(long date, byte[] selections) throws IOException, ClassNotFoundException
	{
		this.date = date;
		this.selections = deserialize(selections);
	}


	private DayEntry(Parcel in)
	{
		date = in.readLong();
		selections = (Selections) in.readSerializable();
	}

	public static final Creator<DayEntry> CREATOR = new Creator<DayEntry>()
	{
		@Override
		public DayEntry createFromParcel(Parcel in)
		{
			return new DayEntry(in);
		}

		@Override
		public DayEntry[] newArray(int size)
		{
			return new DayEntry[size];
		}
	};

	public long getDate()
	{
		return date;
	}

	public byte[] getSelections() throws IOException
	{
		return serialize(selections);
	}

	public void setSelectionAt(String id, byte selection)
	{
		selections.put(id, selection);
	}

	public byte getSelection(@NonNull String id)
	{
		final Byte value = selections.get(id);
		return value == null ? 0 : value;
	}

	public float[] getRatios()
	{
		final float[] ratios = {0, 0, 0, 0};
		for (Byte selection : selections.values())
		{
			ratios[selection]++;
		}
		return ratios;
	}

	private static byte[] serialize(Selections obj) throws IOException
	{
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(obj);
		objectOutputStream.flush();
		return byteArrayOutputStream.toByteArray();
	}

	private static Selections deserialize(byte[] data) throws IOException, ClassNotFoundException
	{
		ByteArrayInputStream byteArrayIS = new ByteArrayInputStream(data);
		ObjectInputStream objectIS = new ObjectInputStream(byteArrayIS);
		return (Selections) objectIS.readObject();
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i)
	{
		parcel.writeLong(date);
		parcel.writeSerializable(selections);
	}

	/**
	 * Selections class to avoid 'unchecked cast' warning
	 */
	private class Selections extends HashMap<String, Byte> implements Serializable {

	}
}
