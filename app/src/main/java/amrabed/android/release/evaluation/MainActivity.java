package amrabed.android.release.evaluation;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import amrabed.android.release.evaluation.auth.Authenticator;
import amrabed.android.release.evaluation.db.DatabaseUpdater;
import amrabed.android.release.evaluation.edit.EditSection;
import amrabed.android.release.evaluation.locale.LocaleManager;
import amrabed.android.release.evaluation.sync.SyncActivity;

/**
 * Main Activity
 */
public class MainActivity extends SyncActivity {
    private static final int EDIT_REQUEST = 10;
    private FirebaseUser user;
    private NavigationDrawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleManager.setLocale(this);
        setContentView(R.layout.main_activity);
        startService(new Intent(getApplicationContext(), DatabaseUpdater.class));

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Authenticator.signIn(this);
        }

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = new NavigationDrawer(this).create(savedInstanceState, toolbar);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        drawer.saveState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocaleManager.setLocale(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isOpen()) {
            drawer.close();
        } else {
                drawer.onBackStackChanged();
                super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Authenticator.SIGN_IN_REQUEST) {
            if (resultCode == RESULT_OK) { // Successfully signed in
                user = FirebaseAuth.getInstance().getCurrentUser();
                showWelcomeMessage();
            } else {
                Toast.makeText(this, R.string.no_sign_in, Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode == EDIT_REQUEST) {
            if(resultCode == RESULT_OK) {
                recreate();
            }
        }
    }

    private void showWelcomeMessage() {
        if(user != null) {
            final String name = user.getDisplayName();
            final String text = (name != null) ?
                    getString(R.string.welcome) + " " +  name.split(" ")[0] :
                    getString(R.string.signed_in);
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        }
    }

    public void editList() {
        startActivityForResult(new Intent(this, EditSection.class), EDIT_REQUEST);
    }
}
