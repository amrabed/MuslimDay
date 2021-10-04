package amrabed.android.release.evaluation

import amrabed.android.release.evaluation.databinding.MainActivityBinding
import amrabed.android.release.evaluation.utilities.auth.Authenticator
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar

/**
 * Main Activity
 */
class MainActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: MainActivityBinding

    private val navController by lazy {
        findNavController(R.id.fragment).apply {
            addOnDestinationChangedListener { _, destination, _ ->
                binding.toolbar.visibility =
                    if (destination.id == R.id.taskEditor) View.GONE else View.VISIBLE
                binding.navigation.visibility =
                    if (destination.id == R.id.taskEditor) View.GONE else View.VISIBLE
                binding.user.visibility =
                    if (destination.id != R.id.assessment) View.GONE else View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Authenticator.user != null) {
            Glide.with(this).load(Authenticator.user?.photoUrl)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_user).into(findViewById(R.id.user))
        }

        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.assessment, R.id.progress, R.id.guide))
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(this, navController, appBarConfiguration)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.navigation.setupWithNavController(navController)
    }

    override fun setTitle(title: CharSequence?) {
        binding.toolbar.title = title
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.listEditor -> {
                startActivityForResult(Intent(this, EditActivity::class.java), EDIT_REQUEST)
                return true
            }
            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
            R.id.help -> {
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(getString(R.string.helpWebsite))
                })
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        // Profile picture clicked -> sign out if signed in
        if (Authenticator.user != null) {
            AlertDialog.Builder(this).setMessage(R.string.confirmSignOut)
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes) { _, _ ->
                    Authenticator.signOut(this) {
                        Glide.with(this).clear(findViewById<ImageView>(R.id.user))
                        Snackbar.make(
                            window.decorView.rootView,
                            R.string.signedOut,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
                .create().show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_REQUEST && resultCode == Activity.RESULT_OK) {
            recreate()
        }
    }

    companion object {
        private const val EDIT_REQUEST = 10
    }
}