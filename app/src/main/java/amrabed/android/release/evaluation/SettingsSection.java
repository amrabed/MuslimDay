package amrabed.android.release.evaluation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.preference.PreferenceFragmentCompat;

import amrabed.android.release.evaluation.notification.BootReceiver;

public class SettingsSection extends PreferenceFragmentCompat implements OnSharedPreferenceChangeListener
{

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String x)
    {
        PreferenceManager.setDefaultValues(getActivity(), R.xml.settings, false);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        FragmentHelper.setTitle(R.string.menu_settings, getActivity());
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
                                            MainActivity activity = (MainActivity) getActivity();
                                            if (activity != null) {
                                                activity.sync();
                                            }
                                        }
                                    })
                            .create().show();
                }
                break;
            case "notification":
                if (preferences.getBoolean(key, false))
                {
                    Context context = getContext();
                    if (context != null) {
                        BootReceiver.enable(context);
                    }
                }
                else
                {
                    BootReceiver.disable(getActivity());
                }
                break;

            default: // Language
                new AlertDialog.Builder(getContext())
                        .setMessage(R.string.restart)
                        .setPositiveButton(R.string.res_yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Restart Application
                                final Activity activity = getActivity();
                                if (activity != null) {
                                    activity.finish();
                                    startActivity(new Intent(getContext(), MainActivity.class));
                                }
                            }
                        })
                        .create().show();

        }
    }
}
