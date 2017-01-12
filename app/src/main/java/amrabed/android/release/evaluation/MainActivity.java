package amrabed.android.release.evaluation;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import amrabed.android.release.evaluation.api.SyncTask;
import amrabed.android.release.evaluation.app.ApplicationEvaluation;
import amrabed.android.release.evaluation.db.Database;
import amrabed.android.release.evaluation.db.DatabaseEntry;
import amrabed.android.release.evaluation.db.DatabaseUpdater;

/**
 * Main Activity
 *
 * @author AmrAbed
 */
public class MainActivity extends BaseActivity
{
    private static final String TAG = MainActivity.class.getName();

    private static final int READ_CONTACTS_PERMISSION_REQUEST = 0;
    private static final int RESOLVE_CONNECTION_REQUEST = 5;
    private static final int ACCOUNT_PICKER_REQUEST = 1;
    private static final int COMPLETE_AUTHORIZATION_REQUEST = 4;

    private NavigationDrawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if (isSyncEnabled())
        {
            getApiManager();
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = new NavigationDrawer(this).create(savedInstanceState, toolbar);

        Database db = ApplicationEvaluation.getDatabase();
        db.insert(new DatabaseEntry(DatabaseUpdater.today.getMillis(), 0));
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (isSyncEnabled())
        {
            sync();
        }
        getBackupManager().dataChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState)
    {
        super.onSaveInstanceState(outState, outPersistentState);
        drawer.saveState(outState);
    }

    @Override
    public void onBackPressed()
    {
        if (drawer.isOpen())
        {
            drawer.close();
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        // ToDo: Fix error messages
        switch (requestCode)
        {
            case READ_CONTACTS_PERMISSION_REQUEST:
                if (resultCode == RESULT_OK)
                {
                    pickAccount();
                }
                else
                {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            case ACCOUNT_PICKER_REQUEST:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null)
                {
                    final String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (!TextUtils.isEmpty(accountName))
                    {
                        PreferenceManager.getDefaultSharedPreferences(this).edit()
                                .putString(AccountManager.KEY_ACCOUNT_NAME, accountName).apply();
                        getApiManager().getCredential().setSelectedAccountName(accountName);
                    }
                    sync();
                }
                else
                {
                    Toast.makeText(this, "Sync not authorized", Toast.LENGTH_LONG).show();
                }
                break;
            case RESOLVE_CONNECTION_REQUEST:
                if (resultCode == RESULT_OK)
                {
                    getApiManager().getClient().connect();
                }
                else
                {
                    Toast.makeText(this, "Connection Error", Toast.LENGTH_LONG).show();
                }
                break;
            case COMPLETE_AUTHORIZATION_REQUEST:
                if (resultCode == RESULT_OK)
                {
                    // App is authorized, you can go back to sending the API request
                    sync();
                }
                else
                {
                    pickAccount();
                }
                break;
//			case REQUEST_AUTHORIZATION:
//			case REQUEST_AUTHORIZATION_SYNC:
//				if (resultCode == RESULT_OK)
//				{
//					if (REQUEST_AUTHORIZATION == requestCode)
//					{
//						createFolder();
//					}
//					else
//					{
//						sync();
//					}
//				}
//				else
//				{
//					startActivityForResult(credential.newChooseAccountIntent(),
//							ACCOUNT_PICKER_REQUEST);
//				}
//				break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        Log.i(TAG, "Connection to Google API client completed successfully");
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Log.w(TAG, "Connection to Google API suspended");
        getApiManager().connect();
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
            }
            catch (IntentSender.SendIntentException e)
            {
                Log.wtf(TAG, e);
                Toast.makeText(this, "Connection failed", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
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
            restart(false);
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

    private void checkPermissions()
    {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_PERMISSION_REQUEST);
        }
        else
        {
            pickAccount();
        }
    }

    private void pickAccount()
    {
        try
        {

//			final String account = apiManager.getCredential().getSelectedAccountName();
            final String account = PreferenceManager.getDefaultSharedPreferences(this)
                    .getString(AccountManager.KEY_ACCOUNT_NAME, null);
            if (TextUtils.isEmpty(account))
            {
                startActivityForResult(getApiManager().getCredential().newChooseAccountIntent(),
                        ACCOUNT_PICKER_REQUEST);
            }
            else
            {
                sync();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getBaseContext(), "Error: Have you set your Google account on device?",
                    Toast.LENGTH_LONG).show();
            Log.e(TAG, e.toString());
        }
    }

    private void sync()
    {
        getApiManager().getDriveService();
        new SyncTask(this, this).execute(getApiManager().getClient());
    }

    public void handleSyncRequest()
    {
        checkPermissions();
    }


    void restart(boolean shouldShowDialog)
    {
        if (!shouldShowDialog)
        {
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            startActivity(new Intent(MainActivity.this, MainActivity.class));
            return;
        }
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
                        restart(false);
                    }
                })
                .create().show();
    }
}
