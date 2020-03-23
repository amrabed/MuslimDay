package amrabed.android.release.evaluation

import amrabed.android.release.evaluation.edit.EditActivity
import amrabed.android.release.evaluation.locale.LocaleManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Main Activity
 */
class MainActivity : AppCompatActivity() {
    private var user: FirebaseUser? = null
    private var drawer: NavigationDrawer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        LocaleManager.setLocale(this)
        setContentView(R.layout.main_activity)
        user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivityForResult(createSignInIntent(), SIGN_IN_REQUEST)
        } else {
            showWelcomeMessage()
            updateProfilePicture()
        }
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawer = NavigationDrawer(this).create(savedInstanceState, toolbar)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        drawer!!.saveState(outState)
    }

    override fun onResume() {
        super.onResume()
        LocaleManager.setLocale(this)
    }

    override fun onBackPressed() {
        if (drawer!!.isOpen) {
            drawer!!.close()
        } else {
            drawer!!.onBackStackChanged()
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST) {
            if (resultCode == Activity.RESULT_OK) { // Successfully signed in
                user = FirebaseAuth.getInstance().currentUser
                showWelcomeMessage()
                updateProfilePicture()
            } else {
                Toast.makeText(this, R.string.no_sign_in, Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == EDIT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                recreate()
            }
        }
    }

    private fun showWelcomeMessage() {
        if (user != null) {
            val name = user!!.displayName
            val text = if (name != null) getString(R.string.welcome) + " " + name.split(" ").toTypedArray()[0] else getString(R.string.signed_in)
            if (currentFocus != null) {
                Snackbar.make(currentFocus!!, text, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateProfilePicture() {
        Glide.with(this).load(user!!.photoUrl).into((findViewById<View>(R.id.user) as ImageView))
    }

    fun startEditorActivity() {
        startActivityForResult(Intent(this, EditActivity::class.java), EDIT_REQUEST)
    }

    fun signOut() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            Toast.makeText(this, R.string.no_sign_in, Toast.LENGTH_SHORT).show()
        } else {
            AuthUI.getInstance().signOut(this@MainActivity)
                    .addOnCompleteListener(this) {
                        Toast.makeText(this, R.string.signed_out, Toast.LENGTH_SHORT).show()
                        Glide.with(this@MainActivity).clear((findViewById<View>(R.id.user) as ImageView))
                    }
        }
    }

    companion object {
        const val SIGN_IN_REQUEST = 100
        private const val EDIT_REQUEST = 10
        private fun createSignInIntent(): Intent {
            val providers = listOf(
                    GoogleBuilder().build())
            //                new AuthUI.IdpConfig.AnonymousBuilder().build());
            return AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setLogo(R.drawable.logo)
                    .setTheme(R.style.AppTheme_FullScreen)
                    .build()
        }
    }
}