package com.example.hchirinos.elmejorprecio.FCM;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.example.hchirinos.elmejorprecio.HomeActivity;
import com.example.hchirinos.elmejorprecio.NotificacionesChat.Token;
import com.example.hchirinos.elmejorprecio.R;
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.graphics.Color.rgb;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String sented = remoteMessage.getData().get("sented");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean activarVibrar = sharedPreferences.getBoolean("activarVibrar", false);

                if (remoteMessage.getNotification() != null) {

                    if (activarVibrar) {
                        Log.d("MSG", "Message Notification Body: " + remoteMessage.getNotification().getBody());
                        showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
                    } else {
                        Log.d("MSG", "Message Notification Body: " + remoteMessage.getNotification().getBody());
                        showNotificationNoVibrar(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
                    }
                }

                if (firebaseUser != null && sented.equals(firebaseUser.getUid())) {
                    showNotificationMessenger(remoteMessage);
                }

    }

    private void showNotificationMessenger(RemoteMessage remoteMessage) {
    }

    private void showNotification(String title, String body) {

            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            //
            String NOTIFICATION_CHANNEL_ID = "MaestrosNotif";


            NotificationCompat.Builder notificationBuilder =  new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            notificationBuilder
                    .setSmallIcon(R.drawable.ic_stat_ic_notification)
                    .setColor(rgb(0, 60, 255))
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{0, 1000, 500, 1000})
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)

                    .setContentInfo("info");

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"Notification", NotificationManager.IMPORTANCE_HIGH);

                notificationChannel.setDescription("Descripcion");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.BLUE);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableLights(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }


            notificationManager.notify(new Random().nextInt(), notificationBuilder.build());

    }


    private void showNotificationNoVibrar(String title, String body) {

        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //
        String NOTIFICATION_CHANNEL_ID = "MaestrosNotif";


        NotificationCompat.Builder notificationBuilder =  new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setColor(rgb(0, 60, 255))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setVibrate(null)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

                .setContentInfo("info");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"Notification", NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("Descripcion");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(null);
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());

    }

    @Override
    public void onNewToken(@NonNull String token) {


        Log.d("Token", "Refreshed token: " + token);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Add a new document with a generated id.

            Token token1 = new Token(token);

            db.collection("Tokens")
                    .add(token1)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("Token", "DocumentSnapshot written with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Token", "Error adding document", e);
                        }
                    });
        }

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

    }

}
