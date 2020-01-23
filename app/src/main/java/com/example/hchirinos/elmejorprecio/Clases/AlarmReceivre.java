package com.example.hchirinos.elmejorprecio.Clases;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class AlarmReceivre extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_ALMACEN).document("Qgbgzyvbxi5509vdqZ0K").update(VariablesEstaticas.BD_OFERTA_SEMANA, false).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

    }
}
