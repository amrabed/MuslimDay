package amrabed.android.release.evaluation;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.net.ConnectException;
import java.util.Collections;
import java.util.List;

import amrabed.android.release.evaluation.api.CreateFolderTask;
import amrabed.android.release.evaluation.api.SyncTask;
import amrabed.android.release.evaluation.app.ApplicationEvaluation;
import amrabed.android.release.evaluation.db.DatabaseEntry;
import amrabed.android.release.evaluation.db.DatabaseUpdater;

/**
 * Main Activity
 *
 * @author AmrAbed
 */
public class MainActivity extends AppCompatActivity implements
		NavigationView.OnNavigationItemSelectedListener, CreateFolderTask.Listener,
		SyncTask.Listener
{

	private static final int REQUEST_ACCOUNT_PICKER = 1;
	private static final int REQUEST_AUTHORIZATION = 2;
	private static final String TAG = MainActivity.class.getName();
	private static final int REQUEST_AUTHORIZATION_SYNC = 3;

	private GoogleAccountCredential credential;


	private static final String INDEX_KEY = "Navigation index key";

	private DrawerLayout drawer;
	private List<DatabaseEntry> entries;
	private int navigationIndex;
	private NavigationView navigationView;

	private static Drive service;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		if (savedInstanceState != null)
		{
			navigationIndex = savedInstanceState.getInt(INDEX_KEY);
		}
		loadCurrentFragment();

		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		navigationView = (NavigationView) findViewById(R.id.navigation);
		navigationView.setNavigationItemSelectedListener(this);
		navigationView.getMenu().getItem(navigationIndex).setChecked(true);

		ApplicationEvaluation
				.getDatabase().insert(new DatabaseEntry(DatabaseUpdater.today.getMillis(), 0));
		entries = ApplicationEvaluation.getDatabase().getAllEntries();


		ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer,
				toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

		drawer.addDrawerListener(actionBarDrawerToggle);
		actionBarDrawerToggle.syncState();
	}


	private void loadCurrentFragment()
	{
		Fragment fragment;
		switch (navigationIndex)
		{
			case 5:
				fragment = new SettingsSection();
				break;
			case 4:
				fragment = new EditSection();
				break;
			case 3:
				fragment = new PreferenceSection();
				break;
			case 2:
				fragment = new GuideSection();
				break;
			case 1:
				fragment = new PreferenceSection();
				break;
			case 0:
			default:
				fragment = new EvaluationSection();
		}
		getFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
	}

	@Override
	public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState)
	{
		super.onSaveInstanceState(outState, outPersistentState);
		outState.putInt(INDEX_KEY, navigationIndex);
	}

	@Override
	public void onBackPressed()
	{
		if (drawer.isDrawerOpen(navigationView))
		{
			drawer.closeDrawers();
		}
		else
		{
			super.onBackPressed();
		}
	}

	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
	{
		switch (requestCode)
		{
			case REQUEST_ACCOUNT_PICKER:
				if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null)
				{
					String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
					if (accountName != null)
					{
						PreferenceManager.getDefaultSharedPreferences(this).edit()
								.putString("ACCOUNT", accountName).apply();
						credential.setSelectedAccountName(accountName);
						service = new Drive.Builder(AndroidHttp.newCompatibleTransport(),
								new GsonFactory(), credential).build();
						createFolder();
					}
				}
				break;
			case REQUEST_AUTHORIZATION:
			case REQUEST_AUTHORIZATION_SYNC:
				if (resultCode == Activity.RESULT_OK)
				{
					if (REQUEST_AUTHORIZATION == requestCode)
					{
						createFolder();
					}
					else
					{
						sync();
					}
				}
				else
				{
					startActivityForResult(credential.newChooseAccountIntent(),
							REQUEST_ACCOUNT_PICKER);
				}
				break;
		}
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.nav_eval:
				navigationIndex = 0;
				setTitle(R.string.evaluation);
				getFragmentManager().beginTransaction()//.addToBackStack(null)
						.replace(R.id.content, new EvaluationSection()).commit();
				break;
			case R.id.nav_progress:
				navigationIndex = 1;
				setTitle(R.string.menu_progress);
				getFragmentManager().beginTransaction()//.addToBackStack(null)
						.replace(R.id.content, new ProgressSection()).commit();
				break;
			case R.id.nav_guide:
				navigationIndex = 2;
				setTitle(R.string.menu_progress);
				getFragmentManager().beginTransaction()//.addToBackStack(null)
						.replace(R.id.content, new GuideSection()).commit();
				break;
			case R.id.nav_preferences:
				navigationIndex = 3;
				getFragmentManager().beginTransaction()//.addToBackStack(null)
						.replace(R.id.content, new PreferenceSection()).commit();
				break;
			case R.id.nav_edit:
				navigationIndex = 4;
				getFragmentManager().beginTransaction()//.addToBackStack(null)
						.replace(R.id.content, new EditSection()).commit();
				break;
			case R.id.nav_Settings:
				navigationIndex = 5;
				getFragmentManager().beginTransaction()//.addToBackStack(null)
						.replace(R.id.content, new SettingsSection()).commit();
				break;
			case R.id.nav_help:
				new AlertDialog.Builder(this)
						.setTitle("Help")
						.setView(getLayoutInflater().inflate(R.layout.help_dialog, null))
						.create().show();
				break;
			case R.id.nav_about:
				new AlertDialog.Builder(this)
						.setTitle(R.string.menu_about)
						.setMessage(R.string.about_content1)
						.create().show();
				break;
		}
		invalidateOptionsMenu();
		drawer.closeDrawers();
		return true;
	}

	@Override
	public void onCreateFolderError(Exception e)
	{
		if (e instanceof UserRecoverableAuthIOException)
		{
			startActivityForResult(((UserRecoverableAuthIOException) e).getIntent(),
					REQUEST_AUTHORIZATION);
		}
	}

	@Override
	public void onCreateFolderSuccess()
	{
		sync();
	}

	@Override
	public void onSyncSuccess(boolean isSaved, boolean isUpdated)
	{
		if ((!isUpdated) && (!isSaved))
		{
			Toast.makeText(this, R.string.no_change, Toast.LENGTH_SHORT).show();
		}
		else if (isUpdated)
		{
			Toast.makeText(this, R.string.updating, Toast.LENGTH_SHORT).show();
			restart();
		}
		else // if (isSaved)
		{
			Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onSyncError(Exception e)
	{
		if (e instanceof ConnectException)
		{
			Toast.makeText(this, "Check Internet Connection", Toast.LENGTH_LONG).show();
		}
		else if (e instanceof UserRecoverableAuthIOException)
		{
			startActivityForResult(((UserRecoverableAuthIOException) e).getIntent(),
					REQUEST_AUTHORIZATION_SYNC);
		}
	}

	void getAccount()
	{
		try
		{
			credential = GoogleAccountCredential.usingOAuth2(getBaseContext(),
					Collections.singletonList(DriveScopes.DRIVE));
			final String account = PreferenceManager.getDefaultSharedPreferences(this)
					.getString("ACCOUNT", "");
			if (!"".equals(account))
			{
				credential.setSelectedAccountName(account);
				service = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(),
						credential).build();
				sync();
			}
			else
			{
				startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
			}
		}
		catch (Exception e)
		{
			Toast.makeText(getBaseContext(), "Error: Have you set your Google account on device?",
					Toast.LENGTH_LONG).show();
			Log.e(TAG, e.toString());
		}
	}

	void sync()
	{
		new SyncTask(this, this).execute(service);
	}

	public void handleSyncRequest()
	{
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.sync_dialog))
				.setMessage(getString(R.string.sync_description))
				.setCancelable(true)
				.setNegativeButton(getString(R.string.res_no),
						new DialogInterface.OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								dialog.cancel();
							}
						})
				.setPositiveButton(getString(R.string.res_yes),
						new DialogInterface.OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								getAccount();
//								credential = ApplicationEvaluation.getApiManager()
//										.getCredential();
//								startActivityForResult(credential.newChooseAccountIntent(),
//										REQUEST_ACCOUNT_PICKER);
							}
						})
				.create().show();
	}

	void createFolder()
	{
		new CreateFolderTask(this, this).execute(service);
	}

	void restart()
	{
		// Show confirmation dialog to restart app, so user can see what's going on
		new AlertDialog.Builder(this)
				.setMessage(R.string.restart)
				.setCancelable(true)
				.setPositiveButton(R.string.res_yes, new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						// Restart Application
						finish();
						android.os.Process.killProcess(android.os.Process.myPid());
						startActivity(new Intent(getApplicationContext(), MainActivity.class));
					}
				})
				.create().show();
	}
}
