package com.skysam.hchirinos.elmejorprecio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean temaClaro = sharedPreferences.getBoolean("temaClaro", true);
        if (!temaClaro) {

            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);

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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
    }

    public static class SettingsFragment extends PreferenceFragmentCompat  implements SharedPreferences.OnSharedPreferenceChangeListener{
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            PreferenceScreen preferenceScreen = findPreference("perfil_usuario");

            if (user != null) {
                preferenceScreen.setVisible(true);
            } else {
                preferenceScreen.setVisible(false);
            }
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
                        anularFCM();
                    } else {
                        switchPreferenceCompatNotif.setIcon(R.drawable.ic_noitificacion);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("activarNotif", true);
                        editor.commit();
                        suscribirseFCM();
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
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("temaClaro", true);
                        editor.commit();
                        getActivity().recreate();
                    } else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("temaClaro", false);
                        editor.commit();
                        getActivity().recreate();

                    }
                    break;
            }
        }

        private void suscribirseFCM() {
            FirebaseMessaging.getInstance().subscribeToTopic("notif")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = "Subscripcion exitosa";
                            if (!task.isSuccessful()) {
                                msg = "Failed";
                            }
                            Log.d("suscrito", msg);

                        }
                    });
        }

        private void anularFCM() {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("notif").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    String msg = "Anulacion exitosa";
                    if (!task.isSuccessful()) {
                        msg = "Failed anulacion";
                    }
                    Log.d("anulado", msg);

                }
            });
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