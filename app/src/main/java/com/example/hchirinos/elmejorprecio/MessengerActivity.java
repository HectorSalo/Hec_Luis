package com.example.hchirinos.elmejorprecio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;
import androidx.emoji.widget.EmojiEditText;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.FontRequest;
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
import com.example.hchirinos.elmejorprecio.ui.FragmentChat.ConversacionesChatFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class MessengerActivity extends AppCompatActivity {

    private EmojiEditText editTextMsg;
    private String emisor, receptor;
    private Calendar calendario;
    private RecyclerView recyclerView;
    private AdapterMessenger adapterMessenger;
    private ArrayList<ConstructorMessenger> listMsg;
    private FirebaseFirestore db;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);

        setContentView(R.layout.activity_messenger);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView imageView = findViewById(R.id.imageConversacion);
        TextView textView = findViewById(R.id.textViewConversacion);
        editTextMsg = findViewById(R.id.editTextMsg);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        emisor = user.getUid();
        receptor = VariablesGenerales.idChatVendedor;
        calendario = Calendar.getInstance();
        recyclerView = findViewById(R.id.recycler_view_Chat);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
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

        leerMsg();
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
        calendario = Calendar.getInstance();
        Date date = calendario.getTime();

        Map<String, Object> data = new HashMap<>();
        data.put(VariablesEstaticas.BD_ID_EMISOR, emisor);
        data.put(VariablesEstaticas.BD_ID_RECEPTOR, receptor);
        data.put(VariablesEstaticas.BD_MENSAJE_CHAT, mensaje);
        data.put(VariablesEstaticas.BD_FECHA_MENSAJE, date);

        if (!mensaje.isEmpty()) {
            db.collection(VariablesEstaticas.BD_CHATS).document(VariablesEstaticas.BD_CONVERSACIONES_CHAT).collection("Test").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    editTextMsg.setText("");
                    activarConversacion();
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
        recyclerView.setAdapter(adapterMessenger);

        db.collection(VariablesEstaticas.BD_CHATS).document(VariablesEstaticas.BD_CONVERSACIONES_CHAT).collection("Test")
                .orderBy(VariablesEstaticas.BD_FECHA_MENSAJE, Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            System.err.println("Listen failed:" + e);
                            return;
                        }

                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            ConstructorMessenger constructorMessenger = new ConstructorMessenger();
                            int position;
                            String idMsj;
                            String emisorBD;
                            String receptorBD;

                            switch (dc.getType()) {
                                case ADDED:
                                    constructorMessenger.setIdMensaje(dc.getDocument().getId());
                                    constructorMessenger.setEmisor(dc.getDocument().getString(VariablesEstaticas.BD_ID_EMISOR));
                                    constructorMessenger.setReceptor(dc.getDocument().getString(VariablesEstaticas.BD_ID_RECEPTOR));
                                    constructorMessenger.setMensaje(dc.getDocument().getString(VariablesEstaticas.BD_MENSAJE_CHAT));

                                    if((constructorMessenger.getEmisor().equals(emisor) && constructorMessenger.getReceptor().equals(receptor)) || (constructorMessenger.getEmisor().equals(receptor) && constructorMessenger.getReceptor().equals(emisor))) {
                                        listMsg.add(constructorMessenger);
                                    }

                                    Log.d("Msg", "New mensaje: " + dc.getDocument().getData());
                                    break;
                                case MODIFIED:
                                    position = 0;
                                    idMsj = dc.getDocument().getId();
                                    emisorBD = dc.getDocument().getString(VariablesEstaticas.BD_ID_EMISOR);
                                    receptorBD = dc.getDocument().getString(VariablesEstaticas.BD_ID_RECEPTOR);

                                    if((emisorBD.equals(emisor) && receptorBD.equals(receptor)) || (emisorBD.equals(receptor) && receptorBD.equals(emisor))) {

                                        for (int i = 0; i < listMsg.size(); i++) {
                                            if (listMsg.get(i).getIdMensaje().equals(idMsj)) {
                                                position = i;
                                            }
                                        }

                                        constructorMessenger.setIdMensaje(dc.getDocument().getId());
                                        constructorMessenger.setEmisor(dc.getDocument().getString(VariablesEstaticas.BD_ID_EMISOR));
                                        constructorMessenger.setReceptor(dc.getDocument().getString(VariablesEstaticas.BD_ID_RECEPTOR));
                                        constructorMessenger.setMensaje(dc.getDocument().getString(VariablesEstaticas.BD_MENSAJE_CHAT));

                                        listMsg.set(position, constructorMessenger);
                                    }

                                    Log.d("Msg", "Modified mensaje: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    position = 0;
                                    idMsj = dc.getDocument().getId();
                                    emisorBD = dc.getDocument().getString(VariablesEstaticas.BD_ID_EMISOR);
                                    receptorBD = dc.getDocument().getString(VariablesEstaticas.BD_ID_RECEPTOR);

                                    if((emisorBD.equals(emisor) && receptorBD.equals(receptor)) || (emisorBD.equals(receptor) && receptorBD.equals(emisor))) {

                                        for (int i = 0; i < listMsg.size(); i++) {
                                            if (listMsg.get(i).getIdMensaje().equals(idMsj)) {
                                                position = i;
                                            }
                                        }

                                        listMsg.remove(position);
                                    }

                                    Log.d("Msg", "Removed mensaje: " + dc.getDocument().getData());
                                    break;
                            }
                        }
                        adapterMessenger.updateList(listMsg);
                        linearLayoutManager.smoothScrollToPosition(recyclerView, null, adapterMessenger.getItemCount());
                    }

                });
    }

    private void activarConversacion() {
        db.collection(VariablesEstaticas.BD_USUARIOS_CHAT).document(receptor).update("conversacionActiva", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ConversacionesChatFragment conversacionesChatFragment = new ConversacionesChatFragment();
                        //conversacionesChatFragment.onCreateView()
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
