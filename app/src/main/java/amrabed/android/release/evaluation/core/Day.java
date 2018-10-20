package amrabed.android.release.evaluation.core;

@Deprecated
public class Day
{
	private static final byte FAST_MASK = 0x01;
	private static final byte RECITE_MASK = 0x02;
	private static final byte MEMORIZE_MASK = 0x04;
	private static final byte DIET_MASK = 0x08;
	private static final int RATIO_GOOD_MASK = 0xff00;
	private static final int RATIO_BAD_MASK = 0x00ff;
	private static final int RATIO_OK_MASK = 0xff0000;
	private static final byte RATIO_GOOD_OFFSET = 8;
	private static final byte RATIO_OK_OFFSET = 16;

	private long date;
	private long selections;
	private byte flags;
	private int ratios;
	private short totalNumber;

	public Day(long date, long selections)
	{
		// Create entry to be added to database
		this.date = date;
		this.selections = selections;
		flags = 0;
		calcRatios();
	}

	public Day(long date, long selections, byte flags, short totalNumber, int ratios)
	{
		// Create entry read from database
		this.date = date;
		this.selections = selections;
		this.flags = flags;
		this.totalNumber = totalNumber;
		this.ratios = ratios;
	}

	public long getDate()
	{
		return date;
	}

	public void setDate(long date)
	{
		this.date = date;
	}

	public long getSelections()
	{
		return selections;
	}

	public void setSelections(long selections)
	{
		this.selections = selections;
	}

	public byte getFlags()
	{
		return flags;
	}

	public void setFlags(byte flags)
	{
		this.flags = flags;
	}

	public int getRatios()
	{
		return ratios;
	}

	public void setRatios(short ratios)
	{
		this.ratios = ratios;
	}

	public short getTotalNumber()
	{
		return totalNumber;
	}

	public void setTotalNumber(short totalNumber)
	{
		this.totalNumber = totalNumber;
	}

	public int getSelectionAt(int position)
	{
		return (int) ((selections >>> (2 * position)) & 3);
	}

	public void updateSelectionAt(int position, long x)
	{
		selections &= ~((long) 3 << (2 * position));
		selections |= (x << (2 * position));
		calcRatios();
	}

	private void calcRatios()
	{
		long value = selections;
		int[] p = new int[3];
		while (value > 0)
		{
			switch ((int) (value & 3))
			{
				case Selection.GOOD:
					p[0]++;
					break;
				case Selection.BAD:
					p[1]++;
					break;
				case Selection.OK:
					p[2]++;
					break;
			}
			value >>= 2;
		}
		// This code is left the way  it is to guarantee app backward compatibility
		ratios = (ratios & (~RATIO_OK_MASK)) | (p[2] << RATIO_OK_OFFSET);
		ratios = (ratios & (~RATIO_GOOD_MASK)) | (p[0] << RATIO_GOOD_OFFSET);
		ratios = (ratios & (~RATIO_BAD_MASK)) | p[1];
	}

	public int getOkRatio()
	{
		return (ratios & (RATIO_OK_MASK)) >> RATIO_OK_OFFSET;
	}

	public int getGoodRatio()
	{
		return (ratios & (RATIO_GOOD_MASK)) >> RATIO_GOOD_OFFSET;
	}

	public int getBadRatio()
	{
		return ratios & (RATIO_BAD_MASK);
	}

	public boolean isFastingDay()
	{
		return ((flags & FAST_MASK) != 0);
	}

	public void setFastingDay(boolean flag)
	{
		if (flag)
		{
			flags |= FAST_MASK;
		}
		else
		{
			flags &= (~FAST_MASK);
		}
	}

	public boolean isRecitingDay()
	{
		return ((flags & RECITE_MASK) != 0);
	}

	public void setRecitingDay(boolean flag)
	{
		if (flag)
		{
			flags |= RECITE_MASK;
		}
		else
		{
			flags &= (~RECITE_MASK);
		}
	}

	public boolean isMemorizingDay()
	{
		return ((flags & MEMORIZE_MASK) != 0);
	}

	public void setMemorizingDay(boolean flag)
	{
		if (flag)
		{
			flags |= MEMORIZE_MASK;
		}
		else
		{
			flags &= (~MEMORIZE_MASK);
		}
	}

	public boolean isDietDay()
	{
		return ((flags & DIET_MASK) != 0);
	}

	public void setDietDay(boolean flag)
	{
		if (flag)
		{
			flags |= DIET_MASK;
		}
		else
		{
			flags &= (~DIET_MASK);
		}
	}

}
