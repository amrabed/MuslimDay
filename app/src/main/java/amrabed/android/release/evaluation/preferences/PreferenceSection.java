package amrabed.android.release.evaluation.preferences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.Set;

import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.db.DatabaseUpdater;

/**
 * Preferences Fragment
 *
 * @author AmrAbed
 */
public class PreferenceSection extends PreferenceFragment
		implements SharedPreferences.OnSharedPreferenceChangeListener
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		getActivity().setTitle(R.string.menu_preferences);
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
		if (key.equals("gender")) return; // Already handled

		final MultiSelectListPreference preference = (MultiSelectListPreference) findPreference(key);
//		setSummary(preference);

		if (key.equals("reciteDays") || key.equals("memorizeDays") || key.equals("dietDays") ||
				key.equals("fastingDays"))
		{
			getActivity().startService(new Intent(getActivity().getApplicationContext(),
					DatabaseUpdater.class));
			return;
		}

		final Set<String> values = preference.getValues();
		switch (key)
		{
			case "recite":
				preferences.edit().putInt("reciteDays", getByteValue(values, 1)).apply();
				break;
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
		}
	}

	private void setSummary(MultiSelectListPreference preference)
	{
		final CharSequence[] entries = preference.getEntries();
		final CharSequence[] values = preference.getEntryValues();
		final Set<String> selectedValues = preference.getValues();

		String summary = "";
		for (int i = 0; i < entries.length; i++)
		{
			if (selectedValues.contains(values[i].toString()))
			{
				if (!TextUtils.isEmpty(summary))
				{
					summary += ", ";
				}
				summary += entries[i];
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
