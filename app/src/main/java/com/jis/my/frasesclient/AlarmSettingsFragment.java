package com.jis.my.frasesclient;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.jis.my.frasesclient.receiver.AlarmReceiver;

import java.util.Calendar;
import java.util.Random;
public class AlarmSettingsFragment extends PreferenceFragmentCompat {

    private Preference horaAlarma, minutoAlarma, segundoAlarma;
    private EditTextPreference editTextHora,editTextMinuto, editTextSegundo;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);


         horaAlarma = findPreference("pref_alarm_hora");
         editTextHora = (EditTextPreference) horaAlarma;
        editTextHora.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {

            @Override
            public void onBindEditText(@NonNull EditText editTextHora) {
                editTextHora.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
        });

         minutoAlarma = findPreference("pref_alarm_minuto");
         editTextMinuto = (EditTextPreference) minutoAlarma;
        editTextMinuto.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {

            @Override
            public void onBindEditText(@NonNull EditText editTextMinuto) {
                editTextMinuto.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
        });



         segundoAlarma = findPreference("pref_alarm_segundo");
         editTextSegundo = (EditTextPreference) segundoAlarma;
        editTextSegundo.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {

            @Override
            public void onBindEditText(@NonNull EditText editTexSegundo) {
                editTexSegundo.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
        });


        horaAlarma.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Establece el nuevo valor de la preferencia
                editor.putString("pref_alarm_hora", String.valueOf(newValue));

                // Realiza una confirmación inmediata de los cambios
                editor.commit();

                actualizarAlarma();
                preference.setSummary(String.valueOf(newValue));
                return true;
            }
        });

        minutoAlarma.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Establece el nuevo valor de la preferencia
                editor.putString("pref_alarm_minuto", String.valueOf(newValue));

                // Realiza una confirmación inmediata de los cambios
                editor.commit();
                actualizarAlarma();
                preference.setSummary(String.valueOf(newValue));
                return true;
            }
        });

        segundoAlarma.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Establece el nuevo valor de la preferencia
                editor.putString("pref_alarm_segundo", String.valueOf(newValue));

                // Realiza una confirmación inmediata de los cambios
                editor.commit();
                actualizarAlarma();
                preference.setSummary(String.valueOf(newValue));
                return true;
            }
        });

        SwitchPreferenceCompat switchPreferenceEnableAlarm = (SwitchPreferenceCompat) findPreference("pref_alarm_enable");
        switchPreferenceEnableAlarm.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                // Obtén el editor de preferencias
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                boolean isEnable = (boolean) newValue;

                // Establece el nuevo valor de la preferencia
                editor.putBoolean("pref_alarm_enable", isEnable);

                // Realiza una confirmación inmediata de los cambios
                editor.commit();



                // Realiza las acciones necesarias según el estado del switch
                if (!isEnable) {
                   cancelarAlarma();
                }else{
                    setAlarm();
                }
                return true;
            }
        });



        if(!editTextHora.getText().isEmpty()) horaAlarma.setSummary(editTextHora.getText());
        if(!editTextMinuto.getText().isEmpty()) minutoAlarma.setSummary(editTextMinuto.getText());
        if(!editTextSegundo.getText().isEmpty()) segundoAlarma.setSummary(editTextSegundo.getText());

    }

    private void actualizarAlarma(){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isAlarmEnabled = sharedPreferences.getBoolean("pref_alarm_enable",false);
        if(isAlarmEnabled){
            cancelarAlarma();
            setAlarm();

        }
    }



    private int generateRandomNumericId() {
        Random random = new Random();
        int minId = 1000; // Valor mínimo para el ID
        int maxId = 9999; // Valor máximo para el ID
        int range = maxId - minId + 1;

        // Genera un número aleatorio dentro del rango y lo desplaza para que comience desde minId
        int randomId = random.nextInt(range) + minId;

        return randomId;
    }

    private void setAlarm(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int lastAlarmCode = sharedPreferences.getInt("pref_alarm_code", AlarmReceiver.ALARM_CODE);

        // Genera un ID numérico aleatorio
        int randomId = -1;
        do{
            randomId = generateRandomNumericId();
        }while (randomId == lastAlarmCode);

        Intent nuevoIntent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent nuevoPendingIntent = PendingIntent.getBroadcast(getActivity(), randomId, nuevoIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        AlarmManager nuevoAlarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());


        int horaAlarma = Integer.parseInt(sharedPreferences.getString("pref_alarm_hora","0"));
        int minutoAlarma = Integer.parseInt(sharedPreferences.getString("pref_alarm_minuto","0"));
        int segundoAlarma =  Integer.parseInt(sharedPreferences.getString("pref_alarm_segundo","0"));

        System.out.println("Alarma configurada para hora: "+horaAlarma +" minuto: "+minutoAlarma +" segundo "+segundoAlarma);

        calendar.set(Calendar.HOUR_OF_DAY, horaAlarma);
        calendar.set(Calendar.MINUTE,minutoAlarma);
        calendar.set(Calendar.SECOND, segundoAlarma);
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        long intervalMillis = 24 * 60 * 60 * 1000; // Intervalo de 24 horas

        nuevoAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                intervalMillis, nuevoPendingIntent);

        //Actualizamos codigo de alarma
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("pref_alarm_code", randomId);
        editor.apply();
    }


    private void cancelarAlarma(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int lastAlarmCode = sharedPreferences.getInt("pref_alarm_code", AlarmReceiver.ALARM_CODE);

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), lastAlarmCode, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);
        alarmManager.cancel(pendingIntent);
    }
}