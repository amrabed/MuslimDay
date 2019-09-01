package amrabed.android.release.evaluation.core;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;

import java.util.UUID;

import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.preferences.Preferences;

/**
 * Task in the action list
 */

public class Task implements Parcelable
{
	private static final byte ACTIVE_EVERYDAY = (byte) 0x7F;
	private static final byte ACTIVE_FRIDAY = (byte) 0x10;

	private final String id;
	private final int defaultIndex;
	private final int guideEntry;
	private String prefKey;

	private int currentIndex;
	private String currentTitle;
	private boolean[] activeDays = new boolean[7];
	private int selection;

	public Task()
	{
		this(-1, 0);
	}

	Task(int defaultIndex, @RawRes int guideEntry)
	{
		this(UUID.randomUUID().toString(), defaultIndex, guideEntry);
	}

	/**
	 * To be used by database only
	 */
	public Task(String id, int defaultIndex, @RawRes int guideEntry)
	{
		this.id = id;
		this.defaultIndex = defaultIndex;
        this.guideEntry = defaultIndex == -1 ? 0 : TaskList.entries[defaultIndex];
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

	private Task(Parcel parcel)
	{
		id = parcel.readString();
		guideEntry = parcel.readInt();
		currentTitle = parcel.readString();
		defaultIndex = parcel.readInt();
		currentIndex = parcel.readInt();
		selection = parcel.readInt();
		parcel.readBooleanArray(activeDays);
	}

	public String getCurrentTitle()
	{
		return currentTitle;
	}

	public Task setCurrentTitle(String currentTitle)
	{
		this.currentTitle = currentTitle;
		return this;
	}

	public int getCurrentIndex()
	{
		return currentIndex;
	}

	public Task setCurrentIndex(int currentIndex)
	{
		this.currentIndex = currentIndex;
		return this;
	}

	private String getDefaultTitle(Context context)
	{
		return Preferences.getActivities(context)[defaultIndex];
	}

	/**
	 * Updates active days based on User selected preferences
	 *
	 * @return this
	 */
	Task setActiveDays(Context context)
	{
		if (guideEntry == R.raw.friday)
		{
			setActiveDays(Task.ACTIVE_FRIDAY);
			return this;
		}

		final String key = map.get(guideEntry);
		if (key != null)
		{
			setActiveDays(Preferences.getActiveDays(context, key));
		}
		return this;
	}

	boolean isActiveDay(int day)
	{
		return activeDays[day - 1];
	}

	public boolean[] getActiveDays()
	{
		return activeDays;
	}

	/**
	 * Get shifted version of active days for Arabic list of days
	 * (Mon, Tue, ..., Fri) -> (Sat, Sun, ..., Fri)
	 * @param shift number of days
	 * @return shifted version of active days
	 */
	public boolean[] getActiveDays(int shift)
	{
		if(shift == 0) return activeDays;
		boolean [] shifted = new boolean[7];
		for(int i = 0; i < activeDays.length; i++)
		{
			shifted[(i + shift)% 7] = activeDays[i];
		}
		return shifted;
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

	public Task setActiveDays(byte activeDays)
	{
		for (int i = 0; i < this.activeDays.length; i++)
		{
			this.activeDays[i] = (activeDays & 0x01) == 0x01;
			activeDays >>>= 1;
		}
		return this;
	}

	public Task setActiveDays(boolean[] activeDays)
	{
		this.activeDays = activeDays;
		return this;
	}

	public int getSelection()
	{
		return selection;
	}

	public Task setSelection(int selection)
	{
		this.selection = selection;
		return this;
	}

	public void setActiveDay(int day, boolean isActive)
	{
		activeDays[day - 1] = isActive;
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

	public static final Creator<Task> CREATOR = new Creator<Task>()
	{
		@Override
		public Task createFromParcel(Parcel parcel)
		{
			return new Task(parcel);
		}

		@Override
		public Task[] newArray(int size)
		{
			return new Task[size];
		}
	};

	@Override
    public @NonNull
    String toString()
	{
		return "Task: {id: " + id + ", title: " + currentTitle + "}";
	}

	public String getPrefKey()
	{
		return prefKey;
	}

	public Task setPrefKey(String prefKey)
	{
		this.prefKey = prefKey;
		return this;
	}

	private static final SparseArray<String> map = new SparseArray<>();

	static
	{
		map.put(R.raw.memorize, "memorizeDays");
		map.put(R.raw.diet, "dietDays");
	}
}
