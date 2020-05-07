package amrabed.android.release.evaluation

import amrabed.android.release.evaluation.edit.EditActivity
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.main_activity.*

/**
 * Main Activity
 */
class MainActivity : BaseActivity(), View.OnClickListener {
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            signIn()
        } else {
            showWelcomeMessage()
            updateProfilePicture()
        }

        val navController = findNavController(R.id.fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.assessment, R.id.progress, R.id.guide))
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(this, navController, appBarConfiguration)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        navigation.setupWithNavController(navController)
    }

    override fun setTitle(title: CharSequence?) {
        toolbar.title = title
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit -> {
                startActivityForResult(Intent(this, EditActivity::class.java), EDIT_REQUEST)
                return true
            }
            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
            R.id.help -> {
                startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(getString(R.string.helpWebsite)) })
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST) {
            if (resultCode == Activity.RESULT_OK) { // Successfully signed in
                user = FirebaseAuth.getInstance().currentUser
                showWelcomeMessage()
                updateProfilePicture()
            } else {
                Toast.makeText(this, R.string.notSignedIn, Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == EDIT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                recreate()
            }
        }
    }

    override fun onClick(v: View?) {
        if (user == null) {
            signIn()
        } else {
            AlertDialog.Builder(this)
                    .setMessage(R.string.confirmSignOut)
                    .setPositiveButton(R.string.ok) { _, _ -> signOut() }
                    .setNegativeButton(R.string.cancel) { d, _ -> d.dismiss() }
                    .create()
        }
    }


    private fun signIn() {
        startActivityForResult(createSignInIntent(), SIGN_IN_REQUEST)
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(this@MainActivity)
                .addOnCompleteListener(this) {
                    Toast.makeText(this, R.string.signedOut, Toast.LENGTH_SHORT).show()
                    Snackbar.make(window.decorView.rootView, R.string.signedOut, Snackbar.LENGTH_SHORT).show()
                    Glide.with(this@MainActivity).clear((findViewById<View>(R.id.user) as ImageView))
                }
    }


    private fun showWelcomeMessage() {
        if (user != null) {
            val name = user!!.displayName
            val text = if (name != null) getString(R.string.welcome) + " " + name.split(" ").toTypedArray()[0] else getString(R.string.signedIn)
            if (currentFocus != null) {
                Snackbar.make(currentFocus!!, text, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateProfilePicture() {
        Glide.with(this).load(user?.photoUrl).apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_user)
                .into((findViewById<View>(R.id.user) as ImageView))
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