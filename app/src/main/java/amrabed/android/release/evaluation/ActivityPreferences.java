package amrabed.android.release.evaluation;

import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import amrabed.android.release.evaluation.app.ApplicationEvaluation;
import amrabed.android.release.evaluation.db.DatabaseUpdater;

@SuppressWarnings("deprecation")
public class ActivityPreferences extends PreferenceActivity implements OnSharedPreferenceChangeListener
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		final ActionBar actionBar = getActionBar();
		if(actionBar != null)
		{
			actionBar.setDisplayShowHomeEnabled(false);
		}

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	public void onSharedPreferenceChanged(SharedPreferences preferences, String key)
	{
//		new BackupManager(this).dataChanged();
		if (key.equals("reciteDays") || key.equals("memorizeDays") || key.equals("dietDays") || key.equals("fastingDays"))
		{
			startService(new Intent(getApplicationContext(), DatabaseUpdater.class));
			return;
		}
		switch (key)
		{
			case "recite":
				preferences.edit().putInt("reciteDays", getValue(key)).apply();
				break;
			case "memorize":
				preferences.edit().putInt("memorizeDays", getValue(key)).apply();
				break;
			case "diet":
				preferences.edit().putInt("dietDays", getValue(key)).apply();
				break;
			case "fasting":
				int v = getValue(key);
				preferences.edit().putInt("fastingDays", v).apply();
				if ((v & 0x08) == 0)
				{
					preferences.edit().remove("ldof").apply();
				}
				break;
			case "sync":
				if (preferences.getBoolean("sync", false))
				{
					AlertDialog.Builder d = new AlertDialog.Builder(this);
					d.setTitle(getString(R.string.sync_dialog));
					d.setMessage(getString(R.string.sync_description));
					d.setCancelable(true);
					d.setNegativeButton(getString(R.string.res_no),
							new DialogInterface.OnClickListener()
							{

								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									dialog.cancel();
								}
							});
					d.setPositiveButton(getString(R.string.res_yes),
							new DialogInterface.OnClickListener()
							{

								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									credential = ApplicationEvaluation.getApiManager()
											.getCredential();
									startActivityForResult(credential.newChooseAccountIntent(),
											REQUEST_ACCOUNT_PICKER);
								}
							});
					d.create().show();
					return;
				}
				break;
		}
		showDialog(getString(R.string.restart), getString(R.string.res_yes), getString(R.string.res_no));
	}

	void showDialog(String message, String yes, String no)
	{
		AlertDialog.Builder d = new AlertDialog.Builder(this);
		d.setMessage(message);
		d.setCancelable(true);
		if (no != null)
		{
			d.setNegativeButton(no, new DialogInterface.OnClickListener()
			{

				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.cancel();
				}
			});
		}
		if (yes != null)
		{
			d.setPositiveButton(yes, new DialogInterface.OnClickListener()
			{

				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					restartApp();
				}
			});
		}
		d.create().show();
	}

	int getValue(String key)
	{
		int val = 0;
		MultiSelectListPreference pref = (MultiSelectListPreference) findPreference(key);
		Set<String> s = pref.getValues();
		String[] values = new String[7];
		s.toArray(values);
		for (int i = 0; i < s.size(); i++)
		{
			int day = Integer.parseInt(values[i]);
			val |= 0x01 << day;
		}
		return val;
	}

	void restartApp()
	{
		// Restart Application
		finish();
		android.os.Process.killProcess(android.os.Process.myPid());
		startActivity(new Intent(getApplicationContext(), ActivityMain.class));
	}

	void createFolder()
	{
		Thread t = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					// if (isOnline())
					{

						File folder = new File();
						folder.setMimeType("application/vnd.google-apps.folder");
						folder.setTitle(getString(R.string.app_name));
						folder.setEditable(false);

						List<File> files = new ArrayList<>();
						files.addAll(service.files().list().setQ("title = '" + folder.getTitle() + "'").execute().getItems());
						if (files.isEmpty())
						{
							folder = service.files().insert(folder).execute();
						}
						runOnUiThread(new Runnable()
						{
							
							@Override
							public void run()
							{
								showDialog(getString(R.string.restart), getString(R.string.res_yes), getString(R.string.res_no));								
							}
						});
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
						PreferenceManager.getDefaultSharedPreferences(this).edit().putString("ACCOUNT", accountName).apply();
						credential.setSelectedAccountName(accountName);
						service = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).build();
						createFolder();
					}
				}
				break;
			case REQUEST_AUTHORIZATION:
				if (resultCode == Activity.RESULT_OK)
				{
					createFolder();
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

}
