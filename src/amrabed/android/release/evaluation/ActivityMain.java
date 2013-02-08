package amrabed.android.release.evaluation;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.joda.time.LocalDate;

import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;

public class ActivityMain extends FragmentActivity implements OnNavigationListener
{
	final static int MY_INDEX = 0;
	List<DatabaseEntry> entries;
	int count;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isFirstTime", true))
		{
			// If first use, get user preferences
			startActivity(new Intent(this, ActivityPreferences.class));
			// Don't come here again :@
			PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("isFirstTime", false).commit();
		}
		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("sync", false))
		{
			getAccount();
		}
		ApplicationEvaluation.db.insert(new DatabaseEntry(DatabaseUpdater.today.getMillis(),0));
		setContentView(R.layout.activity_main);
		entries = ApplicationEvaluation.db.getAllEntries();
		count = entries.size();
		setView();
	}

	void setView()
	{
		// setContentView(R.layout.activity_main);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setSelectedNavigationItem(MY_INDEX);
		actionBar.setListNavigationCallbacks(new ArrayAdapter<CharSequence>(this, R.layout.item_spinner, getResources().getStringArray(R.array.spinner)), this);

		ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));

		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			mViewPager.setCurrentItem(extras.getInt("POS"));
		}
		else
		{
			mViewPager.setCurrentItem(count - 1);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main_options, menu);
		return true;
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("sync", false))
		{
			getAccount();
		}

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		getActionBar().setSelectedNavigationItem(MY_INDEX);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				getActionBar().setSelectedNavigationItem(1);
				return true;
			case R.id.menu_sync:
				AlertDialog.Builder d = new AlertDialog.Builder(this);
				d.setTitle(getString(R.string.sync_dialog));
				d.setMessage(getString(R.string.sync_description));
				d.setCancelable(true);
				d.setNegativeButton(getString(R.string.res_no), new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.cancel();
					}
				});
				d.setPositiveButton(getString(R.string.res_yes), new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						getAccount();
					}
				});
				d.create().show();
				return true;
			case R.id.menu_settings:
				startActivity(new Intent(this, ActivityPreferences.class));
				return true;
			case R.id.menu_guide:
				startActivity(new Intent(this, ActivityGuide.class));
				return true;
			case R.id.menu_help:
				new DialogHelp().show(getSupportFragmentManager(), "help!");
				return true;
			case R.id.menu_about:
				new DialogAbout().show(getSupportFragmentManager(), "About");
				return true;
			case R.id.menu_edit:
				startActivity(new Intent(this, ActivityEdit.class));
				return true;
				// case R.id.menu_exit:
				// finish();
				// android.os.Process.killProcess(android.os.Process.myPid());
				// return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter
	{

		public SectionsPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			FragmentDay fragment = new FragmentDay();
			Bundle args = new Bundle();
			args.putLong(FragmentDay.ARGS, entries.get(position).date);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount()
		{
			return count;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			return new LocalDate(entries.get(position).date).toString("EEE");
		}
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId)
	{
		if (itemPosition != MY_INDEX)
		{
			startActivity(new Intent(this, ActivityProgress.class));
		}
		return true;
	}

	// public static class Synchronizer extends Activity
	// {
	void getAccount()
	{
		try
		{
			credential = GoogleAccountCredential.usingOAuth2(getBaseContext(), DriveScopes.DRIVE);
			String account = PreferenceManager.getDefaultSharedPreferences(this).getString("ACCOUNT", "");
			if (!"".equals(account))
			{
				credential.setSelectedAccountName(account);
				service = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).build();
				sync();
			}
			else
			{
				startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
			}
		}
		catch (Exception e)
		{
			Toast.makeText(getBaseContext(), "Error: Have you set your Google account on device?", Toast.LENGTH_LONG).show();
			Log.e(TAG, e.toString());
		}
	}

	void sync()
	{
		Thread t = new Thread(new Runnable()
		{
			boolean isUpdated = false;
			boolean isSaved = false;

			@Override
			public void run()
			{
				try
				{

					File folder = new File();
					folder.setMimeType("application/vnd.google-apps.folder");
					folder.setTitle(getString(R.string.app_name));
					folder.setEditable(false);

					List<File> files = new ArrayList<File>();
					files.addAll(service.files().list().setQ("title = '" + folder.getTitle() + "'").execute().getItems());
					if (files.isEmpty())
					{
						folder = service.files().insert(folder).execute();
					}
					else
					{
						folder = service.files().update(files.get(0).getId(), folder).execute();
					}

					ParentReference p = new ParentReference();
					p.setId(folder.getId());
					List<ParentReference> l = new ArrayList<ParentReference>();
					l.add(p);

					saveListFile(l);
					saveDatabaseFile(l);

					runOnUiThread(new Runnable()
					{
						// Show result to user
						@Override
						public void run()
						{
							if ((!isUpdated) && (!isSaved))
							{
								Toast.makeText(getBaseContext(), getString(R.string.no_change), Toast.LENGTH_SHORT).show();
							}
							else if (isUpdated)
							{
								Toast.makeText(getBaseContext(), getString(R.string.updating), Toast.LENGTH_SHORT).show();
								restart();
							}
							else if (isSaved)
							{
								Toast.makeText(getBaseContext(), getString(R.string.saved), Toast.LENGTH_SHORT).show();
							}
						}
					});
				} // try
				catch (ConnectException e)
				{
					try
					{
						runOnUiThread(new Runnable()
						{

							@Override
							public void run()
							{
								Toast.makeText(getBaseContext(), "Check Internet Connection", Toast.LENGTH_LONG).show();
							}
						});
					}
					catch (Exception ex)
					{
					}

				}
				catch (UserRecoverableAuthIOException e)
				{
					startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
				}
				catch (Exception e)
				{
					Log.e(TAG, e.toString());
				}
			}

			void saveListFile(List<ParentReference> parents)
			{
				try
				{
					long localLastModify = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong("LAST_LIST_UPDATE", 0);
					FileContent listFileContent = new FileContent("text/plain", new java.io.File(getFilesDir().getAbsoluteFile() + "/" + ActivityEdit.LIST_FILE));
					File listFile = new File();
					listFile.setTitle(ActivityEdit.LIST_FILE);
					listFile.setEditable(false);
					listFile.setDescription(String.valueOf(localLastModify));
					listFile.setParents(parents);

					List<File> files = new ArrayList<File>();
					files.addAll(service.files().list().setQ("title = '" + listFile.getTitle() + "'").execute().getItems());
					if (files.isEmpty())
					{
						isSaved = true;
						listFile = service.files().insert(listFile, listFileContent).execute();
					}
					else
					{
						long remotLastModify = Long.parseLong(files.get(0).getDescription());
						if (localLastModify > remotLastModify)
						{
							isSaved = true;
							// My copy is newer, replace server copy
							listFile = service.files().update(files.get(0).getId(), listFile, listFileContent).execute();
						}
						else if (localLastModify < remotLastModify)
						{
							isUpdated = true;
							listFile = files.get(0);
							// My copy is obsolete, download server's
							if (listFile.getDownloadUrl() != null && listFile.getDownloadUrl().length() > 0)
							{
								InputStream in = service.getRequestFactory().buildGetRequest(new GenericUrl(listFile.getDownloadUrl())).execute().getContent();
								FileOutputStream out = openFileOutput(ActivityEdit.LIST_FILE, Context.MODE_PRIVATE);
								int c;
								while ((c = in.read()) != -1)
								{
									out.write(c);
								}
								out.close();
								PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putLong("LAST_LIST_UPDATE", remotLastModify).commit();
							}
						}
					}
				}
				catch (IOException e)
				{
					Log.e(TAG, e.toString());
				}
			}

			void saveDatabaseFile(List<ParentReference> parents)
			{
				try
				{
					long localLastModify = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong("LAST_UPDATE", 0);
					FileContent dbFileContent = new FileContent("text/plain", new java.io.File(getDatabasePath(Database.DATABASE_NAME).getAbsolutePath()));
					File databaseFile = new File();
					databaseFile.setTitle(Database.DATABASE_NAME);
					databaseFile.setEditable(false);
					databaseFile.setDescription(String.valueOf(localLastModify));// PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong("LAST_UPDATE",
																					// 0)));
					databaseFile.setParents(parents);

					List<File> files = new ArrayList<File>();
					files.addAll(service.files().list().setQ("title = '" + databaseFile.getTitle() + "'").execute().getItems());
					if (files.isEmpty())
					{
						isSaved = true;
						databaseFile = service.files().insert(databaseFile, dbFileContent).execute();
					}
					else
					{
						long remotLastModify = Long.parseLong(files.get(0).getDescription());

						if (localLastModify > remotLastModify)
						{

							// My copy is newer, replace server copy
							databaseFile = service.files().update(files.get(0).getId(), databaseFile, dbFileContent).execute();
							isSaved = true;
						}
						else if (localLastModify < remotLastModify)
						{
							isUpdated = true;
							databaseFile = files.get(0);
							// My copy is obsolete, download server copy
							if (databaseFile.getDownloadUrl() != null && databaseFile.getDownloadUrl().length() > 0)
							{
								InputStream in = service.getRequestFactory().buildGetRequest(new GenericUrl(databaseFile.getDownloadUrl())).execute().getContent();
								FileOutputStream out = new FileOutputStream(getDatabasePath(Database.DATABASE_NAME).getAbsoluteFile());
								int c;
								while ((c = in.read()) != -1)
								{
									out.write(c);
								}
								out.close();
								PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putLong("LAST_UPDATE", remotLastModify).commit();
							}
						}
					}
				}
				catch (IOException e)
				{
					Log.e(TAG, e.toString());
				}
			}

			private void restart()
			{
				finish();
//				android.os.Process.killProcess(android.os.Process.myPid());
				startActivity(new Intent(getBaseContext(), ActivityMain.class));
			}

		});
		t.start();
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
	{
		switch (requestCode)
		{
			case REQUEST_ACCOUNT_PICKER:
				if (resultCode == RESULT_OK && data != null && data.getExtras() != null)
				{
					String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
					if (accountName != null)
					{
						PreferenceManager.getDefaultSharedPreferences(this).edit().putString("ACCOUNT", accountName).commit();
						credential.setSelectedAccountName(accountName);
						service = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).build();
						sync();
					}
				}
				break;
			case REQUEST_AUTHORIZATION:
				if (resultCode == Activity.RESULT_OK)
				{
					sync();
				}
				else
				{
					startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
				}
				break;
		}
	}

	static final String TAG = "SYNC";
	static final int REQUEST_ACCOUNT_PICKER = 1;
	static final int REQUEST_AUTHORIZATION = 2;

	private static Drive service;
	private GoogleAccountCredential credential;

	private boolean isConnected()
	{
		try
		{
			// Connected to WiFi?
			return (((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected());
		}
		catch (Exception e)
		{
			return false;
		}
	}


	public static class DialogHelp extends DialogFragment
	{

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			getDialog().setTitle(R.string.menu_help);
			return inflater.inflate(R.layout.fragment_help, container, false);
		}
	}

	public static class DialogAbout extends DialogFragment
	{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			String message = getString(R.string.about_content1) + "\n\n" + getString(R.string.about_content2);
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle(R.string.menu_about).setMessage(message);
			return builder.create();
		}
	}

}
