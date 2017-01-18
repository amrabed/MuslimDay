package amrabed.android.release.evaluation.core;

import amrabed.android.release.evaluation.R;

public class Selection
{
	private int index = 0;

	public static class Value
	{
		public final static byte NA = 0;
		public final static byte GOOD = 1;
		public final static byte OK = 2;
		public final static byte BAD = 3;

		public static byte[] list = { NA, GOOD, OK, BAD };
	}

	private static class Color
	{
		static int GREEN = 0xff00E0A8;//008563;
		static int BLUE = 0xff42D0FF;//0xff3D6EFF;
		static int RED =  0xffFF7598;//B30000;
		final static int WHITE = 0xffffffff;

		static int[] list = { WHITE, GREEN, BLUE, RED };
	}
	public static class Icon
	{
		static int YES = R.drawable.ic_check;
		static int OK = R.drawable.ic_neutral;
		static int NO =  R.drawable.ic_clear;
		final static int NA = 0;

		public static int[] list = { NA, YES, OK, NO };
	}

	private static class Message
	{
		final static int YES = R.string.yes;
		final static int NO_WITH_EXECUSE = R.string.no_w;
		final static int NO_WITH_NO_EXECUSE = R.string.no_wo;
		final static int NOT_YET = R.string.not_yet;

		static int[] list = { NOT_YET, YES, NO_WITH_EXECUSE, NO_WITH_NO_EXECUSE };
	}

	public Selection(int sel)
	{
		index = sel;
	}

	public Selection getNext()
	{
		index = ((index + 1) % 4);
		return this;
	}

	public byte getValue()
	{
		return Value.list[index];
	}

	public int getMessage()
	{
		return Message.list[index];
	}

	public int getColor()
	{
		return Color.list[index];
	}
	public int getIcon()
	{
		return Icon.list[index];
	}
}
