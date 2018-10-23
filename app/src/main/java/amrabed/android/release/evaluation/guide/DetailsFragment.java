package amrabed.android.release.evaluation.guide;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import amrabed.android.release.evaluation.R;

/**
 * Fragment to show details of guide entry
 *
 * @author AmrAbed
 */
public class DetailsFragment extends Fragment
{
	private static final String TAG = DetailsFragment.class.getCanonicalName();
	private static final int[] entries = {R.raw.wakeup, R.raw.brush, R.raw.night, R.raw.fasting,
			R.raw.sunna, R.raw.fajr, R.raw.quran, R.raw.memorize, R.raw.morning, R.raw.duha,
			R.raw.sports, R.raw.friday, R.raw.work, R.raw.cong, R.raw.prayer, R.raw.rawateb,
			R.raw.evening, R.raw.isha, R.raw.wetr, R.raw.diet, R.raw.manners, R.raw.honesty,
			R.raw.backbiting, R.raw.gaze, R.raw.wudu, R.raw.sleep};

	public static DetailsFragment newInstance(int index)
	{
		final DetailsFragment detailsFragment = new DetailsFragment();

		final Bundle args = new Bundle();
		args.putInt("index", index);
		detailsFragment.setArguments(args);

		return detailsFragment;
	}

	private static String readText(InputStream inputStream)
	{

		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		int i;
		try
		{
			i = inputStream.read();
			while (i != -1)
			{
				byteArrayOutputStream.write(i);
				i = inputStream.read();
			}
			inputStream.close();
		} catch (IOException e)
		{
			Log.e(TAG, e.toString());
		}

		return byteArrayOutputStream.toString();
	}

	private int getShownIndex()
	{
		return getArguments().getInt("index", 0);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		getActivity().setTitle(getResources().getStringArray(R.array.titles)[getShownIndex()]);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		final View view = inflater.inflate(R.layout.guide_entry, container, false);
		TextView text = view.findViewById(R.id.text);
		text.setText(readText(getResources().openRawResource(entries[getShownIndex()])));
		return view;
	}
}