package com.skysam.hchirinos.elmejorprecio.Clases;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.skysam.hchirinos.elmejorprecio.HomeActivity;
import com.skysam.hchirinos.elmejorprecio.R;
import com.skysam.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

import static android.graphics.Color.rgb;

public class AlarmReceiverOferta extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String idUsuario = user.getUid();

        String idProducto = intent.getStringExtra("idProducto");
        String nombre = intent.getStringExtra("nombre");
        String idUsuarioOferta = intent.getStringExtra("idUsuario");
        double precioViejo = intent.getDoubleExtra("precioViejo", 0);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_ALMACEN).document(idProducto).update(VariablesEstaticas.BD_OFERTA_SEMANA, false, VariablesEstaticas.BD_PRECIO_PRODUCTO, precioViejo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (idUsuario != null && idUsuario.equals(idUsuarioOferta) )
                showNotification(context, nombre);
            }
        });

    }


    private void showNotification(Context context, String nombre) {

        Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //
        String NOTIFICATION_CHANNEL_ID = "OfertaFinish";


        NotificationCompat.Builder notificationBuilder =  new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        notificationBuilder
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setColor(rgb(0, 60, 255))
                .setContentTitle("Oferta finalizada")
                .setContentText("La oferta de: " + nombre + " acaba de concluir")
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

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
}
