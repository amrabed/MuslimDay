package amrabed.android.release.evaluation.guide;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import amrabed.android.release.evaluation.R;

/**
 * Guide section
 *
 * @author AmrAbed
 */

public class GuideSection extends ListFragment
{
	private static int currentPosition = 0;

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		setListAdapter(new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_list_item_activated_1,
				getResources().getStringArray(R.array.titles)));
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id)
	{
		showDetails(position);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		getActivity().setTitle(R.string.menu_guide);
	}

	void showDetails(int index)
	{
		currentPosition = index;

		getFragmentManager().beginTransaction().addToBackStack(null)
				.replace(R.id.content, DetailsFragment.newInstance(index)).commit();
	}

	/**
	 * Fragment for displaying details of selected title
	 */
	public static class DetailsFragment extends Fragment
	{
		public static DetailsFragment newInstance(int index)
		{
			final DetailsFragment detailsFragment = new DetailsFragment();

			final Bundle args = new Bundle();
			args.putInt("index", index);
			detailsFragment.setArguments(args);

			return detailsFragment;
		}

		public int getShownIndex()
		{
			return getArguments().getInt("index", 0);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
		                         Bundle savedInstanceState)
		{
//			if (container == null)
//			{
//				return null;
//			}
//			currentPosition = getShownIndex();

			final View view = inflater.inflate(R.layout.guide_entry, container, false);
			TextView text = (TextView) view.findViewById(R.id.text);
			text.setText(readText(getResources().openRawResource(entries[getShownIndex()])));
			return view;
		}

		@Override
		public void onResume()
		{
			super.onResume();
			getActivity().setTitle(getResources().getStringArray(R.array.titles)[currentPosition]);
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
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			return byteArrayOutputStream.toString();
		}

		private final static int entries[] = {R.raw.wakeup, R.raw.brush, R.raw.night, R.raw.fasting,
				R.raw.sunna, R.raw.fajr, R.raw.quran, R.raw.memorize, R.raw.morning, R.raw.duha,
				R.raw.sports, R.raw.friday, R.raw.work, R.raw.cong, R.raw.prayer, R.raw.rawateb,
				R.raw.evening, R.raw.isha, R.raw.wetr, R.raw.diet, R.raw.manners, R.raw.honesty,
				R.raw.backbiting, R.raw.gaze, R.raw.wudu, R.raw.sleep};
	}

}
