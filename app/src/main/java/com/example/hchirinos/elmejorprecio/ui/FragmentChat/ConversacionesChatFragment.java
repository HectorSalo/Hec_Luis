package com.example.hchirinos.elmejorprecio.ui.FragmentChat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hchirinos.elmejorprecio.Adaptadores.MyUsuariosChatRecyclerViewAdapter;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorMessenger;
import com.example.hchirinos.elmejorprecio.MessengerActivity;
import com.example.hchirinos.elmejorprecio.R;
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.example.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ConversacionesChatFragment extends Fragment {

    public ConversacionesChatFragment() {}

    private static final String ARG_SECTION_NUMBER = "section_number";

    private ArrayList<ConstructorMessenger> listUsuarios;
    private MyUsuariosChatRecyclerViewAdapter myUsuariosChatRecyclerViewAdapter;
    private RecyclerView recyclerViewUsuarios;
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

        cargarConversaciones();
        selecUsuarioChat();
        

        return root;
    }


    public void cargarConversaciones() {
        listUsuarios = new ArrayList<>();
        //progressBar.setVisibility(View.VISIBLE);
        myUsuariosChatRecyclerViewAdapter = new MyUsuariosChatRecyclerViewAdapter(listUsuarios, getContext());
        recyclerViewUsuarios.setHasFixedSize(true);
        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewUsuarios.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewUsuarios.setAdapter(myUsuariosChatRecyclerViewAdapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String usuarioActual = user.getUid();


        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                            usuario.setNombreReceptor(dc.getDocument().getString(VariablesEstaticas.BD_NOMBRE_USUARIO));
                            usuario.setEmail(dc.getDocument().getString(VariablesEstaticas.BD_EMAIL_USUARIO));
                            usuario.setImagen(dc.getDocument().getString(VariablesEstaticas.BD_IMAGEN_USUARIO));
                            usuario.setConversacionActiva(dc.getDocument().getBoolean("conversacionActiva"));

                            if (!usuario.getReceptor().equals(usuarioActual)) {
                                if (usuario.isConversacionActiva()) {
                                    listUsuarios.add(usuario);
                                }
                            }
                            Log.d("Msg", "New mensaje: " + dc.getDocument().getData());
                            break;
                        case MODIFIED:
                            int position = 0;
                            usuario.setReceptor(dc.getDocument().getId());
                            usuario.setNombreReceptor(dc.getDocument().getString(VariablesEstaticas.BD_NOMBRE_USUARIO));
                            usuario.setEmail(dc.getDocument().getString(VariablesEstaticas.BD_EMAIL_USUARIO));
                            usuario.setImagen(dc.getDocument().getString(VariablesEstaticas.BD_IMAGEN_USUARIO));
                            usuario.setConversacionActiva(dc.getDocument().getBoolean("conversacionActiva"));

                            for (int i = 0; i < listUsuarios.size(); i++) {
                                if (listUsuarios.get(i).getReceptor().equals(dc.getDocument().getId())) {
                                    position = i;
                                }
                            }

                            if (!usuario.getReceptor().equals(usuarioActual)) {
                                if (usuario.isConversacionActiva()) {
                                    listUsuarios.add(usuario);
                                } else {
                                    listUsuarios.remove(position);
                                }
                            }
                            Log.d("Msg", "Modified mensaje: " + dc.getDocument().getData());
                            break;
                        case REMOVED:
                            Log.d("Msg", "Removed mensaje: " + dc.getDocument().getData());
                            break;
                    }
                }
                myUsuariosChatRecyclerViewAdapter.updateList(listUsuarios);
            }
        });
    }

    private void selecUsuarioChat() {
        myUsuariosChatRecyclerViewAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VariablesGenerales.idChatVendedor = listUsuarios.get(recyclerViewUsuarios.getChildAdapterPosition(v)).getReceptor();
                VariablesGenerales.nombreChatVendedor = listUsuarios.get(recyclerViewUsuarios.getChildAdapterPosition(v)).getNombreReceptor();
                VariablesGenerales.correoChatVendedor = listUsuarios.get(recyclerViewUsuarios.getChildAdapterPosition(v)).getEmail();
                VariablesGenerales.imagenChatVendedor = listUsuarios.get(recyclerViewUsuarios.getChildAdapterPosition(v)).getImagen();
                startActivity(new Intent(getContext(), MessengerActivity.class));
            }
        });
    }
}