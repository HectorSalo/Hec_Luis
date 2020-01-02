package com.example.hchirinos.elmejorprecio.ui.FragmentChat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.hchirinos.elmejorprecio.Adaptadores.MyUsuariosChatRecyclerViewAdapter;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorMessenger;
import com.example.hchirinos.elmejorprecio.MessengerActivity;
import com.example.hchirinos.elmejorprecio.R;
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.example.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.example.hchirinos.elmejorprecio.ui.FragmentChat.dummy.DummyContent.DummyItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class UsuariosChatFragment extends Fragment {

    private ArrayList<ConstructorMessenger> listUsuarios;
    private MyUsuariosChatRecyclerViewAdapter myUsuariosChatRecyclerViewAdapter;
    private RecyclerView recyclerViewUsuarios;
    private ProgressBar progressBar;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UsuariosChatFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static UsuariosChatFragment newInstance(int columnCount) {
        UsuariosChatFragment fragment = new UsuariosChatFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usuarioschat_list, container, false);

        recyclerViewUsuarios = view.findViewById(R.id.recyclerViewUsuariosChat);
        progressBar = view.findViewById(R.id.progressBarUsuariosChat);

        cargarUsuariosChat();
        selecUsuarioChat();

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }

    private void cargarUsuariosChat() {
        listUsuarios = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        myUsuariosChatRecyclerViewAdapter = new MyUsuariosChatRecyclerViewAdapter(listUsuarios, getContext());
        recyclerViewUsuarios.setHasFixedSize(true);
        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewUsuarios.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewUsuarios.setAdapter(myUsuariosChatRecyclerViewAdapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String usuarioActual = user.getUid();


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_USUARIOS_CHAT).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        ConstructorMessenger usuario = new ConstructorMessenger();
                        usuario.setReceptor(doc.getId());
                        usuario.setNombreReceptor(doc.getString(VariablesEstaticas.BD_NOMBRE_USUARIO));
                        usuario.setEmail(doc.getString(VariablesEstaticas.BD_EMAIL_USUARIO));
                        usuario.setImagen(doc.getString(VariablesEstaticas.BD_IMAGEN_USUARIO));

                        if (!usuario.getReceptor().equals(usuarioActual)) {
                            listUsuarios.add(usuario);
                        }

                    }
                    myUsuariosChatRecyclerViewAdapter.updateList(listUsuarios);

                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getContext(), "" +
                            "Error al cargar lista", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
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
