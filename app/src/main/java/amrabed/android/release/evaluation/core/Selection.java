package amrabed.android.release.evaluation.core;

import amrabed.android.release.evaluation.R;

public class Selection
{
	public static final byte NONE = 0;
	public static final byte GOOD = 1;
	public static final byte OK = 2;
	public static final byte BAD = 3;

	private static final int[] COLORS = {R.color.none, R.color.good, R.color.ok, R.color.bad};
	private static final int[] ICONS = {0, R.drawable.ic_check, R.drawable.ic_neutral, R.drawable.ic_clear};

	private byte value;

	public Selection(byte value)
	{
		this.value = value;
	}

	public Selection next()
	{
		value = (byte) ((value + 1) % 4);
		return this;
	}

	public byte getValue()
	{
		return value;
	}

	public int getIcon()
	{
		return ICONS[value];
	}

	public static int[] getColors()
	{
		return COLORS;
	}

	public static int getIcon(byte selection)
	{
		return ICONS[selection];
	}
}
