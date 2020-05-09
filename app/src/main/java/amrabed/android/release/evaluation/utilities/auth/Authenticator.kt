package amrabed.android.release.evaluation.utilities.auth

import amrabed.android.release.evaluation.R
import android.content.Context
import android.content.Intent
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

object Authenticator {
    private val auth by lazy { FirebaseAuth.getInstance() }
    val user get() = auth.currentUser

    fun createSignInIntent(): Intent {
        val providers = listOf(AuthUI.IdpConfig.GoogleBuilder().build())
        return AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(providers)
                .setLogo(R.drawable.logo)
                .setTheme(R.style.AppTheme_FullScreen)
                .build()
    }

    fun signOut(context: Context, listener: OnCompleteListener<Void>) {
        AuthUI.getInstance().signOut(context).addOnCompleteListener(listener)
    }
}