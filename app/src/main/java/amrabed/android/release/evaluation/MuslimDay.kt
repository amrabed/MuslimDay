package amrabed.android.release.evaluation

import amrabed.android.release.evaluation.utilities.locale.LocaleManager
import android.app.Application
import android.content.res.Configuration

class MuslimDay : Application() {
    override fun onCreate() {
        super.onCreate()
        LocaleManager.setLocale(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleManager.setLocale(this)
    }
}