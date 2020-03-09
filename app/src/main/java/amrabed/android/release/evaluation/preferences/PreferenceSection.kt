package amrabed.android.release.evaluation.preferences

import amrabed.android.release.evaluation.R
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.text.TextUtils
import androidx.preference.MultiSelectListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

/**
 * Preferences Fragment
 */
class PreferenceSection : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, x: String?) {
        PreferenceManager.setDefaultValues(activity, R.xml.preferences, false)
        addPreferencesFromResource(R.xml.preferences)
        setSummary(findPreference("fasting"))
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceScreen.sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences, key: String) {
        if (key == "gender") return  // Already handled
        val preference = findPreference<MultiSelectListPreference>(key)
        setSummary(preference)
        if (preference != null) {
            val values = preference.values
            if ("fasting" == key) {
                val value = getByteValue(values)
                preferences.edit().putInt("fastingDays", value).apply()
                if (value and 0x08 == 0) {
                    preferences.edit().remove("ldof").apply()
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
            for (i in entries.indices) {
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