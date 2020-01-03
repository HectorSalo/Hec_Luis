package com.example.hchirinos.elmejorprecio.ui.FragmentChat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hchirinos.elmejorprecio.Adaptadores.AdapterConversacionesChat;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorMessenger;
import com.example.hchirinos.elmejorprecio.MessengerActivity;
import com.example.hchirinos.elmejorprecio.R;
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.example.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.google.errorprone.annotations.Var;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ConversacionesChatFragment extends Fragment {

    public ConversacionesChatFragment() {}

    private static final String ARG_SECTION_NUMBER = "section_number";

    private ArrayList<ConstructorMessenger> listUsuarios;
    private AdapterConversacionesChat adapterConversacionesChat;
    private RecyclerView recyclerViewUsuarios;
    private FirebaseFirestore db;
    private String usuarioActual;
    private ArrayList<String> listaConversaciones;
    private ProgressBar progressBar;

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private UsuariosChatFragment.OnListFragmentInteractionListener mListener;

    public static ConversacionesChatFragment newInstance(int index) {
        ConversacionesChatFragment fragment = new ConversacionesChatFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_conversaciones_chat, container, false);

        recyclerViewUsuarios = root.findViewById(R.id.recyclerViewConversacionesChat);
        progressBar = root.findViewById(R.id.progressBarConversacionesChat);

        listUsuarios = new ArrayList<>();
        adapterConversacionesChat = new AdapterConversacionesChat(listUsuarios, getContext());
        recyclerViewUsuarios.setHasFixedSize(true);
        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewUsuarios.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewUsuarios.setAdapter(adapterConversacionesChat);

        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        usuarioActual = user.getUid();

        cargarConversaciones();
        cargarUsuariosConversaciones();
        selecUsuarioChat();


        return root;
    }


    public void cargarConversaciones() {

        db.collection(VariablesEstaticas.BD_USUARIOS_CHAT).document(usuarioActual).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot snapshot,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Msg", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {

                    listaConversaciones = (ArrayList<String>) snapshot.get(VariablesEstaticas.BD_CONVERSACION_ACTIVA_USUARIO);


                    Log.d("Msg", "Current data: " + listaConversaciones);
                } else {
                    Log.d("Msg", "Current data: null");
                }
            }
        });
    }


    public void cargarUsuariosConversaciones() {
        db.collection(VariablesEstaticas.BD_USUARIOS_CHAT).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.err.println("Listen failed:" + e);
                    return;
                }

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {

                    ConstructorMessenger usuario = new ConstructorMessenger();

                    switch (dc.getType()) {
                        case ADDED:
                            usuario.setReceptor(dc.getDocument().getId());

                                if (listaConversaciones.contains(usuario.getReceptor())) {
                                    usuario.setNombreReceptor(dc.getDocument().getString(VariablesEstaticas.BD_NOMBRE_USUARIO));
                                    usuario.setImagen(dc.getDocument().getString(VariablesEstaticas.BD_IMAGEN_USUARIO));
                                    usuario.setOnLine(dc.getDocument().getBoolean(VariablesEstaticas.BD_STATUS_ONLINE_USUARIO));
                                    usuario.setUltimaConexion(dc.getDocument().getDate(VariablesEstaticas.BD_ULTIMA_CONEXION_USUARIO));
                                }
                                listUsuarios.add(usuario);


                            adapterConversacionesChat.updateList(listUsuarios);

                            //Log.d("Msg", "New mensaje: " + listUsuarios.get(1));
                            break;
                        case MODIFIED:
                            int position = 0;
                            usuario.setReceptor(dc.getDocument().getId());
                            if (!usuario.getReceptor().equals(usuarioActual)) {
                                for (int i = 0; i < listaConversaciones.size(); i++) {
                                    if (listaConversaciones.get(i).equals(usuario.getReceptor())) {

                                        for (int j = 0; j < listUsuarios.size(); j++) {
                                            if (listUsuarios.get(j).getReceptor().equals(dc.getDocument().getId())) {
                                                position = j;
                                            }

                                        usuario.setNombreReceptor(dc.getDocument().getString(VariablesEstaticas.BD_NOMBRE_USUARIO));
                                        usuario.setImagen(dc.getDocument().getString(VariablesEstaticas.BD_IMAGEN_USUARIO));
                                        usuario.setOnLine(dc.getDocument().getBoolean(VariablesEstaticas.BD_STATUS_ONLINE_USUARIO));
                                        usuario.setUltimaConexion(dc.getDocument().getDate(VariablesEstaticas.BD_ULTIMA_CONEXION_USUARIO));



                                                listUsuarios.set(position, usuario);
                                            }

                                    }
                                }

                                adapterConversacionesChat.updateList(listUsuarios);

                                Log.d("Msg", "Modified mensaje: " + dc.getDocument().getData());
                            }
                            break;
                        case REMOVED:
                            Log.d("Msg", "Removed mensaje: " + dc.getDocument().getData());
                            break;
                    }
                }
            }
        });
    }

    private void selecUsuarioChat() {
        adapterConversacionesChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VariablesGenerales.idChatVendedor = listUsuarios.get(recyclerViewUsuarios.getChildAdapterPosition(v)).getReceptor();
                startActivity(new Intent(getContext(), MessengerActivity.class));
            }
        });
    }
}