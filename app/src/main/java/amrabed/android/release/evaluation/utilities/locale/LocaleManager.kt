package amrabed.android.release.evaluation.utilities.locale

import android.content.Context
import androidx.preference.PreferenceManager
import java.util.*

/**
 * Locale Manager to update the language of application components correctly
 * Based on https://proandroiddev.com/change-language-programmatically-at-runtime-on-android-5e6bc15c758
 */
object LocaleManager {
    fun setLocale(context: Context) {
        val language = PreferenceManager.getDefaultSharedPreferences(context).getString("language", "en")
        val locale = Locale(language!!)
        Locale.setDefault(locale)
        val config = context.resources.configuration.apply { setLocale(locale) }
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}