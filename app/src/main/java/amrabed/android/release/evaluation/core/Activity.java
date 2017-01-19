package amrabed.android.release.evaluation.core;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RawRes;
import android.util.SparseArray;

import java.util.UUID;

import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.preferences.Preferences;

/**
 * Activity in the action list
 */

public class Activity implements Parcelable
{
	public static final byte ACTIVE_EVERYDAY = (byte) 0x7F;
	public static final byte ACTIVE_FRIDAY = (byte) 0x10;

	private final String id;
	private final int defaultIndex;
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

	public Activity(int defaultIndex, @RawRes int guideEntry)
	{
		this(UUID.randomUUID().toString(), defaultIndex, guideEntry);
	}

	/**
	 * To be used by database only
	 */
	public Activity(String id, int defaultIndex, @RawRes int guideEntry)
	{
		this.id = id;
		this.defaultIndex = defaultIndex;
		this.guideEntry = guideEntry;
		this.activeDays = new boolean[7];
		setActiveDays(ACTIVE_EVERYDAY);
	}

	public String getId()
	{
		return id;
	}

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

	/**
	 * Updates active days based on User selected preferences
	 *
	 * @return this
	 */
	public Activity setActiveDays(Context context)
	{
		if (guideEntry == R.raw.friday)
		{
			setActiveDays(Activity.ACTIVE_FRIDAY);
			return this;
		}

		final String prefKey = map.get(guideEntry);
		if (prefKey != null)
		{
			setActiveDays(Preferences.getActiveDays(context, prefKey));
		}
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
		id = parcel.readString();
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
		parcel.writeString(id);
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
		return "Activity: {id: " + id + ", title: " + currentTitle + "}";
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

	private static final SparseArray<String> map = new SparseArray<>();

	static
	{
		map.put(R.raw.quran, "reciteDays");
		map.put(R.raw.memorize, "memorizeDays");
		map.put(R.raw.diet, "dietDays");
	}
}
