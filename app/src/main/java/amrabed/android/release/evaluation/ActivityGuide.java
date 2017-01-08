/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package amrabed.android.release.evaluation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class ActivityGuide extends Activity
{
	static int mCurCheckPosition = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_guide);
		getActionBar().setDisplayShowHomeEnabled(false);
	}

	public static class DetailsActivity extends Activity
	{

		@Override
		protected void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);

			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			{
				finish();
				return;
			}
			getActionBar().setDisplayShowHomeEnabled(false);
			getActionBar().setTitle(getResources().getStringArray(R.array.titles)[getIntent().getExtras().getInt("index")]);

			if (savedInstanceState == null)
			{
				DetailsFragment details = new DetailsFragment();
				details.setArguments(getIntent().getExtras());
				getFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
			}
		}
		// @Override
		// public void onConfigurationChanged(Configuration newConfig)
		// {
		// if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
		// {
		// finish();
		// }
		// }
	}

	public static class TitlesFragment extends ListFragment
	{
		boolean mDualPane;

		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
			super.onActivityCreated(savedInstanceState);

			setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, getResources().getStringArray(R.array.titles)));

			View detailsFrame = getActivity().findViewById(R.id.details);
			mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

			// if (savedInstanceState != null)
			// {
			// mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
			// }

			if (mDualPane)
			{
				getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				showDetails(mCurCheckPosition);
			}
		}

		// @Override
		// public void onSaveInstanceState(Bundle outState)
		// {
		// super.onSaveInstanceState(outState);
		// outState.putInt("curChoice", mCurCheckPosition);
		// }

		@Override
		public void onListItemClick(ListView l, View v, int position, long id)
		{
			showDetails(position);
		}

		@Override
		public void onResume()
		{
			super.onResume();
			if (mDualPane)
			{
				getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				showDetails(mCurCheckPosition);
			}
		}

		void showDetails(int index)
		{
			mCurCheckPosition = index;

			if (mDualPane)
			{
				getListView().setItemChecked(index, true);

				DetailsFragment details = (DetailsFragment) getFragmentManager().findFragmentById(R.id.details);
				if (details == null || details.getShownIndex() != index)
				{
					details = DetailsFragment.newInstance(index);

					getFragmentManager().beginTransaction().replace(R.id.details, details).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
				}

			}
			else
			{
				Intent intent = new Intent();
				intent.setClass(getActivity(), DetailsActivity.class);
				intent.putExtra("index", index);
				startActivity(intent);
			}
		}
	}

	public static class DetailsFragment extends Fragment
	{
		public static DetailsFragment newInstance(int index)
		{
			DetailsFragment f = new DetailsFragment();

			Bundle args = new Bundle();
			args.putInt("index", index);
			f.setArguments(args);

			return f;
		}

		public int getShownIndex()
		{
			return getArguments().getInt("index", 0);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			if (container == null)
			{
				return null;
			}
			mCurCheckPosition = getShownIndex();

			ScrollView scroller = new ScrollView(getActivity());
			TextView text = new TextView(getActivity());
			int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getActivity().getResources().getDisplayMetrics());
			text.setPadding(padding, padding, padding, padding);
			text.setLineSpacing(0, (float)1.5);
			scroller.addView(text);
			text.setText(readText(getResources().openRawResource(entries[getShownIndex()])));
			return scroller;
		}
	}

	private static String readText(InputStream inputStream)
	{

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

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

	final static int entries[] = { R.raw.wakeup, R.raw.brush, R.raw.night, R.raw.fasting, R.raw.sunna, R.raw.fajr, R.raw.quran, R.raw.memorize,R.raw.morning, R.raw.duha, R.raw.sports, R.raw.friday, R.raw.work, R.raw.cong,
			R.raw.prayer, R.raw.rawateb, R.raw.evening, R.raw.isha, R.raw.wetr, R.raw.diet, R.raw.manners, R.raw.honesty, R.raw.backbiting, R.raw.gaze,  R.raw.wudu, R.raw.sleep };

}
