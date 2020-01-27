package com.example.hchirinos.elmejorprecio.Clases;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class GuardarDatosUsuario {

    public GuardarDatosUsuario() {
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void almacenarDatos(String id, String nombre, String email){

        db.collection(VariablesEstaticas.BD_VENDEDORES).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        actualizarDatos(id, nombre, email);
                        Log.d("InicSesion", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("InicSesion", "No such document");
                    }
                } else {
                    Log.d("InicSesion", "get failed with ", task.getException());
                }
            }
        });



    }

    private void actualizarDatos(String id, String nombre, String email) {
        HashMap<String, Object> info = new HashMap<>();
        info.put(VariablesEstaticas.BD_NOMBRE_VENDEDOR, nombre);
        info.put(VariablesEstaticas.BD_CORREO_VENDEDOR, email);

        db.collection(VariablesEstaticas.BD_VENDEDORES).document(id).set(info);
    }
}
