package com.example.hchirinos.elmejorprecio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.widget.EmojiEditText;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hchirinos.elmejorprecio.Adaptadores.AdapterMessenger;
import com.example.hchirinos.elmejorprecio.Clases.UsuarioEnLinea;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorMessenger;
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.example.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
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
    private UsuarioEnLinea usuarioEnLinea;
    private ImageView imagenUsuario;
    private TextView nombreUsuario, statusUsuario;

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

        imagenUsuario = findViewById(R.id.imageConversacion);
        nombreUsuario = findViewById(R.id.textViewNombreUsuario);
        statusUsuario = findViewById(R.id.textViewStatusUsuario);
        editTextMsg = findViewById(R.id.editTextMsg);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        emisor = user.getUid();
        calendario = Calendar.getInstance();
        recyclerView = findViewById(R.id.recycler_view_Chat);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        usuarioEnLinea = new UsuarioEnLinea();


        setVolumeControlStream(AudioManager.STREAM_ALARM);

        if (VariablesGenerales.idChatVendedor == null || VariablesGenerales.idChatVendedor.isEmpty()) {
            Intent intent = new Intent();
            receptor = intent.getStringExtra(VariablesEstaticas.BD_ID_EMISOR);
        } else {
            receptor = VariablesGenerales.idChatVendedor;
        }

        db = FirebaseFirestore.getInstance();

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

        cargarPerfilUsuario();
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

        Map<String, Object> dataEmisor = new HashMap<>();
        dataEmisor.put(VariablesEstaticas.BD_ID_EMISOR, emisor);
        dataEmisor.put(VariablesEstaticas.BD_ID_RECEPTOR, receptor);
        dataEmisor.put(VariablesEstaticas.BD_MENSAJE_CHAT, mensaje);
        dataEmisor.put(VariablesEstaticas.BD_FECHA_MENSAJE, date);
        dataEmisor.put(VariablesEstaticas.BD_STATUS_MENSAJE, "Enviando");

        if (!mensaje.isEmpty()) {
            db.collection(VariablesEstaticas.BD_CHATS).document(VariablesEstaticas.BD_CONVERSACIONES_CHAT).collection(emisor).add(dataEmisor).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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


        Map<String, Object> dataReceptor = new HashMap<>();
        dataReceptor.put(VariablesEstaticas.BD_ID_EMISOR, emisor);
        dataReceptor.put(VariablesEstaticas.BD_ID_RECEPTOR, receptor);
        dataReceptor.put(VariablesEstaticas.BD_MENSAJE_CHAT, mensaje);
        dataReceptor.put(VariablesEstaticas.BD_FECHA_MENSAJE, date);
        dataReceptor.put(VariablesEstaticas.BD_STATUS_MENSAJE, "Recibiendo");

        if (!mensaje.isEmpty()) {
            db.collection(VariablesEstaticas.BD_CHATS).document(VariablesEstaticas.BD_CONVERSACIONES_CHAT).collection(receptor).add(dataReceptor).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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
        recyclerView.setAdapter(adapterMessenger);

        db.collection(VariablesEstaticas.BD_CHATS).document(VariablesEstaticas.BD_CONVERSACIONES_CHAT).collection(emisor)
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
                                    constructorMessenger.setFechaEnvio(dc.getDocument().getDate(VariablesEstaticas.BD_FECHA_MENSAJE));

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
                                        constructorMessenger.setFechaEnvio(dc.getDocument().getDate(VariablesEstaticas.BD_FECHA_MENSAJE));

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


    private void cargarPerfilUsuario() {
        db.collection(VariablesEstaticas.BD_USUARIOS_CHAT).document(receptor).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Msg", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    String imagen = snapshot.getString(VariablesEstaticas.BD_IMAGEN_USUARIO);
                    String nombre = snapshot.getString(VariablesEstaticas.BD_NOMBRE_USUARIO);
                    boolean onLine = snapshot.getBoolean(VariablesEstaticas.BD_STATUS_ONLINE_USUARIO);
                    Date fechaConexion = snapshot.getDate(VariablesEstaticas.BD_ULTIMA_CONEXION_USUARIO);
                    String fechaS = new SimpleDateFormat("EEE d MMM h:mm a").format(fechaConexion);

                    if (imagen != null){
                        if (!imagen.isEmpty()) {
                            Glide.with(getApplicationContext()).load(imagen).apply(RequestOptions.circleCropTransform()).into(imagenUsuario);
                        } else {
                            imagenUsuario.setImageResource(R.mipmap.ic_usuario_sin_imagen);
                        }
                    }

                    nombreUsuario.setText(nombre);

                    if (onLine) {
                        statusUsuario.setText("En línea");
                    } else {
                        statusUsuario.setText(fechaS);
                    }


                    Log.d("Msg", "Current data: " + snapshot.getData());
                } else {
                    Log.d("Msg", "Current data: null");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        calendario = Calendar.getInstance();
        Date date = calendario.getTime();
        usuarioEnLinea.modificarStatus(true, date, emisor);
    }

    @Override
    protected void onPause() {
        super.onPause();
        calendario = Calendar.getInstance();
        Date date = calendario.getTime();
        usuarioEnLinea.modificarStatus(false, date, emisor);
    }

}
