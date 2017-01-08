package amrabed.android.release.evaluation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.MailTo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ActivityEdit extends ListActivity
{
	static final String ARGS = "args";
	public static final String LIST_FILE = "list";
	private static final String TAG = "ActivityEdit";
	static MyAdapter adapter;
	static List<String> itemList = new ArrayList<String>();
	boolean isChanged = false;
	boolean isUpToDate = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayShowHomeEnabled(false);
		registerForContextMenu(getListView());
		readItems();
		adapter = new MyAdapter(this, android.R.layout.simple_list_item_1, itemList);
		setListAdapter(adapter);

//		final SwipeDetector swipeDetector = new SwipeDetector();
//		 getListView().setOnTouchListener(swipeDetector);
//		getListView().setOnItemClickListener(new OnItemClickListener()
//		{
//			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
//			{
//				parent.showContextMenuForChild(v);
//			}
//		});
//		getListView().setOnItemLongClickListener(new OnItemLongClickListener()
//		{
//			@Override
//			public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id)
//			{
//				if (swipeDetector.swipeDetected())
//				{
//					// do the onSwipe action
//					Action action = swipeDetector.getAction();
//					switch (action)
//					{
//					// case LR:
//					// case RL:
//					// itemList.remove(position);
//					// adapter.notifyDataSetChanged();
//					// isChanged = true;
//					// return true;
//						case TB:
//						case BT:
//							return v.startDrag(ClipData.newPlainText(ClipDescription.MIMETYPE_TEXT_PLAIN, ((TextView) v.getTag()).getText()), new View.DragShadowBuilder(v), null, 0);
//						default:
//							showEditDialog(position, R.string.edit);
//							return true;
//					}
//				}
//				else
//				{
//					return false;
//				}
//
//			}
//			// return
//			// v.startDrag(ClipData.newPlainText(ClipDescription.MIMETYPE_TEXT_PLAIN,
//			// ((TextView) v.getTag()).getText()), new
//			// View.DragShadowBuilder(v), null, 0);
//
//		});

	}

	@Override
	public void onResume()
	{
		super.onResume();
		getListView().scrollTo(0, getPreferences(0).getInt("Position", 0));
	}

	@Override
	public void onPause()
	{
		super.onPause();
		getPreferences(0).edit().putInt("Position", getListView().getScrollY());
	}

	@Override
	public void onBackPressed()
	{
		if (isChanged)
		{
			if (!isUpToDate)
			{
				new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("").setMessage(R.string.confirm_save).setPositiveButton(R.string.dialog_yes,
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								save();
								finish();
								android.os.Process.killProcess(android.os.Process.myPid());
								startActivity(new Intent(getBaseContext(), ActivityMain.class));
							}

						}).setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						finish();
					}
				}).show();
			}
			else
			{
				// Changes made and saved .. restart
				finish();
				android.os.Process.killProcess(android.os.Process.myPid());
				startActivity(new Intent(getBaseContext(), ActivityMain.class));
			}
		}
		else
		{
			finish();
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		l.showContextMenuForChild(v);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.edit_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		try
		{
			isChanged = true;
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			View view = info.targetView;
			Integer position = info.position;

			switch (item.getItemId())
			{
				case R.id.edit:
					showEditDialog(position, R.string.edit);
					break;
				case R.id.add_before:
				// if (itemList.size() < (Long.SIZE / 2))
				{
					showEditDialog(position, R.string.add_before);
				}
					// else
					// {
					// new
					// AlertDialog.Builder(this).setTitle(R.string.add_before).setMessage(R.string.limit).setPositiveButton(R.string.back,
					// null).show();
					// }
					break;
				case R.id.add_after:
				// if (itemList.size() < (Long.SIZE / 2))
				{
					showEditDialog(position, R.string.add_after);
				}
					// else
					// {
					// new
					// AlertDialog.Builder(this).setTitle(R.string.add_after).setMessage(R.string.limit).setPositiveButton(R.string.back,
					// null).show();
					// }
					break;
//				case R.id.duplicate:
//					itemList.add(position, itemList.get(position));
//					adapter.add(adapter.getItem(position));
//					break;
				case R.id.delete:
					itemList.remove(position);
					adapter.remove(adapter.getItem(position));
					// adapter.notifyDataSetChanged();
					break;
				// case R.id.move:
				// new
				// AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.move).setMessage(R.string.move_howto).show();
				// break;
				default:
					return super.onContextItemSelected(item);
			}
			PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putLong("LAST_LIST_UPDATE", Calendar.getInstance().getTimeInMillis()).commit();
		}
		catch (Exception x)
		{
			Log.e(getClass().getName(), x.toString());
		}
		return true;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.edit_options, menu);
		return true;
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

	private void restoreDefaults()
	{
		try
		{
			isChanged = true;
			itemList.clear();
			boolean isMale = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("gender", true);
			String items[] = getResources().getStringArray(isMale ? R.array.m_activities : R.array.f_activities);
			for (int i = 0; i < items.length; i++)
			{
				itemList.add(items[i]);
			}
			adapter.notifyDataSetChanged();
			File file = new File(getFilesDir() + "/" + LIST_FILE);
			file.delete();
		}
		catch (Exception x)
		{
			Log.e(getClass().getName(), x.toString());
		}
	}

	private void save()
	{
		Thread t = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					FileOutputStream outputStream = openFileOutput(LIST_FILE, Context.MODE_PRIVATE);
					for (String s : itemList)
					{
						s += "\n";
						outputStream.write(s.getBytes());
					}
					outputStream.close();
					isUpToDate = true;
					PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putLong("LAST_LIST_UPDATE", Calendar.getInstance().getTimeInMillis()).commit();
				}
				catch (Exception x)
				{
					Log.e(getClass().getName(), x.toString());
				}
			}
		});
		t.start();
	}

	private void readItems()
	{
		final Thread t = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					itemList.clear();
					FileInputStream in = openFileInput(LIST_FILE);
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					String line = null;
					while ((line = reader.readLine()) != null)
					{
						itemList.add(line);
					}
					isChanged = false;
				}
				catch (FileNotFoundException e)
				{
					boolean isMale = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("gender", true);

					String items[] = getResources().getStringArray(isMale ? R.array.m_activities : R.array.f_activities);
					for (int i = 0; i < items.length; i++)
					{
						itemList.add(items[i]);
					}
					adapter.notifyDataSetChanged();
					isChanged = false;
				}
				catch (Exception x)
				{
					Log.e(getClass().getName(), x.toString());
				}
			}
		});
		t.start();
	}

	private void showAlertDialog(final int title, int confirmMessage)
	{
		try
		{
			AlertDialog.Builder d = new AlertDialog.Builder(this);
			d.setTitle(getString(title));
			d.setMessage(getString(confirmMessage));
			d.setCancelable(true);
			d.setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener()
			{

				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.cancel();
				}
			});
			d.setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener()
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
							readItems();
							adapter.notifyDataSetChanged();
							break;
						case R.string.reset:
							restoreDefaults();
							break;
					}
				}
			});
			d.create().show();
		}
		catch (Exception x)
		{
			Log.e(getClass().getName(), x.toString());
		}

	}

	private void showEditDialog(int position, int res)
	{
		try
		{
			Bundle args = new Bundle();
			args.putInt("Position", position);
			DialogEdit dialog = new DialogEdit();
			dialog.setArguments(args);
			dialog.show(getFragmentManager(), getString(res));
			adapter.notifyDataSetChanged();
		}
		catch (Exception x)
		{
			Log.e(getClass().getName(), x.toString());
		}

	}

	public static class DialogEdit extends DialogFragment
	{

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			final int position = getArguments().getInt("Position");
			LayoutInflater factory = LayoutInflater.from(getActivity());
			final View textEntryView = factory.inflate(R.layout.fragment_edit, null);
			final EditText et = (EditText) textEntryView.findViewById(R.id.username_edit);
			if (getTag().equals(getString(R.string.edit)))
			{
				et.setText(itemList.get(position));
			}
			return new AlertDialog.Builder(getActivity()).setTitle(getTag()).setView(textEntryView).setPositiveButton(R.string.pos_button, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int whichButton)
				{
					if (getTag().equals(getString(R.string.edit)))
					{
						itemList.remove(position);
						itemList.add(position, et.getText().toString());
					}
					else if (getTag().equals(getString(R.string.add_before)))
					{
						itemList.add(position, et.getText().toString());
					}
					else if (getTag().equals(getString(R.string.add_after)))
					{
						itemList.add(position + 1, et.getText().toString());
					}
				}
			}).setNegativeButton(R.string.neg_button, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int whichButton)
				{
					dialog.dismiss();
				}
			}).create();
		}
	}

	class MyAdapter extends ArrayAdapter<String> implements View.OnDragListener
	{
		public MyAdapter(Context context, int layout, List<String> list)
		{
			super(context, layout, list);
		}

		@Override
		public View getView(int position, View view, ViewGroup parent)
		{
			TextView tv;
			String txt = itemList.get(position);
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(android.R.layout.simple_list_item_activated_1, null);
			tv = (TextView) view.findViewById(android.R.id.text1);
			view.setTag(tv);
			tv.setText(txt);
			if (!isEnabled(position))
			{
				view.setBackgroundColor(Color.GRAY);
			}
			tv.setOnDragListener(this);
			// view.setOnLongClickListener(new OnLongClickListener()
			// {
			//
			// @Override
			// public boolean onLongClick(View v)
			// {
			// return
			// v.startDrag(ClipData.newPlainText(ClipDescription.MIMETYPE_TEXT_PLAIN,
			// ((TextView) v.getTag()).getText()), new
			// View.DragShadowBuilder(v), null, 0);
			// }
			// });
			return view;
		}

		private boolean isExcluded(String txt)
		{
			return ((txt.contains(getString(R.string.recite_q))) || (txt.contains(getString(R.string.diet_q))) || (txt.contains(getString(R.string.memorize_q))) || (txt
					.contains(getString(R.string.fasting_q))));
		}

		@Override
		public boolean areAllItemsEnabled()
		{
			return false;
		}

		@Override
		public boolean isEnabled(int position)
		{
			return !isExcluded(itemList.get(position));
		}

		@Override
		public boolean onDrag(View v, DragEvent event)
		{
			final int action = event.getAction();
			switch (action)
			{
				case DragEvent.ACTION_DRAG_STARTED:
					if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
					{
						return true;
					}
					else
					{
						return false;
					}
				case DragEvent.ACTION_DRAG_ENTERED:
					return true;
				case DragEvent.ACTION_DRAG_LOCATION:
					return true;
				case DragEvent.ACTION_DRAG_EXITED:
					// itemList.remove(getPosition((int)event.getX(),(int)event.getY()));
					// adapter.notifyDataSetChanged();
					return true;
				case DragEvent.ACTION_DROP:
					ClipData.Item item = event.getClipData().getItemAt(0);
					CharSequence dragData = item.getText();

					int position = getPosition((int) v.getX(), (int) v.getY());

					itemList.add(position, (String) item.getText());
					adapter.notifyDataSetChanged();
					isChanged = true;

					return true;
				case DragEvent.ACTION_DRAG_ENDED:

					return true;
				default:
					return false;
			}
		}

		int getPosition(int x, int y)
		{
			for (int i = 0; i < getListView().getChildCount(); i++)
			{
				Rect r = new Rect();
				getListView().getChildAt(i).getHitRect(r);
				if (r.contains(x, y))
				{
					return i;
				}
			}
			return getListView().getChildCount();
		}

	}

	static class SwipeDetector implements View.OnTouchListener
	{

		public static enum Action
		{
			LR, // Left to Right
			RL, // Right to Left
			TB, // Top to bottom
			BT, // Bottom to Top
			None // when no action was detected
		}

		private static final String logTag = "SwipeDetector";
		private static final int MIN_DISTANCE = 100;
		private float downX, downY, upX, upY;
		private Action mSwipeDetected = Action.None;

		public boolean swipeDetected()
		{
			return mSwipeDetected != Action.None;
		}

		public Action getAction()
		{
			return mSwipeDetected;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			switch (event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
				{
					downX = event.getX();
					downY = event.getY();
					mSwipeDetected = Action.None;
					return false; // allow other events like Click to be
					// processed
				}
				case MotionEvent.ACTION_MOVE:
				{
					upX = event.getX();
					upY = event.getY();

					float deltaX = downX - upX;
					float deltaY = downY - upY;

					// horizontal swipe detection
					if (Math.abs(deltaX) > 40)
					{
						// left or right
						if (deltaX < 0)
						{
							mSwipeDetected = Action.LR;
							return true;
						}
						if (deltaX > 0)
						{
							mSwipeDetected = Action.RL;
							return true;
						}
					}
					else

					// vertical swipe detection
					if (Math.abs(deltaY) > 10)
					{
						// top or down
						if (deltaY < 0)
						{
							mSwipeDetected = Action.TB;
							return false;
						}
						if (deltaY > 0)
						{
							mSwipeDetected = Action.BT;
							return false;
						}
					}
					return true;
				}
			}
			return false;
		}
	}
}
