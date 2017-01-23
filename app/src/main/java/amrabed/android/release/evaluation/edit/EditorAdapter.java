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

			viewHolder.days.setVisibility(View.INVISIBLE);
			viewHolder.rename.setVisibility(View.INVISIBLE);
			viewHolder.delete.setVisibility(View.INVISIBLE);
		}
		else
		{
			view.setBackgroundColor(Color.WHITE);

			viewHolder.days.setVisibility(View.VISIBLE);
			viewHolder.rename.setVisibility(View.VISIBLE);
			viewHolder.delete.setVisibility(View.VISIBLE);
		}
		viewHolder.days.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				selectDays(position);
			}
		});
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


		viewHolder.text.setText(activity.getTitle(getContext()));
		return view;
	}

	private void selectDays(final int position)
	{
		final Activity activity = getItem(position);
		final boolean [] selected =  activity.getActiveDays(getContext().getResources().getInteger(R.integer.day_shift));
		new AlertDialog.Builder(getContext())
				.setTitle(R.string.select_days_title)
				.setMultiChoiceItems(R.array.days, selected, new DialogInterface.OnMultiChoiceClickListener()
				{
					@Override
					public void onClick(DialogInterface dialogInterface, int which, boolean isChecked)
					{
						final int day = Integer.parseInt(getContext().getResources().getStringArray(R.array.day_values)[which]);
						list.get(position).setActiveDay(day, isChecked);
					}
				})
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialogInterface, int which)
					{
						notifyDataSetChanged();
					}
				})
				.create().show();
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
		return !isExcluded(list.get(position).getTitle(getContext()));
	}

	private boolean isExcluded(String txt)
	{
		return ((txt.contains(getContext().getString(R.string.diet_q))) ||
				(txt.contains(getContext().getString(R.string.memorize_q))) ||
				(txt.contains(getContext().getString(R.string.fasting_q))));
	}

	private void showEditDialog(final int position, int title)
	{
		final EditText editText = (EditText) LayoutInflater.from(getContext())
				.inflate(R.layout.edit_dialog, null);
		editText.setText(list.get(position).getTitle(getContext()));
		new AlertDialog.Builder(getContext())
				.setTitle(title)
				.setView(editText)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						list.get(position).setCurrentTitle(editText.getText().toString());
						notifyDataSetChanged();
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
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
		private final ImageView days;
		private final ImageView rename;
		private final ImageView delete;
		private final ImageView up;
		private final ImageView down;


		private ViewHolder(View view)
		{
			text = (TextView) view.findViewById(R.id.text);
			days = (ImageView) view.findViewById(R.id.days);
			rename = (ImageView) view.findViewById(R.id.rename);
			delete = (ImageView) view.findViewById(R.id.delete);
			up = (ImageView) view.findViewById(R.id.up);
			down = (ImageView) view.findViewById(R.id.down);

		}
	}
}
