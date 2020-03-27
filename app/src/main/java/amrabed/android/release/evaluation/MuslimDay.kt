package amrabed.android.release.evaluation

import amrabed.android.release.evaluation.locale.LocaleManager
import amrabed.android.release.evaluation.notification.BootReceiver
import amrabed.android.release.evaluation.notification.Notifier
import android.app.Application
import android.content.res.Configuration
import androidx.preference.PreferenceManager

class MuslimDay : Application() {
    override fun onCreate() {
        super.onCreate()
        LocaleManager.setLocale(this)
        val settings = PreferenceManager.getDefaultSharedPreferences(this)
        if (settings.getBoolean("notification", true)) {
            Notifier.createNotificationChannel(this)
        }
        if (settings.getBoolean(IS_FIRST_RUN, true)) {
            settings.edit().putBoolean(IS_FIRST_RUN, false).apply()
            BootReceiver.toggle(this, true)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleManager.setLocale(this)
    }

    companion object {
        private const val IS_FIRST_RUN = "is first run"
    }
}