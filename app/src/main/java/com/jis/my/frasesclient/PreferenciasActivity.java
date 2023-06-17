package com.jis.my.frasesclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class PreferenciasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferencias);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new AlarmSettingsFragment())
                .commit();








    }
}