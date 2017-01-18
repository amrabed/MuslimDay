package amrabed.android.release.evaluation.edit;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.app.ApplicationEvaluation;
import amrabed.android.release.evaluation.core.Activity;
import amrabed.android.release.evaluation.core.ActivityList;

/**
 * Edit list fragment
 *
 * @author AmrAbed
 */

public class Editor extends ListFragment implements OnBackPressedListener, EditorAdapter.Listener
{
	private static final String POSITION_KEY = "position";
	private ActivityList list;
	private EditorAdapter adapter;

	private int position;
	public boolean isChanged = false;
	public boolean isSaved = false;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		if (savedInstanceState != null)
		{
			position = savedInstanceState.getInt(POSITION_KEY);
		}
		loadCurrentList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		adapter = new EditorAdapter(getActivity(), R.layout.edit_item, this, list);
		setListAdapter(adapter);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		registerForContextMenu(getListView());
		getListView().scrollTo(0, position);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		getActivity().getMenuInflater().inflate(R.menu.menu_edit, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menu_save:
				showAlertDialog(R.string.save, R.string.confirm_save);
				return true;
			case R.id.menu_discard:
				showAlertDialog(R.string.discard, R.string.confirm_discard);
				return true;
			case R.id.menu_reset:
				showAlertDialog(R.string.reset, R.string.confirm_reset);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		getActivity().getMenuInflater().inflate(R.menu.edit_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.add_before:
				handleInsert(R.string.add_before);
				return true;
			case R.id.add_after:
				handleInsert(R.string.add_after);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putInt(POSITION_KEY, getListView().getScrollY());
	}

	@Override
	public void onBackPressed()
	{
		if (isChanged && !isSaved)
		{
			new AlertDialog.Builder(getActivity())
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("").setMessage(R.string.confirm_save)
					.setPositiveButton(R.string.dialog_yes,
							new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									save();
									goHome();
								}

							})
					.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener()
					{

						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							goHome();
						}
					})
					.create().show();
		}
		else
		{
			goHome();
		}
	}

	private void goHome()
	{
		getActivity().onBackPressed();
//		getFragmentManager().beginTransaction().replace(R.id.content, new EvaluationSection())
//				.commit();
	}

	private void handleInsert(@StringRes int title)
	{
		if (list.size() < (Long.SIZE / 2))
		{

			showEditDialog(position, title);
		}
		else
		{
			new AlertDialog.Builder(getActivity())
					.setTitle(title)
					.setMessage(R.string.limit)
					.setPositiveButton(R.string.pos_button, null)
					.create().show();
		}
	}

	private void showAlertDialog(final int title, int confirmMessage)
	{
		try
		{
			new AlertDialog.Builder(getActivity())
					.setTitle(getString(title))
					.setMessage(getString(confirmMessage))
					.setCancelable(true)
					.setNegativeButton(getString(R.string.dialog_no),
							new DialogInterface.OnClickListener()
							{

								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									dialog.cancel();
								}
							})
					.setPositiveButton(getString(R.string.dialog_yes),
							new DialogInterface.OnClickListener()
							{

								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									switch (title)
									{
										case R.string.save:
											save();
											break;
										case R.string.discard:
											loadCurrentList();
											adapter.notifyDataSetChanged();
											break;
										case R.string.reset:
											loadDefaultList();
											break;
									}
								}
							})
					.create().show();
		}
		catch (Exception x)
		{
			Log.e(getClass().getName(), x.toString());
		}

	}

	private void showEditDialog(final int position, int title)
	{
		final EditText editText = (EditText) LayoutInflater.from(getActivity())
				.inflate(R.layout.edit_dialog, null);
		new AlertDialog.Builder(getActivity())
				.setTitle(title)
				.setView(editText)
				.setPositiveButton(R.string.pos_button, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						final int newPosition = getTag().equals(getString(R.string.add_before)) ? position : position + 1;
						final String text = editText.getText().toString();
						list.add(newPosition, new Activity().setCurrentTitle(text));
						adapter.notifyDataSetChanged();
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

	private void loadDefaultList()
	{
		final boolean isMale = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("gender", true);
		list = ActivityList.getDefault(getActivity(), isMale);
		save();
	}

	private void loadCurrentList()
	{
		list = ActivityList.getCurrent();
		if (list.isEmpty())
		{
			loadDefaultList();
		}
	}

	public void save()
	{
		ApplicationEvaluation.getDatabase().saveList(list);
		isSaved = true;
	}

	@Override
	public void onChange()
	{
		isChanged = true;
		isSaved = false;
	}
}