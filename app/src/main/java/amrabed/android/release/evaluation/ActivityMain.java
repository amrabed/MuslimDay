//package amrabed.android.release.evaluation;
//
//import android.accounts.AccountManager;
//import android.app.ActionBar.OnNavigationListener;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.net.ConnectivityManager;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import com.google.api.client.extensions.android.http.AndroidHttp;
//import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
//import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
//import com.google.api.client.http.FileContent;
//import com.google.api.client.http.GenericUrl;
//import com.google.api.client.json.gson.GsonFactory;
//import com.google.api.services.drive.Drive;
//import com.google.api.services.drive.DriveScopes;
//import com.google.api.services.drive.model.File;
//import com.google.api.services.drive.model.ParentReference;
//
//import org.joda.time.LocalDate;
//
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.ConnectException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import amrabed.android.release.evaluation.app.ApplicationEvaluation;
//import amrabed.android.release.evaluation.db.Database;
//import amrabed.android.release.evaluation.db.DatabaseEntry;
//import amrabed.android.release.evaluation.db.DatabaseUpdater;
//
//public class ActivityMain extends FragmentActivity implements OnNavigationListener
//{
//	final static int MY_INDEX = 0;
//	List<DatabaseEntry> entries;
//	int count;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState)
//	{
//		super.onCreate(savedInstanceState);
//		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isFirstTime", true))
//		{
//			// If first use, get user preferences
//			startActivity(new Intent(this, SettingsSection.class));
//			// Don't come here again
//			PreferenceManager.getDefaultSharedPreferences(this).edit()
//					.putBoolean("isFirstTime", false).apply();
//		}
//		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("sync", false))
//		{
//			getAccount();
//		}
//		ApplicationEvaluation
//				.getDatabase().insert(new DatabaseEntry(DatabaseUpdater.today.getMillis(),0));
//		setContentView(R.layout.activity_main);
//		entries = ApplicationEvaluation.getDatabase().getAllEntries();
//		count = entries.size();
//		setView();
//	}
//
//	void setView()
//	{
//		// setContentView(R.layout.activity_main);
//
////		final ActionBar actionBar = getActionBar();
////		if(actionBar != null)
////		{
////			actionBar.setDisplayShowHomeEnabled(false);
////			actionBar.setDisplayShowTitleEnabled(false);
////			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
////			actionBar.setSelectedNavigationItem(MY_INDEX);
////			actionBar.setListNavigationCallbacks(
////					new ArrayAdapter<CharSequence>(this, R.layout.item_spinner,
////							getResources().getStringArray(R.array.spinner)), this);
////
////		}
//
//		final ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
//		mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
//
//		Bundle extras = getIntent().getExtras();
//		if (extras != null)
//		{
//			mViewPager.setCurrentItem(extras.getInt("POS"));
//		}
//		else
//		{
//			mViewPager.setCurrentItem(count - 1);
//		}
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu)
//	{
//		getMenuInflater().inflate(R.menu.main_options, menu);
//		return true;
//	}
//
//	@Override
//	protected void onPause()
//	{
//		super.onPause();
//		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("sync", false))
//		{
//			getAccount();
//		}
//	}
//
//	@Override
//	protected void onResume()
//	{
//		super.onResume();
//		getActionBar().setSelectedNavigationItem(MY_INDEX);
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item)
//	{
//		switch (item.getItemId())
//		{
//			case android.R.id.home:
//				getActionBar().setSelectedNavigationItem(1);
//				return true;
//			case R.id.menu_sync:
//				AlertDialog.Builder d = new AlertDialog.Builder(this);
//				d.setTitle(getString(R.string.sync_dialog));
//				d.setMessage(getString(R.string.sync_description));
//				d.setCancelable(true);
//				d.setNegativeButton(getString(R.string.res_no), new DialogInterface.OnClickListener()
//				{
//
//					@Override
//					public void onClick(DialogInterface dialog, int which)
//					{
//						dialog.cancel();
//					}
//				});
//				d.setPositiveButton(getString(R.string.res_yes), new DialogInterface.OnClickListener()
//				{
//
//					@Override
//					public void onClick(DialogInterface dialog, int which)
//					{
//						getAccount();
//					}
//				});
//				d.create().show();
//				return true;
////			case R.id.menu_settings:
////				startActivity(new Intent(this, SettingsSection.class));
////				return true;
////			case R.id.menu_guide:
////				startActivity(new Intent(this, ActivityGuide.class));
////				return true;
////			case R.id.menu_help:
////				new DialogHelp().show(getSupportFragmentManager(), "help!");
////				return true;
////			case R.id.menu_about:
////				new DialogAbout().show(getSupportFragmentManager(), "About");
////				return true;
////			case R.id.menu_edit:
////				startActivity(new Intent(this, EditSection.class));
////				return true;
//				// case R.id.menu_exit:
//				// finish();
//				// android.os.Process.killProcess(android.os.Process.myPid());
//				// return true;
//			default:
//				return super.onOptionsItemSelected(item);
//		}
//	}
//
//	public class SectionsPagerAdapter extends FragmentPagerAdapter
//	{
//
//		public SectionsPagerAdapter(FragmentManager fm)
//		{
//			super(fm);
//		}
//
//		@Override
//		public Fragment getItem(int position)
//		{
////			DaySection fragment = DaySection();
////			Bundle args = new Bundle();
////			args.putLong(DaySection.ARGS, entries.get(position).getDate());
////			fragment.setArguments(args);
//			return null;
//		}
//
//		@Override
//		public int getCount()
//		{
//			return count;
//		}
//
//		@Override
//		public CharSequence getPageTitle(int position)
//		{
//			return new LocalDate(entries.get(position).getDate()).toString("EEE");
//		}
//	}
//
//	@Override
//	public boolean onNavigationItemSelected(int itemPosition, long itemId)
//	{
//		if (itemPosition != MY_INDEX)
//		{
//			startActivity(new Intent(this, ProgressSection.class));
//		}
//		return true;
//	}
//
//	// public static class Synchronizer extends Activity
//	// {
//
//	@Override
//	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
//	{
//		switch (requestCode)
//		{
//			case REQUEST_ACCOUNT_PICKER:
//				if (resultCode == RESULT_OK && data != null && data.getExtras() != null)
//				{
//					String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
//					if (accountName != null)
//					{
//						PreferenceManager.getDefaultSharedPreferences(this).edit().putString("ACCOUNT", accountName).commit();
//						credential.setSelectedAccountName(accountName);
//						service = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).build();
//						sync();
//					}
//				}
//				break;
//			case REQUEST_AUTHORIZATION:
//				if (resultCode == Activity.RESULT_OK)
//				{
//					sync();
//				}
//				else
//				{
//					startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
//				}
//				break;
//		}
//	}
//
//	static final String TAG = "SYNC";
//	static final int REQUEST_ACCOUNT_PICKER = 1;
//	static final int REQUEST_AUTHORIZATION = 2;
//
//	private static Drive service;
//	private GoogleAccountCredential credential;
//
//	private boolean isConnected()
//	{
//		try
//		{
//			// Connected to WiFi?
//			return (((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected());
//		}
//		catch (Exception e)
//		{
//			return false;
//		}
//	}
//
//}
