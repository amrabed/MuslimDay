package amrabed.android.release.evaluation.core;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RawRes;

import java.util.Random;

import amrabed.android.release.evaluation.preferences.Preferences;

/**
 * Activity in the action list
 */

public class Activity implements Parcelable
{
	public static final byte ACTIVE_EVERYDAY = (byte) 0x7F;
	public static final byte ACTIVE_FRIDAY = (byte) 0x10;

	private final long uniqueId;
	private final int defaultIndex;
	//	private final int defaultTitle;
	private final int guideEntry;
	private String prefKey;

	private int currentIndex;
	private String currentTitle;
	private boolean[] activeDays = new boolean[7];
	private int selection;

	public Activity()
	{
		this(-1, 0);
	}

	public Activity(@RawRes int guideEntry)
	{
		this(-1, guideEntry);
	}

	public Activity(int defaultIndex, @RawRes int guideEntry)
	{
		this(new Random(System.currentTimeMillis()).nextLong(), defaultIndex, guideEntry);
	}

	/**
	 * To be used by database only
	 */
	public Activity(long id, int defaultIndex, @RawRes int guideEntry)
	{
		this.uniqueId = id;
		this.defaultIndex = defaultIndex;
		this.guideEntry = guideEntry;
		this.activeDays = new boolean[7];
		setActiveDays(ACTIVE_EVERYDAY);
	}


	public long getUniqueId()
	{
		return uniqueId;
	}

//	public int getDefaultTitle()
//	{
//		return defaultTitle;
//	}

	public int getGuideEntry()
	{
		return guideEntry;
	}

	public int getDefaultIndex()
	{
		return defaultIndex;
	}

	public String getTitle(Context context)
	{
		return currentTitle != null ? currentTitle : getDefaultTitle(context);
	}

	public String getDefaultTitle(Context context)
	{
		return Preferences.getActivities(context)[defaultIndex];
	}


	public String getCurrentTitle()
	{
		return currentTitle;
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

	public Activity setActiveDay(int day, boolean isActive)
	{
		activeDays[day - 1] = isActive;
		return this;
	}

	public boolean isActiveDay(int day)
	{
		return activeDays[day - 1];
	}

	public boolean[] getActiveDays()
	{
		return activeDays;
	}

	public byte getActiveDaysByte()
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
		for (int i = 0; i < this.activeDays.length; i++)
		{
			this.activeDays[i] = (activeDays & 0x01) == 0x01;
			activeDays >>>= 1;
		}
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
//		defaultTitle = parcel.readInt();
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
//		parcel.writeInt(defaultTitle);
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
		return "Activity: {id: " + uniqueId + ", title: " + currentTitle + "}";
	}

	public String getPrefKey()
	{
		return prefKey;
	}

	public Activity setPrefKey(String prefKey)
	{
		this.prefKey = prefKey;
		return this;
	}
}
