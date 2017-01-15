package amrabed.android.release.evaluation.edit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.core.Activity;
import amrabed.android.release.evaluation.core.ActivityList;

/**
 * Edit list adapter
 *
 * @author AmrAbed
 */
public class EditorAdapter extends ArrayAdapter<Activity>
{
	private final Listener listener;
	private final ActivityList list;

	public EditorAdapter(Context context, @LayoutRes int edit_item, Listener listener, ActivityList list)
	{
		super(context, edit_item, list);
		this.listener = listener;
		this.list = list;
	}

	@NonNull
	@Override
	public View getView(final int position, View view, @NonNull ViewGroup parent)
	{
		final Activity activity = getItem(position);

		if (view == null)
		{
			view = LayoutInflater.from(getContext()).inflate(R.layout.edit_item, parent, false);
		}
		final ViewHolder viewHolder = new ViewHolder(view);
		view.setTag(viewHolder);

		if (!isEnabled(position))
		{
			view.setBackgroundColor(Color.LTGRAY);

			viewHolder.rename.setVisibility(View.GONE);
			viewHolder.delete.setVisibility(View.GONE);
		}
		else
		{
			view.setBackgroundColor(Color.WHITE);

			viewHolder.rename.setVisibility(View.VISIBLE);
			viewHolder.delete.setVisibility(View.VISIBLE);
		}
		viewHolder.rename.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				showEditDialog(position, R.string.edit);
			}
		});

		viewHolder.delete.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				delete(position);
			}
		});

		viewHolder.up.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				moveItemUp(position);
			}
		});
		viewHolder.down.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				moveItemDown(position);
			}
		});


		viewHolder.text.setText(activity.getCurrentTitle());
		return view;
	}

	@Override
	public void notifyDataSetChanged()
	{
		super.notifyDataSetChanged();
		listener.onChange();
	}

	@Override
	public boolean areAllItemsEnabled()
	{
		return false;
	}

	@Override
	public boolean isEnabled(int position)
	{
		return !isExcluded(list.get(position).getCurrentTitle());
	}

	private boolean isExcluded(String txt)
	{
		return ((txt.contains(getContext().getString(R.string.recite_q))) || (txt
				.contains(getContext().getString(R.string.diet_q))) || (txt
				.contains(getContext().getString(R.string.memorize_q))) || (txt
				.contains(getContext().getString(R.string.fasting_q))));
	}

	private void showEditDialog(final int position, int title)
	{
		final EditText editText = (EditText) LayoutInflater.from(getContext())
				.inflate(R.layout.edit_dialog, null);
		editText.setText(list.get(position).getCurrentTitle());
		new AlertDialog.Builder(getContext())
				.setTitle(title)
				.setView(editText)
				.setPositiveButton(R.string.pos_button, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						list.get(position).setCurrentTitle(editText.getText().toString());
						notifyDataSetChanged();
					}
				})
				.setNegativeButton(R.string.neg_button, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						dialog.dismiss();
					}
				})
				.create().show();
	}

	private void moveItemUp(int position)
	{
		if (position == 0) return;
		final Activity temp = list.get(position - 1);
		list.set(position - 1, list.get(position));
		list.set(position, temp);
		notifyDataSetChanged();
	}

	private void moveItemDown(int position)
	{
		if (position == list.size() - 1) return;
		final Activity temp = list.get(position + 1);
		list.set(position + 1, list.get(position));
		list.set(position, temp);
		notifyDataSetChanged();
	}

	private void delete(int position)
	{
		list.remove(position);
		notifyDataSetChanged();
	}

	public interface Listener
	{
		void onChange();
	}

	private class ViewHolder
	{
		private final TextView text;
		private final ImageView rename;
		private final ImageView delete;
		private final ImageView up;
		private final ImageView down;


		private ViewHolder(View view)
		{
			text = (TextView) view.findViewById(R.id.text);
			rename = (ImageView) view.findViewById(R.id.rename);
			delete = (ImageView) view.findViewById(R.id.delete);
			up = (ImageView) view.findViewById(R.id.up);
			down = (ImageView) view.findViewById(R.id.down);

		}
	}
}
