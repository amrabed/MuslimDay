package amrabed.android.release.evaluation;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsSection extends PreferenceFragment implements OnSharedPreferenceChangeListener
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		PreferenceManager.setDefaultValues(getActivity(), R.xml.settings, false);
		addPreferencesFromResource(R.xml.settings);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		getActivity().setTitle(R.string.menu_settings);
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	public void onSharedPreferenceChanged(SharedPreferences preferences, String key)
	{
//		new BackupManager(this).dataChanged();
		if (key.equals("sync"))
		{
				if (preferences.getBoolean("sync", false))
				{
					((MainActivity) getActivity()).handleSyncRequest();
				}
		}
		else  // Langauge
		{
			// Show confirmation dialog to restart app, so user can see what's going on
			((MainActivity) getActivity()).showDialog(getString(R.string.restart),
					getString(R.string.res_yes), getString(R.string.res_no));
		}
	}
}
