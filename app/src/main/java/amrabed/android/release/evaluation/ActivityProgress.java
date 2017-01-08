package amrabed.android.release.evaluation;

import java.util.List;

import org.joda.time.LocalDate;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ActivityProgress extends ListActivity implements OnNavigationListener
{
	static final int MY_INDEX = 1;

	List<DatabaseEntry> entries;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setListAdapter(new MyAdapter(this, android.R.layout.simple_list_item_activated_1));
		entries = ApplicationEvaluation.db.getAllEntries();
		for (int i = 0; i < entries.size(); i++)
		{
			((MyAdapter) getListAdapter()).add("");
		}
		getListView().setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		getListView().setStackFromBottom(true);
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(new ArrayAdapter<CharSequence>(this,R.layout.item_spinner, getResources().getStringArray(R.array.spinner)), this);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setSelectedNavigationItem(MY_INDEX);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.progress_options, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menu_settings:
				startActivity(new Intent(this, ActivityPreferences.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		getActionBar().setSelectedNavigationItem(MY_INDEX);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		startActivity(new Intent(this,ActivityMain.class).putExtra("POS", position));
	}

	private class MyAdapter extends ArrayAdapter<String>
	{

		public MyAdapter(Context context, int id)
		{
			super(context, id);
		}

		@Override
		public View getView(int position, View view, ViewGroup parent)
		{
			Holder holder = new Holder();
			if (view == null)
			{
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.item_progress, parent, false);
				holder.tv = (TextView) view.findViewById(R.id.textView1);
				holder.pb = (ProgressBar) view.findViewById(R.id.progressBar1);
				view.setTag(holder);
			}
			else
			{
				holder = (Holder) view.getTag();
			}
			DatabaseEntry e = entries.get(position);

			holder.tv.setText(new LocalDate(e.date).toString("E d MMMMMMMM yyyy"));
			holder.pb.setMax(e.totalNumber);
			holder.pb.setProgress(e.getGoodRatio());
			holder.pb.setSecondaryProgress(e.totalNumber - e.getBadRatio());
			return view;
		}

		class Holder
		{
			TextView tv;
			ProgressBar pb;
		}
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId)
	{
		if(itemPosition != MY_INDEX)
		{
			startActivity(new Intent(this, ActivityMain.class));
		}
		return true;
	}

}
