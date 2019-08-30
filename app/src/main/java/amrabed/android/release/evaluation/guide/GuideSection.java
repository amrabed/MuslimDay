package amrabed.android.release.evaluation.guide;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import amrabed.android.release.evaluation.R;

/**
 * Guide section
 */

public class GuideSection extends ListFragment
{
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

	private void showDetails(int index)
	{
		getFragmentManager().beginTransaction().addToBackStack(null)
				.replace(R.id.content, DetailsFragment.newInstance(index)).commit();
	}
}
