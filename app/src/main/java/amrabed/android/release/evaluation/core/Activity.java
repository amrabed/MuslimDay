package amrabed.android.release.evaluation.core;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;

import java.util.Random;

/**
 * Activity in the action list
 */

public class Activity implements Parcelable
{
	private static int lastIndex = 0;

	private final long uniqueId;
	private final int defaultIndex;
	private final String defaultTitle;
	private final int guideEntry;

	private int currentIndex;
	private String currentTitle;
	private boolean[] activeDays = new boolean[7];
	private int selection;

	public Activity()
	{
		this(-1, null, 0);
	}

	public Activity(@Nullable String defaultTitle, @RawRes int guideEntry)
	{
		this(-1, defaultTitle, guideEntry);
	}

	public Activity(int defaultIndex, @Nullable String defaultTitle, @RawRes int guideEntry)
	{
		this(new Random(System.currentTimeMillis()).nextLong(), defaultIndex, defaultTitle, guideEntry);
	}

	/**
	 * To be used by database only
	 */
	public Activity(long id, int defaultIndex, @Nullable String defaultTitle, @RawRes int guideEntry)
	{
		this.uniqueId = id;
		this.defaultTitle = defaultTitle;
		this.defaultIndex = defaultIndex;
		this.guideEntry = guideEntry;
		this.activeDays = new boolean[7];
	}


	public long getUniqueId()
	{
		return uniqueId;
	}

	public String getDefaultTitle()
	{
		return defaultTitle;
	}

	public int getGuideEntry()
	{
		return guideEntry;
	}

	public int getDefaultIndex()
	{
		return defaultIndex;
	}

	public String getCurrentTitle()
	{
		return currentTitle == null ? defaultTitle : currentTitle;
	}

	public Activity setCurrentTitle(String currentTitle)
	{
		this.currentTitle = currentTitle;
		return this;
	}

	public int getCurrentIndex()
	{
		return currentIndex;
	}

	public Activity setCurrentIndex(int currentIndex)
	{
		this.currentIndex = currentIndex;
		return this;
	}

	public boolean isActiveDay(int day)
	{
		return activeDays[day - 1];
	}

	public byte getActiveDays()
	{
		byte result = 0;
		for (int i = 0; i < activeDays.length; i++)
		{
			if (activeDays[i])
			{
				result |= (1 << i);
			}

		}
		return result;
	}

	public Activity setActiveDays(byte activeDays)
	{
		// ToDo: implement this
		return this;
	}

	public Activity setActiveDays(boolean[] activeDays)
	{
		this.activeDays = activeDays;
		return this;
	}

	public int getSelection()
	{
		return selection;
	}

	public Activity setSelection(int selection)
	{
		this.selection = selection;
		return this;
	}

	protected Activity(Parcel parcel)
	{
		uniqueId = parcel.readLong();
		defaultTitle = parcel.readString();
		guideEntry = parcel.readInt();
		currentTitle = parcel.readString();
		defaultIndex = parcel.readInt();
		currentIndex = parcel.readInt();
		selection = parcel.readInt();
		parcel.readBooleanArray(activeDays);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i)
	{
		parcel.writeLong(uniqueId);
		parcel.writeString(defaultTitle);
		parcel.writeInt(guideEntry);
		parcel.writeString(currentTitle);
		parcel.writeInt(defaultIndex);
		parcel.writeInt(currentIndex);
		parcel.writeInt(selection);
		parcel.writeBooleanArray(activeDays);
	}

	public static final Creator<Activity> CREATOR = new Creator<Activity>()
	{
		@Override
		public Activity createFromParcel(Parcel parcel)
		{
			return new Activity(parcel);
		}

		@Override
		public Activity[] newArray(int size)
		{
			return new Activity[size];
		}
	};

	@Override
	public String toString()
	{
		return "Activity: {id: " + uniqueId + ", title: " + getCurrentTitle() + "}";
	}
}
