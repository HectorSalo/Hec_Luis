package com.example.hchirinos.elmejorprecio;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);


            final SharedPreferences preferences = getActivity().getSharedPreferences("notif", Context.MODE_PRIVATE);
            boolean notifActivo = preferences.getBoolean("activarNotif", true);
            boolean vibrarActivo = preferences.getBoolean("activarVibrar", false);

            final SwitchPreferenceCompat switchPreferenceCompat = findPreference("notificacion");
            SwitchPreferenceCompat switchPreferenceCompatVibrar = findPreference("vibracion");

            if (!notifActivo) {
                switchPreferenceCompat.setIcon(R.drawable.ic_notif_cancel);
            }

            switchPreferenceCompat.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean activo = (boolean) newValue;

                    if (activo) {

                        switchPreferenceCompat.setIcon(R.drawable.ic_noitificacion);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("activarNotif", true);
                        editor.commit();
                    } else {
                        switchPreferenceCompat.setIcon(R.drawable.ic_notif_cancel);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("activarNotif", false);
                        editor.commit();
                    }

                    return true;
                }
            });

            switchPreferenceCompatVibrar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean activo = (boolean) newValue;

                    if (activo) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("activarVibrar", true);
                        editor.apply();
                    } else {

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("activarVibrar", false);
                        editor.apply();
                    }
                    return true;
                }
            });
        }
    }
}