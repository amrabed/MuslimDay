package amrabed.android.release.evaluation;

import java.util.List;

import org.joda.time.LocalDate;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import amrabed.android.release.evaluation.app.ApplicationEvaluation;
import amrabed.android.release.evaluation.db.DatabaseEntry;

public class ProgressSection extends ListFragment
{
	private static final int MY_INDEX = 1;

	private List<DatabaseEntry> entries;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		entries = ApplicationEvaluation.getDatabase().getAllEntries();
		setListAdapter(new MyAdapter(getActivity()));
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		getListView().setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		getListView().setStackFromBottom(true);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		getActivity().setTitle(R.string.menu_progress);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id)
	{
		super.onListItemClick(listView, view, position, id);
//		startActivity(new Intent(this,MainActivity.class).putExtra("POS", position));
	}

	private class MyAdapter extends ArrayAdapter<DatabaseEntry>
	{

		MyAdapter(Context context)
		{
			super(context, android.R.layout.simple_list_item_activated_1, entries);
		}

		@NonNull
		@Override
		public View getView(int position, View view, @NonNull ViewGroup parent)
		{
			ViewHolder holder;
			if (view == null)
			{
				view = LayoutInflater.from(getActivity()).inflate(R.layout.item_progress, parent, false);
				holder = new ViewHolder(view);
				view.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) view.getTag();
			}
			final DatabaseEntry entry = entries.get(position);

			holder.textView.setText(new LocalDate(entry.getDate()).toString("E d MMM yyyy"));
			holder.progressBar.setMax(entry.getTotalNumber());
			holder.progressBar.setProgress(entry.getGoodRatio());
			holder.progressBar.setSecondaryProgress(entry.getTotalNumber() - entry.getBadRatio());
			return view;
		}

		class ViewHolder
		{
			final TextView textView;
			final ProgressBar progressBar;

			ViewHolder(View view)
			{
				textView = (TextView) view.findViewById(R.id.textView1);
				progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
			}
		}
	}
}
