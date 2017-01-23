package amrabed.android.release.evaluation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import amrabed.android.release.evaluation.notification.BootReceiver;

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

    public void onSharedPreferenceChanged(final SharedPreferences preferences, final String key)
    {
//		new BackupManager(this).dataChanged();
        switch (key)
        {
            case "sync":
                if (preferences.getBoolean(key, false))
                {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.sync_dialog))
                            .setMessage(getString(R.string.sync_description))
                            .setCancelable(true)
                            .setNegativeButton(getString(R.string.res_no),
                                    new DialogInterface.OnClickListener()
                                    {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            dialog.cancel();
                                            preferences.edit().putBoolean(key, false).apply();
                                        }
                                    })
                            .setPositiveButton(getString(R.string.res_yes),
                                    new DialogInterface.OnClickListener()
                                    {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            ((MainActivity) getActivity()).sync();
                                        }
                                    })
                            .create().show();
                }
                break;
            case "notification":
                if (preferences.getBoolean(key, false))
                {
                    BootReceiver.enable(getActivity());
                }
                else
                {
                    BootReceiver.disable(getActivity());
                }
                break;

            default: // Language
                ((MainActivity) getActivity()).restart(true);
        }
    }
}
