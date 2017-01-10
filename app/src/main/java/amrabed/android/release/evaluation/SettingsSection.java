package amrabed.android.release.evaluation;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import amrabed.android.release.evaluation.utilities.BootReceiver;

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
		switch (key)
		{
			case "sync":
				if (preferences.getBoolean(key, false))
				{
					((MainActivity) getActivity()).handleSyncRequest();
				}
				break;
			case "notification":
				if (preferences.getBoolean(key, false))
				{
					BootReceiver.enable(getActivity());
//					Notifier.scheduleNotifications(getActivity());
				}
				else
				{
					BootReceiver.disable(getActivity());
//					Notifier.cancelNotifications(getActivity());
				}
				break;

			default: // Langauage
				((MainActivity) getActivity()).restart();
		}
	}
}
