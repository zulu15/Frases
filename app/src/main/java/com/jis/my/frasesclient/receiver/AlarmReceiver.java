package com.jis.my.frasesclient.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.jis.my.frasesclient.FraseDetailActivity;
import com.jis.my.frasesclient.FrasesActivity;
import com.jis.my.frasesclient.R;
import com.jis.my.frasesclient.model.entity.Frase;
import com.jis.my.frasesclient.model.entity.FraseApiResponse;
import com.jis.my.frasesclient.service.FraseApiService;
import com.jis.my.frasesclient.utils.RetrofitUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class AlarmReceiver extends BroadcastReceiver {

    public static final int ALARM_CODE = 22;

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("Dentro de onReceive");
        createNotificationChannel(context);
        FraseApiService apiService = RetrofitUtil.GetRetrofitInstance().create(FraseApiService.class);
        Call<FraseApiResponse> call = apiService.getFrases();
        call.enqueue(new Callback<FraseApiResponse>() {


            @Override
            public void onResponse(Call<FraseApiResponse> call, Response<FraseApiResponse> response) {
                if (response.isSuccessful()) {
                    FraseApiResponse apiResponse = response.body();
                    int min = 0;
                    int frasesResponseLength =  apiResponse.getFrases().size() > 0 ? apiResponse.getFrases().size() -1 : 0;
                    if(frasesResponseLength == 0) return;
                    int randomIndex = (int)(Math.random()*(frasesResponseLength-min+1)+min);
                    sendNotification(context, apiResponse.getFrases().get(randomIndex));
                }
            }

            @Override
            public void onFailure(Call<FraseApiResponse> call, Throwable t) {
                Timber.d("Error al recibir frases "+t.getMessage());
                Timber.d("Error al recibir frases "+t.getLocalizedMessage());
            }
        });







    }


    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "FRASES_CHANNEL";
            String description = "FRASES_CHANNEL_DESCRIPTION";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private void sendNotification(Context context, Frase frase){
        // Create an explicit intent for an Activity in your app
        Intent toIntent = new Intent(context, FraseDetailActivity.class);
        toIntent.putExtra(FraseDetailActivity.EXTRA_FRASE, frase);
        toIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, toIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL_ID")
                .setSmallIcon(R.drawable.notif_icon)
                .setContentTitle("Hay una nueva frase disponible!")
                .setContentText(frase.getFrase())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(23423, builder.build());
    }
}
