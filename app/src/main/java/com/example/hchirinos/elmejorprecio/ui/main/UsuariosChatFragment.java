package com.example.hchirinos.elmejorprecio.ui.main;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hchirinos.elmejorprecio.Adaptadores.MyUsuariosChatRecyclerViewAdapter;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorVendedores;
import com.example.hchirinos.elmejorprecio.R;
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.example.hchirinos.elmejorprecio.ui.main.dummy.DummyContent.DummyItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    private ArrayList<ConstructorVendedores> listUsuarios;
    private MyUsuariosChatRecyclerViewAdapter myUsuariosChatRecyclerViewAdapter;
    private RecyclerView recyclerViewUsuarios;

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
        //progressBar.setVisibility(View.VISIBLE);
        myUsuariosChatRecyclerViewAdapter = new MyUsuariosChatRecyclerViewAdapter(listUsuarios, getContext());
        recyclerViewUsuarios.setHasFixedSize(true);
        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewUsuarios.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewUsuarios.setAdapter(myUsuariosChatRecyclerViewAdapter);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collectionGroup(VariablesEstaticas.BD_DETALLES_VENDEDOR).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        ConstructorVendedores vendedor = new ConstructorVendedores();
                        vendedor.setIdVendedor(doc.getString(VariablesEstaticas.BD_ID_VENDEDOR));
                        vendedor.setNombreVendedor(doc.getString(VariablesEstaticas.BD_NOMBRE_VENDEDOR));
                        vendedor.setCorreoVendedor(doc.getString(VariablesEstaticas.BD_CORREO_VENDEDOR));
                        vendedor.setTelefonoVendedor(doc.getString(VariablesEstaticas.BD_TELEFONO_VENDEDOR));
                        vendedor.setImagen(doc.getString(VariablesEstaticas.BD_IMAGEN_VENDEDOR));
                        vendedor.setUbicacionPreferida(doc.getString(VariablesEstaticas.BD_UBICACION_PREFERIDA));
                        vendedor.setLatlong(doc.getGeoPoint(VariablesEstaticas.BD_LATITUD_LONGITUD));

                        listUsuarios.add(vendedor);

                    }
                    myUsuariosChatRecyclerViewAdapter.updateList(listUsuarios);

                    //progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getContext(), "Error al cargar lista", Toast.LENGTH_SHORT).show();
                    //progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    private void selecUsuarioChat() {
        myUsuariosChatRecyclerViewAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), listUsuarios.get(recyclerViewUsuarios.getChildAdapterPosition(v)).getNombreVendedor(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
