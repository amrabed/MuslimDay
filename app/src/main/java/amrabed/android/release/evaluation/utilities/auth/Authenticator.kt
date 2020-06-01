package amrabed.android.release.evaluation.utilities.auth

import amrabed.android.release.evaluation.R
import android.content.Context
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

object Authenticator {
    val user get() = FirebaseAuth.getInstance().currentUser

    val signInIntent
        get() = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(listOf(AuthUI.IdpConfig.GoogleBuilder().build(), AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false)
                .setLogo(R.drawable.logo)
                .setTheme(R.style.AppTheme_FullScreen)
                .build()

    fun signOut(context: Context, listener: OnCompleteListener<Void>) = AuthUI.getInstance().signOut(context).addOnCompleteListener(listener)
}