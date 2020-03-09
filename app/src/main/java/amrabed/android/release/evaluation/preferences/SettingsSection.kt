package amrabed.android.release.evaluation.preferences

import amrabed.android.release.evaluation.MainActivity
import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.notification.BootReceiver
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsSection : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, x: String?) {
        PreferenceManager.setDefaultValues(activity, R.xml.settings, false)
        addPreferencesFromResource(R.xml.settings)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences, key: String) {
        if ("notification" == key) {
            if (preferences.getBoolean(key, false)) {
                val context = context
                if (context != null) {
                    BootReceiver.enable(context)
                }
            } else {
                BootReceiver.disable(activity)
            }
        } else { // Language
            AlertDialog.Builder(context)
                    .setMessage(R.string.restart)
                    .setPositiveButton(R.string.res_yes) { _: DialogInterface?, _: Int ->
                        // Restart Application
                        val activity: Activity? = activity
                        if (activity != null) {
                            activity.finish()
                            startActivity(Intent(context, MainActivity::class.java))
                        }
                    }
                    .create().show()
        }
    }
}