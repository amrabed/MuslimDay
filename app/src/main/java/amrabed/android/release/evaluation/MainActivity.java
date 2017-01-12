package amrabed.android.release.evaluation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.Toolbar;

import amrabed.android.release.evaluation.sync.SyncActivity;
import amrabed.android.release.evaluation.app.ApplicationEvaluation;
import amrabed.android.release.evaluation.db.Database;
import amrabed.android.release.evaluation.db.DatabaseEntry;
import amrabed.android.release.evaluation.db.DatabaseUpdater;

/**
 * Main Activity
 *
 * @author AmrAbed
 */
public class MainActivity extends SyncActivity
{
    private static final String TAG = MainActivity.class.getName();

    private NavigationDrawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = new NavigationDrawer(this).create(savedInstanceState, toolbar);

        final Database db = ApplicationEvaluation.getDatabase();
        for (int i = 0; i < 4; i++)
        {
            db.insert(new DatabaseEntry(DatabaseUpdater.today.minusDays(i).getMillis(), 0));
        }
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
