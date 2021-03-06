package com.skysam.hchirinos.elmejorprecio.Clases;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.skysam.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Collections;
import java.util.HashMap;

public class GuardarDatosUsuario {

    public GuardarDatosUsuario() {
    }


    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void almacenarDatos(String id, String nombre, String email, Context context){

        db.collection(VariablesEstaticas.BD_VENDEDORES).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("InicSesion", "DocumentSnapshot data: " + document.getData());
                        actualizarToken(id, context);
                    } else {
                        generarDatosVendedores(id, nombre, email);
                        generarDatosUsuariChat(id, nombre, email, context);
                        Log.d("InicSesion", "No such document");
                    }
                } else {
                    Log.d("InicSesion", "get failed with ", task.getException());
                }
            }
        });



    }


    private void generarDatosVendedores(String id, String nombre, String email) {

        GeoPoint ubicacion = new GeoPoint(0, 0);

        HashMap<String, Object> info = new HashMap<>();
        info.put(VariablesEstaticas.BD_NOMBRE_VENDEDOR, nombre);
        info.put(VariablesEstaticas.BD_CORREO_VENDEDOR, email);
        info.put(VariablesEstaticas.BD_IMAGEN_VENDEDOR, "");
        info.put(VariablesEstaticas.BD_TELEFONO_VENDEDOR, "");
        info.put(VariablesEstaticas.BD_UBICACION_PREFERIDA, "");
        info.put(VariablesEstaticas.BD_ID_USUARIO, id);
        info.put(VariablesEstaticas.BD_LATITUD_LONGITUD, ubicacion);
        info.put(VariablesEstaticas.BD_PRODUCTOS_ASOCIADOS, Collections.emptyList());


        db.collection(VariablesEstaticas.BD_VENDEDORES).document(id).set(info);

    }

    private void generarDatosUsuariChat(String id, String nombre, String email, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String token = sharedPreferences.getString("token", "");


        HashMap<String, Object> info = new HashMap<>();
        info.put(VariablesEstaticas.BD_NOMBRE_USUARIO, nombre);
        info.put(VariablesEstaticas.BD_EMAIL_USUARIO, email);
        info.put(VariablesEstaticas.BD_IMAGEN_USUARIO, "");
        info.put(VariablesEstaticas.BD_ID_USUARIO, id);
        info.put("token", token);

        db.collection(VariablesEstaticas.BD_USUARIOS_CHAT).document(id).set(info);

    }

    private void actualizarToken(String id, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String token = sharedPreferences.getString("token", "");


        db.collection(VariablesEstaticas.BD_USUARIOS_CHAT).document(id).update("token", token);
    }
}
