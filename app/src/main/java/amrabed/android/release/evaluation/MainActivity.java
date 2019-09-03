package amrabed.android.release.evaluation;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import amrabed.android.release.evaluation.core.DayEntry;
import amrabed.android.release.evaluation.db.Database;
import amrabed.android.release.evaluation.db.DatabaseUpdater;
import amrabed.android.release.evaluation.edit.OnBackPressedListener;
import amrabed.android.release.evaluation.locale.LocaleManager;
import amrabed.android.release.evaluation.sync.SyncActivity;

/**
 * Main Activity
 */
public class MainActivity extends SyncActivity {
    private NavigationDrawer drawer;

    private boolean isReentry = false;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        drawer.saveState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleManager.setLocale(this);

        setContentView(R.layout.main_activity);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = new NavigationDrawer(this).create(savedInstanceState, toolbar);

        final Database db = ApplicationEvaluation.getDatabase();
        db.insertDay(new DayEntry(DatabaseUpdater.TODAY.getMillis()));

    }

    @Override
    public void onBackPressed() {
        if (drawer.isOpen()) {
            drawer.close();
        } else {
            final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
            if (!isReentry && fragment instanceof OnBackPressedListener) {
                isReentry = true;
                ((OnBackPressedListener) fragment).onBackPressed();
            } else {
                drawer.onBackStackChanged();
                super.onBackPressed();
            }
        }
    }
}
