package com.example.hchirinos.elmejorprecio.ui.FragmentChat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hchirinos.elmejorprecio.Adaptadores.AdapterConversacionesChat;
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
    private AdapterConversacionesChat adapterConversacionesChat;
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

        listUsuarios = new ArrayList<>();
        adapterConversacionesChat = new AdapterConversacionesChat(listUsuarios, getContext());
        recyclerViewUsuarios.setHasFixedSize(true);
        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewUsuarios.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewUsuarios.setAdapter(adapterConversacionesChat);

        cargarConversaciones();
        selecUsuarioChat();


        return root;
    }


    public void cargarConversaciones() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String usuarioActual = user.getUid();


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_CHATS).document(VariablesEstaticas.BD_CONVERSACIONES_CHAT).collection("Test").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.err.println("Listen failed:" + e);
                    return;
                }

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    ConstructorMessenger usuario = new ConstructorMessenger();
                    String emisorBD;
                    String receptorBD;

                    switch (dc.getType()) {
                        case ADDED:
                            String conversacionCon;
                            emisorBD = dc.getDocument().getString(VariablesEstaticas.BD_ID_EMISOR);
                            receptorBD = dc.getDocument().getString(VariablesEstaticas.BD_ID_RECEPTOR);

                            if (emisorBD.equals(usuarioActual)) {
                                conversacionCon = receptorBD;
                            } else if (receptorBD.equals(usuarioActual)) {
                                conversacionCon = emisorBD;
                            }
                            Log.d("Msg", "New mensaje: " + dc.getDocument().getData());
                            break;
                        case MODIFIED:
                            /*int position = 0;
                            usuario.setReceptor(dc.getDocument().getId());
                            usuario.setNombreReceptor(dc.getDocument().getString(VariablesEstaticas.BD_NOMBRE_USUARIO));
                            usuario.setEmail(dc.getDocument().getString(VariablesEstaticas.BD_EMAIL_USUARIO));
                            usuario.setImagen(dc.getDocument().getString(VariablesEstaticas.BD_IMAGEN_USUARIO));
                            usuario.setOnLine(dc.getDocument().getBoolean(VariablesEstaticas.BD_STATUS_ONLINE_USUARIO));

                            for (int i = 0; i < listUsuarios.size(); i++) {
                                if (listUsuarios.get(i).getReceptor().equals(dc.getDocument().getId())) {
                                    position = i;
                                }
                            }

                            if (!usuario.getReceptor().equals(usuarioActual)) {

                            }*/
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
}