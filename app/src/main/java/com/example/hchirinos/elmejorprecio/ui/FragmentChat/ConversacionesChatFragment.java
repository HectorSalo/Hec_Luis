package com.example.hchirinos.elmejorprecio.ui.FragmentChat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ConversacionesChatFragment extends Fragment implements InterfaceRecyclerViewConversaciones {

    public ConversacionesChatFragment() {}

    private static final String ARG_SECTION_NUMBER = "section_number";

    private ArrayList<ConstructorMessenger> listUsuarios;
    private AdapterConversacionesChat adapterConversacionesChat;
    private RecyclerView recyclerViewUsuarios;
    private FirebaseFirestore db;
    private String usuarioActual;
    private ArrayList<String> listaConversaciones, listaConversacionesBorrar;
    private ProgressBar progressBar;
    private int firstTime;
    private Activity activity;
    private View root;
    private Snackbar snackbar;

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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            activity = (Activity) context;
        }
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
        root = inflater.inflate(R.layout.fragment_conversaciones_chat, container, false);

        recyclerViewUsuarios = root.findViewById(R.id.recyclerViewConversacionesChat);
        progressBar = root.findViewById(R.id.progressBarConversacionesChat);

        listUsuarios = new ArrayList<>();
        listaConversacionesBorrar = new ArrayList<>();
        listaConversaciones = new ArrayList<>();
        adapterConversacionesChat = new AdapterConversacionesChat(listUsuarios, getContext(), this);
        recyclerViewUsuarios.setHasFixedSize(true);
        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewUsuarios.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewUsuarios.setAdapter(adapterConversacionesChat);

        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        usuarioActual = user.getUid();


        cargarConversaciones();
        selecUsuarioChat();

        return root;
    }


    public void cargarConversaciones() {
        db.collection(VariablesEstaticas.BD_CHATS).document(VariablesEstaticas.BD_CONVERSACIONES_CHAT).collection(usuarioActual)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            System.err.println("Listen failed:" + e);
                            return;
                        }

                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            String idMsj;
                            String emisorBD;
                            String receptorBD;

                            switch (dc.getType()) {
                                case ADDED:
                                    firstTime = 1;
                                    idMsj = dc.getDocument().getId();
                                    emisorBD = dc.getDocument().getString(VariablesEstaticas.BD_ID_EMISOR);
                                    receptorBD = dc.getDocument().getString(VariablesEstaticas.BD_ID_RECEPTOR);

                                    if(emisorBD.equals(usuarioActual)) {
                                       if (!listaConversaciones.contains(receptorBD)) {
                                           listaConversaciones.add(receptorBD);
                                           Log.d("Emisor", "Position: " + listaConversaciones);
                                       }
                                    } else if (receptorBD.equals(usuarioActual)) {
                                        if (!listaConversaciones.contains(emisorBD)) {
                                            listaConversaciones.add(emisorBD);
                                            Log.d("Receptor", "Position: " + listaConversaciones);
                                        }
                                    }
                                    Log.d("Msg", "New mensaje agregado al chat: " + listaConversaciones.size());
                                    break;
                                case MODIFIED:
                                    firstTime = 2;
                                    break;
                                case REMOVED:
                                    firstTime = 3;
                                    break;
                            }
                        }
                        if (firstTime == 1) {
                            cargarUsuariosConversaciones();
                        }
                    }

                });
    }


    public void cargarUsuariosConversaciones() {
        listUsuarios = new ArrayList<>();
        adapterConversacionesChat = new AdapterConversacionesChat(listUsuarios, getContext(), this);
        recyclerViewUsuarios.setHasFixedSize(true);
        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewUsuarios.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewUsuarios.setAdapter(adapterConversacionesChat);

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
                            if (!listaConversaciones.isEmpty()) {
                                if (listaConversaciones.contains(usuario.getReceptor())) {
                                    usuario.setNombreReceptor(dc.getDocument().getString(VariablesEstaticas.BD_NOMBRE_USUARIO));
                                    usuario.setImagen(dc.getDocument().getString(VariablesEstaticas.BD_IMAGEN_USUARIO));
                                    usuario.setOnLine(dc.getDocument().getBoolean(VariablesEstaticas.BD_STATUS_ONLINE_USUARIO));
                                    usuario.setUltimaConexion(dc.getDocument().getDate(VariablesEstaticas.BD_ULTIMA_CONEXION_USUARIO));

                                    listUsuarios.add(usuario);
                                }
                            }
                            adapterConversacionesChat.updateList(listUsuarios);

                            Log.d("Msg", "New mensaje: " + listUsuarios);
                            break;
                        case MODIFIED:
                            int position = 0;
                            usuario.setReceptor(dc.getDocument().getId());

                            if (!listaConversaciones.isEmpty()) {
                                if (listaConversaciones.contains(usuario.getReceptor())) {

                                    for (int i = 0; i < listUsuarios.size(); i++) {
                                        if (listUsuarios.get(i).getReceptor().equals(usuario.getReceptor())) {
                                            position = i;
                                        }
                                    }

                                    usuario.setNombreReceptor(dc.getDocument().getString(VariablesEstaticas.BD_NOMBRE_USUARIO));
                                    usuario.setImagen(dc.getDocument().getString(VariablesEstaticas.BD_IMAGEN_USUARIO));
                                    usuario.setOnLine(dc.getDocument().getBoolean(VariablesEstaticas.BD_STATUS_ONLINE_USUARIO));
                                    usuario.setUltimaConexion(dc.getDocument().getDate(VariablesEstaticas.BD_ULTIMA_CONEXION_USUARIO));

                                    listUsuarios.set(position, usuario);
                                }
                            }

                                adapterConversacionesChat.updateList(listUsuarios);

                                Log.d("Msg", "Modified mensaje: " + dc.getDocument().getData());

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

    private void borrarConversaciones() {

        if (listaConversacionesBorrar.isEmpty()) {
            snackbar.dismiss();
            VariablesGenerales.verCheckBoxes = false;
            adapterConversacionesChat.updateList(listUsuarios);
        } else if (listaConversacionesBorrar.size() == 1) {
            snackbar = Snackbar.make(root, "Borrar " + listaConversacionesBorrar.size() + " conversación", Snackbar.LENGTH_INDEFINITE).setAction("Borrar", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteChatsEmisorBD();
                }
            });
            snackbar.show();
        } else {
            snackbar = Snackbar.make(root, "Borrar " + listaConversacionesBorrar.size() + " conversaciones", Snackbar.LENGTH_INDEFINITE).setAction("Borrar", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteChatsEmisorBD();
                }
            });
            snackbar.show();
        }

    }

    private void deleteChatsEmisorBD() {
        for (int j = 0; j < listaConversacionesBorrar.size(); j++) {
            final String id = listaConversaciones.get(j);
            db.collection(VariablesEstaticas.BD_CHATS).document(VariablesEstaticas.BD_CONVERSACIONES_CHAT).collection(usuarioActual)
                    .whereEqualTo(VariablesEstaticas.BD_ID_EMISOR, id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection(VariablesEstaticas.BD_CHATS).document(VariablesEstaticas.BD_CONVERSACIONES_CHAT).collection(usuarioActual).document(document.getId())
                                    .delete();
                            Log.d("Delete", document.getId());

                    }
                        deleteChatsReceptorBD(id);
                    } else {
                        Log.d("Error", "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }

    private void deleteChatsReceptorBD(String id) {

            db.collection(VariablesEstaticas.BD_CHATS).document(VariablesEstaticas.BD_CONVERSACIONES_CHAT).collection(usuarioActual)
                    .whereEqualTo(VariablesEstaticas.BD_ID_RECEPTOR, id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            db.collection(VariablesEstaticas.BD_CHATS).document(VariablesEstaticas.BD_CONVERSACIONES_CHAT).collection(usuarioActual).document(document.getId())
                                    .delete();
                            Log.d("Delete", document.getId());
                        }
                    } else {
                        Log.d("Error", "Error getting documents: ", task.getException());
                    }
                }
            });

    }



    @Override
    public void onLongItemClick(int position) {
        listaConversacionesBorrar = new ArrayList<>();
        String idConversacionCon = listUsuarios.get(position).getReceptor();

        listaConversacionesBorrar.add(idConversacionCon);

        borrarConversaciones();
    }

    @Override
    public void selectedChat(int position) {
        if(VariablesGenerales.verCheckBoxes) {
            String idConversacionCon = listUsuarios.get(position).getReceptor();

            for (int i = 0; i < listaConversacionesBorrar.size(); i++) {
                if (!listaConversacionesBorrar.contains(idConversacionCon)) {
                    listaConversacionesBorrar.add(idConversacionCon);
                    borrarConversaciones();
                }
            }
        }

    }

    @Override
    public void unSelectedChat(int position) {
        if(VariablesGenerales.verCheckBoxes) {
            String idConversacionCon = listUsuarios.get(position).getReceptor();

            for (int i = 0; i < listaConversacionesBorrar.size(); i++) {
                if (listaConversacionesBorrar.get(i).equals(idConversacionCon)) {
                    position = i;
                }
            }
            listaConversacionesBorrar.remove(position);

            borrarConversaciones();
        }
    }

    @Override
    public void borrarSelecciones() {
        listaConversacionesBorrar.clear();
        borrarConversaciones();
    }
}