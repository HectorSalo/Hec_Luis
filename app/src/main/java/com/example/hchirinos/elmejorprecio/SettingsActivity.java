package com.example.hchirinos.elmejorprecio;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
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


    public static class SettingsFragment extends PreferenceFragmentCompat  implements SharedPreferences.OnSharedPreferenceChangeListener{
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SwitchPreferenceCompat switchPreferenceCompatNotif = findPreference("notificacion");
            SwitchPreferenceCompat switchPreferenceCompatVibrar = findPreference("vibracion");


            switch (key) {
                case "notificacion":
                    boolean activarNotif = sharedPreferences.getBoolean("activarNotif", true);
                    if (activarNotif) {
                        switchPreferenceCompatNotif.setIcon(R.drawable.ic_notif_cancel);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("activarNotif", false);
                        editor.commit();
                    } else {
                        switchPreferenceCompatNotif.setIcon(R.drawable.ic_noitificacion);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("activarNotif", true);
                        editor.commit();
                    }
                    break;

                case "vibracion":
                    boolean activarVibrar = sharedPreferences.getBoolean("activarVibrar", false);
                    if (activarVibrar) {
                        switchPreferenceCompatVibrar.setIcon(R.drawable.ic_vibracion_off);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("activarVibrar", false);
                        editor.commit();
                    } else {
                        switchPreferenceCompatVibrar.setIcon(R.drawable.ic_vibracion_on);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("activarVibrar", true);
                        editor.commit();
                    }
                    break;

                case "tema":
                    String escogenciaTema = sharedPreferences.getString("tema", "Tema Claro");
                    if (escogenciaTema.equals("Tema Claro")) {
                        Toast.makeText(getContext(), "Claro", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("temaClaro", true);
                        editor.commit();
                    } else {
                        Toast.makeText(getContext(), "Oscuro", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("temaClaro", false);
                        editor.commit();
                    }
                    break;
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
    }

}