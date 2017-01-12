package amrabed.android.release.evaluation;

import android.app.backup.BackupManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import amrabed.android.release.evaluation.api.ApiManager;
import amrabed.android.release.evaluation.api.SyncTask;

/**
 * Base Activity
 */

public abstract class BaseActivity extends AppCompatActivity implements SyncTask.Listener,
        ApiManager.Listener
{
    private ApiManager apiManager;
    private BackupManager backupManager;


    protected BackupManager getBackupManager()
    {
        if (backupManager == null)
        {
            backupManager = new BackupManager(this);
        }
        return backupManager;
    }

    protected ApiManager getApiManager()
    {
        if (apiManager == null)
        {
            apiManager = new ApiManager(this, this);
        }
        return apiManager;
    }

    protected boolean isSyncEnabled()
    {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("sync", false);
    }

}
