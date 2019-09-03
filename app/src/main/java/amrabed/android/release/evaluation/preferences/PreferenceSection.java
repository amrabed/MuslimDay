package amrabed.android.release.evaluation.preferences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import androidx.preference.MultiSelectListPreference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.Set;

import amrabed.android.release.evaluation.FragmentHelper;
import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.db.DatabaseUpdater;

/**
 * Preferences Fragment
 */
public class PreferenceSection extends PreferenceFragmentCompat
		implements SharedPreferences.OnSharedPreferenceChangeListener
{
	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String x)
	{
		PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
		addPreferencesFromResource(R.xml.preferences);

		setSummary((MultiSelectListPreference) findPreference("memorize"));
		setSummary((MultiSelectListPreference) findPreference("fasting"));
		setSummary((MultiSelectListPreference) findPreference("diet"));
	}

	@Override
	public void onResume()
	{
		super.onResume();
		FragmentHelper.setTitle(R.string.menu_preferences, getActivity());
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause()
	{
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}


	@Override
	public void onSharedPreferenceChanged(SharedPreferences preferences, String key)
	{
		if(key.equals("gender")) return; // Already handled

		if (key.equals("memorizeDays") || key.equals("dietDays") ||
				key.equals("fastingDays"))
		{
			if (getActivity() != null) {
				getActivity().startService(new Intent(getActivity().getApplicationContext(),
						DatabaseUpdater.class));
			}
			return;
		}

		final MultiSelectListPreference preference = (MultiSelectListPreference) findPreference(key);
		setSummary(preference);

		final Set<String> values = preference.getValues();
		switch (key)
		{
			case "memorize":
				preferences.edit().putInt("memorizeDays", getByteValue(values, 1)).apply();
				break;
			case "diet":
				preferences.edit().putInt("dietDays", getByteValue(values, 1)).apply();
				break;
			case "fasting":
				final int value = getByteValue(values, 0);
				preferences.edit().putInt("fastingDays", value).apply();
				if ((value & 0x08) == 0)
				{
					preferences.edit().remove("ldof").apply();
				}
				break;
				default:
		}
	}

	private void setSummary(MultiSelectListPreference preference)
	{
		final CharSequence[] entries = preference.getEntries();
		final CharSequence[] values = preference.getEntryValues();
		final Set<String> selectedValues = preference.getValues();

		final StringBuilder summary = new StringBuilder();
		for (int i = 0; i < entries.length; i++)
		{
			if (selectedValues.contains(values[i].toString()))
			{
				if (!TextUtils.isEmpty(summary))
				{
					summary.append(getString(R.string.comma));
					summary.append(" ");
				}
				summary.append(entries[i]);
			}
		}
		preference.setSummary(summary);
	}

	private int getByteValue(Set<String> selectedValues, int shift)
	{
		int value = 0;
		for (String v : selectedValues)
		{
			value |= 0x01 << (Integer.parseInt(v) - shift);
		}
		return value;
	}
}
