package com.skysam.hchirinos.elmejorprecio.Clases;

import android.util.Log;

import androidx.annotation.NonNull;

import com.skysam.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class UsuarioEnLinea {

    public UsuarioEnLinea() {
    }

    public void modificarStatus (boolean isOnLine, Date fecha, String usuario) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_USUARIOS_CHAT).document(usuario).update(VariablesEstaticas.BD_STATUS_ONLINE_USUARIO, isOnLine, VariablesEstaticas.BD_ULTIMA_CONEXION_USUARIO, fecha)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Msg", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Msg", "Error updating document", e);
                    }
                });
    }
}
