package amrabed.android.release.evaluation

import amrabed.android.release.evaluation.notification.Notifier
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
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
                Notifier.toggle(context, preferences.getBoolean(key, false))
            }
            "language" -> {
                if (context != null) {
                    AlertDialog.Builder(context!!)
                            .setMessage(R.string.restart)
                            .setPositiveButton(R.string.res_yes) { _, _ ->
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
                    val values = preference.values
                    if ("fasting" == key) {
                        val value = getByteValue(values)
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
        if (preference != null) {
            val entries = preference.entries
            val values = preference.entryValues
            val selectedValues = preference.values
            val summary = StringBuilder()
            entries.indices.forEach { i ->
                if (selectedValues.contains(values[i].toString())) {
                    if (!TextUtils.isEmpty(summary)) {
                        summary.append(getString(R.string.comma))
                        summary.append(" ")
                    }
                    summary.append(entries[i])
                }
            }
            preference.summary = summary
        }
    }

    private fun getByteValue(selectedValues: Set<String>): Int {
        var value = 0
        for (v in selectedValues) {
            value = value or (0x01 shl v.toInt())
        }
        return value
    }
}


