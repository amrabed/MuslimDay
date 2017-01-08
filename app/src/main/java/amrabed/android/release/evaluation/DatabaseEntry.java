package amrabed.android.release.evaluation;

public class DatabaseEntry
{
	long date;
	long selections;
	byte flags;
	short ratios;
	short totalNumber;

	// Constructors
	// public Entry(long date, long selections, int isFastingDay, short
	// goodRatio, short badRatio, short numITems)
	// {
	// this.date = date;
	// this.selections = selections;
	// this.ratio1 = goodRatio;
	// this.ratio2 = badRatio;
	// this.numItems = numITems;
	// }

	public DatabaseEntry(long date, long selections)
	{
		// Create entry to be added to database
		this.date = date;
		this.selections = selections;
		flags = 0;
		calcRatios();
	}
//	public Entry(long date, long selections, byte n /*up to 256 items*/)
//	{
//		// Create entry to be added to database
//		this.date = date;
//		this.selections = selections;
//		flags = 0;
//		totalNumber = n;
//		calcRatios();
//	}
	public DatabaseEntry(long date, long selections, byte flags, short totalNumber, short ratios)
	{
		// Create entry read from database
		this.date = date;
		this.selections = selections;
		this.flags = flags;
		this.totalNumber = totalNumber;
		this.ratios = ratios;
	}

	int getSelectionAt(int position)
	{
		return (int) ((selections >>> (2 * position)) & 3);
	}

	void updateSelectionAt(int position, long x)
	{
		selections &= ~((long) 3 << (2 * position));
		selections |= (x << (2 * position));
		calcRatios();
	}

	short[] calcRatios()
	{
		long value = selections;
		short[] p = new short[2];
		while (value > 0)
		{
			switch ((int) (value & 3))
			{
				case Selection.Value.GOOD:
					p[0]++;
					break;
				case Selection.Value.BAD:
					p[1]++;
					break;
			}
			value >>= 2;
		}
		ratios = (short) ((ratios & (~RATIO_GOOD_MASK)) | (p[0]<<8));
		ratios = (short) ((ratios & (~RATIO_BAD_MASK)) | p[1]);
		return p;
	}

	public short getGoodRatio()
	{
		return (short) ((ratios & (RATIO_GOOD_MASK)) >> 8);
	}

	public short getBadRatio()
	{
		return (short) (ratios & (RATIO_BAD_MASK));
	}

	public boolean isFastingDay()
	{
		return ((flags & FAST_MASK) != 0);
	}

	public boolean isRecitingDay()
	{
		return ((flags & RECITE_MASK) != 0);
	}

	public boolean isMemorizingDay()
	{
		return ((flags & MEMORIZE_MASK) != 0);
	}

	public boolean isDietDay()
	{
		return ((flags & DIET_MASK) != 0);
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

	public static final byte FAST_MASK = 0x01;
	public static final byte RECITE_MASK = 0x02;
	public static final byte MEMORIZE_MASK = 0x04;
	public static final byte DIET_MASK = 0x08;
	public static final short RATIO_GOOD_MASK = (short)0xff00;
	public static final short RATIO_BAD_MASK = (short)0x00ff;
	public static final byte RATIO_GOOD_OFFSET = 16;
	public static final byte RATIO_BAD_OFFSET = 8;

}
