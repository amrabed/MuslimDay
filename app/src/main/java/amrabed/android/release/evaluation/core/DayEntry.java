package amrabed.android.release.evaluation.core;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 *
 */
// Todo: Implement Parceleable
public class DayEntry implements Parcelable
{
	private final long date;
	private final HashMap<Long, Byte> selections;


	public DayEntry(long date)
	{
		this.date = new DateTime(date).withTimeAtStartOfDay().getMillis();
		this.selections = new HashMap<>();
	}

	public DayEntry(long date, byte[] selections) throws IOException, ClassNotFoundException
	{
		this.date = date;
		this.selections = deserialize(selections);
	}


	protected DayEntry(Parcel in)
	{
		date = in.readLong();
		selections = (HashMap<Long, Byte>) in.readSerializable();
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

	public void setSelectionAt(long position, byte selection)
	{
		selections.put(position, selection);
	}

	public byte getSelectionAt(long position)
	{
		return selections.get(position);
	}

	private static byte[] serialize(Object obj) throws IOException
	{
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(obj);
		objectOutputStream.flush();
		return byteArrayOutputStream.toByteArray();
	}

	private static HashMap<Long, Byte> deserialize(byte[] data) throws IOException, ClassNotFoundException
	{
		ByteArrayInputStream byteArrayIS = new ByteArrayInputStream(data);
		ObjectInputStream objectIS = new ObjectInputStream(byteArrayIS);
		return (HashMap<Long, Byte>) objectIS.readObject();
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
}
