package amrabed.android.release.evaluation.guide;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import amrabed.android.release.evaluation.R;

/**
 * Fragment to show guide entry
 *
 * @author AmrAbed
 */

public class DetailsFragment extends Fragment
{
	public static DetailsFragment newInstance(int entry, String title)
	{
		DetailsFragment detailsFragment = new DetailsFragment();

		Bundle args = new Bundle();
		args.putInt("index", entry);
		args.putString("TITLE", title);
		detailsFragment.setArguments(args);

		return detailsFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
	                         Bundle savedInstanceState)
	{
		final View view = inflater.inflate(R.layout.guide_entry, parent, false);
		final TextView text = (TextView) view.findViewById(R.id.text);
		text.setText(readText(getResources().openRawResource(getArguments().getInt("index"))));
		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		getActivity().setTitle(getArguments().getString("TITLE"));
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
}
