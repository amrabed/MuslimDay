package amrabed.android.release.evaluation

import amrabed.android.release.evaluation.utilities.notification.DailyReminder
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.preference.MultiSelectListPreference
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment()).commit()
    }
}

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, key: String?) {
        setPreferencesFromResource(R.xml.settings, key)
        setSummary(findPreference("fasting"))
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences, key: String) {
        when (key) {
            "notification" -> {
                DailyReminder.toggle(requireContext(), preferences.getBoolean(key, false))
            }
            "language" -> {
                if (context != null) {
                    AlertDialog.Builder(requireContext())
                            .setMessage(R.string.restart)
                            .setPositiveButton(R.string.agree) { _, _ ->
                                // Restart Application
                                activity?.finishAffinity()
                                startActivity(Intent(context, MainActivity::class.java))
                            }
                            .create().show()
                }
            }
            "fasting" -> {
                val preference = findPreference<MultiSelectListPreference>(key)
                setSummary(preference)
                if (preference != null) {
                    val value = preference.values.map { it.toInt() }.reduce { result, value -> result or (0x01 shl value) }
                    if ("fasting" == key) {
                        preferences.edit().putInt("fastingDays", value)?.apply()
                        if (value and 0x08 == 0) {
                            preferences.edit()?.remove("ldof")?.apply()
                        }
                    }
                }
            }
        }
    }

    private fun setSummary(preference: MultiSelectListPreference?) {
        preference?.summary = preference?.entries
                ?.filterIndexed { i, _ -> preference.values.contains(i.toString()) }
                .toString().replace(",", getString(R.string.comma))
                .removeSurrounding("[", "]")
    }
}


