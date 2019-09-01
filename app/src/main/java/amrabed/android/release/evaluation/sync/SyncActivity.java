package amrabed.android.release.evaluation.sync;

import android.app.backup.BackupManager;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.preferences.Preferences;

/**
 * API client to handle connection to Google APIs
 */
public class SyncActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, SyncTask.Listener
{
	private static final String TAG = SyncActivity.class.getName();
	private static final int RESOLVE_CONNECTION_REQUEST = 1000;

	private GoogleApiClient client;
	private BackupManager backupManager;

	@Override
	protected void onStart()
	{
		super.onStart();
		if (isSyncEnabled())
		{
			sync();
		}
	}

	@Override
	protected void onPause()
	{
		if (isSyncEnabled())
		{
			sync();
		}
		getBackupManager().dataChanged();
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == RESOLVE_CONNECTION_REQUEST)
		{
			if (resultCode == RESULT_OK)
			{
				connect();
			}
			else
			{
				Toast.makeText(this, "Failed to connect", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onConnected(@Nullable Bundle bundle)
	{
		Log.i(TAG, "Connection to Google API client completed successfully");
		new SyncTask(this, this).execute(getClient());
	}

	@Override
	public void onConnectionSuspended(int i)
	{
		Log.w(TAG, "Connection to Google API suspended");
		connect();
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult result)
	{
		Log.e(TAG, "Connection to Google API client failed - Error " + result.getErrorCode());
		if (result.hasResolution())
		{
			try
			{
				result.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST);
			} catch (IntentSender.SendIntentException e)
			{
				Log.wtf(TAG, e);
				Toast.makeText(this, "Connection failed", Toast.LENGTH_LONG).show();
			}
		}
		else
		{
			GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
		}
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
		}
		else // if (isSaved)
		{
			Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onSyncError(Exception e)
	{
		Toast.makeText(this, R.string.sync_error, Toast.LENGTH_LONG).show();
	}

	public void sync()
	{
		connect();
	}

	private GoogleApiClient getClient()
	{
		if (client == null)
		{
			client = new GoogleApiClient.Builder(this)
					.addApi(Drive.API)
					.addScope(Drive.SCOPE_APPFOLDER)
//				.addScope(Drive.SCOPE_FILE)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.build();

		}
		return client;
	}

	private BackupManager getBackupManager()
	{
		if (backupManager == null)
		{
			backupManager = new BackupManager(this);
		}
		return backupManager;
	}

	private void connect()
	{
		// Always make sure to use getClient() here
		getClient().connect();
	}

	private boolean isConnected()
	{
		return client != null && client.isConnected();
	}

	protected void disconnect()
	{
		if (isConnected())
		{
			client.disconnect();
		}
	}

	private boolean isSyncEnabled()
	{
		return Preferences.isSyncEnabled(this);
	}

	private void disableSync()
	{
		Preferences.setSyncEnabled(this, false);
	}
}
