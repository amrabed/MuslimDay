package amrabed.android.release.evaluation.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

import amrabed.android.release.evaluation.R;

public class Authenticator {
    public static final int SIGN_IN_REQUEST = 100;

    private Authenticator() {}

    public static void signIn(Activity activity) {
        activity.startActivityForResult(Authenticator.createSignInIntent(), SIGN_IN_REQUEST);
    }

    private static Intent createSignInIntent() {
        final List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.AnonymousBuilder().build());

        return AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.logo)
                .setTheme(android.R.style.Theme_Light_NoTitleBar_Fullscreen)
                .build();

    }

    public static void signOut(final Context context) {
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(context, R.string.signed_out, Toast.LENGTH_SHORT).show();
        } else {
            AuthUI.getInstance()
                    .signOut(context)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
        }
    }
}
