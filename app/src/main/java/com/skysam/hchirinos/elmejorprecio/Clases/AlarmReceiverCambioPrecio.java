package com.skysam.hchirinos.elmejorprecio.Clases;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.skysam.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AlarmReceiverCambioPrecio extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String idUsuario = user.getUid();

        String idProducto = intent.getStringExtra("idProducto");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_ALMACEN).document(idProducto).update(VariablesEstaticas.BD_CAMBIO_PRECIO, false);
    }
}
