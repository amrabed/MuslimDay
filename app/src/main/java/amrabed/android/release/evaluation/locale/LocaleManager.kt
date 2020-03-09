package amrabed.android.release.evaluation.locale

import android.content.Context
import android.content.res.Configuration
import androidx.preference.PreferenceManager
import java.util.*

/**
 * Locale Manager to update the language of application components correctly
 * Based on https://proandroiddev.com/change-language-programmatically-at-runtime-on-android-5e6bc15c758
 */
object LocaleManager {
    fun setLocale(context: Context) {
        setLocale(context, Configuration(context.resources.configuration))
    }

    private fun setLocale(context: Context, config: Configuration) {
        val language = PreferenceManager.getDefaultSharedPreferences(context).getString("language", "en")
        if (config.locale.language != language) {
            val resources = context.resources
            val locale = Locale(language)
            Locale.setDefault(locale)
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }

    fun isEnglish(context: Context): Boolean {
        return context.resources.configuration.locale.language == "en"
    }
}