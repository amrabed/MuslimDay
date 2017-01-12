package amrabed.android.release.evaluation;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class EditSection extends ListFragment
{
    public static final String LIST_FILE = "list";

    private MyAdapter adapter;
    private List<String> itemList = new ArrayList<>();
    public static final Object fileSyncLock = new Object();
    boolean isChanged = false;
    boolean isUpToDate = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        readItems();
        adapter = new MyAdapter(getActivity(), R.layout.edit_item, itemList);
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getActivity().setTitle(R.string.menu_edit);
        getListView().scrollTo(0, getActivity().getPreferences(0).getInt("Position", 0));
    }

    @Override
    public void onPause()
    {
        getActivity().getPreferences(0).edit().putInt("Position", getListView().getScrollY())
                .apply();
        super.onPause();
    }

    //	@Override
    public void onBackPressed()
    {
        if (isChanged)
        {
            if (!isUpToDate)
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
                                        restart();
                                    }

                                })
                        .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener()
                        {

                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                getActivity().finish();
                            }
                        }).show();
            }
            else
            {
                // Changes made and saved .. restart
                restart();
            }
        }
        else
        {
            getActivity().finish();
        }
    }

    private void restart()
    {
        getActivity().finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id)
    {
//        listView.showContextMenuForChild(view);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.edit_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        try
        {
            isChanged = true;
            final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
//			final View view = info.targetView;
            final Integer position = info.position;

            switch (item.getItemId())
            {
//                case R.id.edit:
//                    showEditDialog(position, R.string.edit);
//                    break;
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
//                case R.id.delete:
//                    // adapter.notifyDataSetChanged();
//                    break;
                // case R.id.move:
                // new
                // AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.move).setMessage(R.string.move_howto).show();
                // break;
                default:
                    return super.onContextItemSelected(item);
            }
            PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                    .edit()
                    .putLong("LAST_LIST_UPDATE", Calendar.getInstance().getTimeInMillis()).apply();
        }
        catch (Exception x)
        {
            Log.e(getClass().getName(), x.toString());
        }
        return true;

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

    private void restoreDefaults()
    {
        try
        {
            isChanged = true;
            itemList.clear();
            final boolean isMale = PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getBoolean("gender", true);
            itemList = Arrays.asList(getResources()
                    .getStringArray(isMale ? R.array.m_activities : R.array.f_activities));
            adapter.notifyDataSetChanged();
            final File file = new File(getActivity().getFilesDir() + "/" + LIST_FILE);
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
                    final FileOutputStream outputStream = getActivity().openFileOutput(LIST_FILE,
                            Context.MODE_PRIVATE);
                    for (String s : itemList)
                    {
                        s += "\n";
                        outputStream.write(s.getBytes());
                    }
                    outputStream.close();
                    isUpToDate = true;
                    PreferenceManager
                            .getDefaultSharedPreferences(getActivity().getApplicationContext())
                            .edit()
                            .putLong("LAST_LIST_UPDATE", Calendar.getInstance().getTimeInMillis())
                            .apply();
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
        new FileReadTask().execute();
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
                                            readItems();
                                            adapter.notifyDataSetChanged();
                                            break;
                                        case R.string.reset:
                                            restoreDefaults();
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
        try
        {
            final EditText editText = (EditText) LayoutInflater.from(getActivity())
                    .inflate(R.layout.edit_dialog, null);
            if (title == R.string.edit)
            {
                editText.setText(itemList.get(position));
            }
            new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setView(editText)
                    .setPositiveButton(R.string.pos_button, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int whichButton)
                        {
                            if (getTag().equals(getString(R.string.edit)))
                            {
                                itemList.remove(position);
                                itemList.add(position, editText.getText().toString());
                            }
                            else if (getTag().equals(getString(R.string.add_before)))
                            {
                                itemList.add(position, editText.getText().toString());
                            }
                            else if (getTag().equals(getString(R.string.add_after)))
                            {
                                itemList.add(position + 1, editText.getText().toString());
                            }
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
        catch (Exception x)
        {
            Log.e(getClass().getName(), x.toString());
        }

    }

    class MyAdapter extends ArrayAdapter<String> implements View.OnDragListener
    {
        MyAdapter(Context context, int layout, List<String> list)
        {
            super(context, layout, list);
        }

        @NonNull
        @Override
        public View getView(final int position, View view, @NonNull ViewGroup parent)
        {
            final String text = getItem(position);

            if (view == null)
            {
                view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_item, parent, false);
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


            viewHolder.text.setText(text);

            view.setOnDragListener(this);
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
            return ((txt.contains(getString(R.string.recite_q))) || (txt
                    .contains(getString(R.string.diet_q))) || (txt
                    .contains(getString(R.string.memorize_q))) || (txt
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
                    return event.getClipDescription()
                            .hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
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
//					CharSequence dragData = item.getText();

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

    private void moveItemUp(int position)
    {
        if (position == 0) return;
        final String s = itemList.get(position - 1);
        itemList.set(position - 1, itemList.get(position));
        itemList.set(position, s);
        adapter.notifyDataSetChanged();
    }

    private void moveItemDown(int position)
    {
        if (position == itemList.size() - 1) return;
        final String s = itemList.get(position + 1);
        itemList.set(position + 1, itemList.get(position));
        itemList.set(position, s);
        adapter.notifyDataSetChanged();
    }

    private void delete(int position)
    {
        itemList.remove(position);
        adapter.notifyDataSetChanged();
    }

    private class FileReadTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids)
        {
            try
            {
                itemList.clear();
                final FileInputStream in = getActivity().openFileInput(LIST_FILE);
                final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null)
                {
                    itemList.add(line);
                }
                isChanged = false;
            }
            catch (FileNotFoundException e)
            {
                final boolean isMale = PreferenceManager
                        .getDefaultSharedPreferences(getActivity().getApplicationContext())
                        .getBoolean("gender", true);
                Collections.addAll(itemList, getResources()
                        .getStringArray(isMale ? R.array.m_activities : R.array.f_activities));
                isChanged = false;
            }
            catch (Exception x)
            {
                Log.e(getClass().getName(), x.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            if (adapter != null)
            {
                adapter.notifyDataSetChanged();
            }
            else
            {
                adapter = new MyAdapter(getActivity(), android.R.layout.simple_list_item_1,
                        itemList);
                setListAdapter(adapter);
            }

        }
    }
}
