package amrabed.android.release.evaluation.locale;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import java.util.Locale;

/**
 * Locale Manager to update the language of application components correctly
 * Based on https://proandroiddev.com/change-language-programmatically-at-runtime-on-android-5e6bc15c758
 */
public class LocaleManager {
    public static void setLocale(Context context) {
        setLocale(context, new Configuration(context.getResources().getConfiguration()));
    }

    private static void setLocale(Context context, Configuration config) {
        final String language = PreferenceManager.getDefaultSharedPreferences(context).getString("language", "en");
        if (!config.locale.getLanguage().equals(language)) {
            final Resources resources = context.getResources();
            final Locale locale = new Locale(language);
            Locale.setDefault(locale);
            config.locale = locale;
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }
    }

    public static boolean isEnglish(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage().equals("en");
    }
}