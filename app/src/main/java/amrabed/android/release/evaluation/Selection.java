package amrabed.android.release.evaluation;

public class Selection
{
	private int index = 0;

	static class Value
	{
		final static int NA = 0;
		final static int GOOD = 1;
		final static int OK = 2;
		final static int BAD = 3;

		static int[] list = { NA, GOOD, OK, BAD };
	}

	private static class Color
	{
		static int GREEN = 0xff00E0A8;//008563;
		static int BLUE = 0xff42D0FF;//0xff3D6EFF;
		static int RED =  0xffFF7598;//B30000;
		final static int WHITE = 0xffffffff;

		static int[] list = { WHITE, GREEN, BLUE, RED };
	}
	static class Icon
	{
		static int YES = R.drawable.yes;
		static int OK = R.drawable.ok;
		static int NO =  R.drawable.no;
		final static int NA = 0;

		static int[] list = { NA, YES, OK, NO };
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

	public int getValue()
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
