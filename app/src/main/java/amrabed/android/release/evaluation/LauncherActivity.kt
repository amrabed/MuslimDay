package amrabed.android.release.evaluation

import amrabed.android.release.evaluation.utilities.auth.Authenticator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

class LauncherActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Authenticator.user == null) {
            startActivityForResult(Authenticator.signInIntent, SIGN_IN_REQUEST)
        } else {
            startMainActivity()
        }
        super.onCreate(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST && resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, R.string.notSignedIn, Toast.LENGTH_SHORT).show()
        }
        startMainActivity()
    }

    private fun startMainActivity() {
        finish()
        startActivity(Intent(this, MainActivity::class.java))
//                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
    }

    companion object {
        private const val SIGN_IN_REQUEST = 100
    }
}