package com.example.hchirinos.elmejorprecio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hchirinos.elmejorprecio.Adaptadores.AdapterMessenger;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorMessenger;
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.example.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MessengerActivity extends AppCompatActivity {

    private EditText editTextMsg;
    private FirebaseUser user;
    private String emisor, receptor;
    private Calendar calendario;
    private RecyclerView recyclerView;
    private AdapterMessenger adapterMessenger;
    private ArrayList<ConstructorMessenger> listMsg;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView imageView = findViewById(R.id.imageConversacion);
        TextView textView = findViewById(R.id.textViewConversacion);
        editTextMsg = findViewById(R.id.editTextMsg);

        user = FirebaseAuth.getInstance().getCurrentUser();
        emisor = user.getUid();
        receptor = VariablesGenerales.idChatVendedor;
        calendario = Calendar.getInstance();
        recyclerView = findViewById(R.id.recycler_view_Chat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        db = FirebaseFirestore.getInstance();

        Glide.with(this).load(VariablesGenerales.imagenChatVendedor).apply(RequestOptions.circleCropTransform()).into(imageView);
        textView.setText(VariablesGenerales.nombreChatVendedor);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean temaClaro = sharedPreferences.getBoolean("temaClaro", true);
        if (!temaClaro) {

            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }

        ImageButton imageButtonEnviarMsg = findViewById(R.id.imageButtonEnviarMsg);
        imageButtonEnviarMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarMsg();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void enviarMsg() {
        String mensaje = editTextMsg.getText().toString();
        Date date = calendario.getTime();

        Map<String, Object> data = new HashMap<>();
        data.put("emisor", emisor);
        data.put("receptor", receptor);
        data.put("mensaje", mensaje);
        data.put("fecha", date);

        if (!mensaje.isEmpty()) {
            db.collection("Chats").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    editTextMsg.setText("");
                    Log.d("Msg", "DocumentSnapshot written with ID: " + documentReference.getId());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("Msg", "Error adding document", e);
                }
            });
        }

    }

    private void leerMsg() {
        listMsg = new ArrayList<>();
        adapterMessenger = new AdapterMessenger(this, listMsg);

        db.collection("Chats")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ConstructorMessenger constructorMessenger = new ConstructorMessenger();
                                Log.d("Msg", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("Msg", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
