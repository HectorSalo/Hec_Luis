package com.example.hchirinos.elmejorprecio;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.hchirinos.elmejorprecio.Adaptadores.AdapterProductos;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorProductos;
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.example.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class InfoVendedorActivity extends AppCompatActivity {

    private AdapterProductos adapterProductos;
    private ArrayList<ConstructorProductos> listProductos;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_vendedor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.setTitle(VariablesGenerales.nombreInfoVendedor);

        ImageView imageView = findViewById(R.id.imagenInfoVendedor);

        Glide.with(this).load(VariablesGenerales.imagenInfoVendedor).into(imageView);

        TextView tvUbicacion = findViewById(R.id.tvUbicacionPreferida);
        TextView tvTelefono = findViewById(R.id.tvTelefonoVendedor);
        TextView tvCorreo = findViewById(R.id.tvCorreoVendedor);
        recyclerView = findViewById(R.id.recyclerOtrosProductos);

        tvTelefono.setText(VariablesGenerales.telefonoInfoVendedor);
        tvCorreo.setText(VariablesGenerales.correoInfoVendedor);

        listProductos = new ArrayList<>();
        adapterProductos = new AdapterProductos(listProductos, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterProductos);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        cargarLista();
    }

    private void cargarLista() {
        listProductos = new ArrayList<>();
        adapterProductos = new AdapterProductos(listProductos, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterProductos);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_VENDEDORES).document(VariablesGenerales.idInfoVendedor).collection(VariablesEstaticas.BD_PRODUCTOS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        ConstructorProductos productos = new ConstructorProductos();
                        productos.setIdProducto(doc.getId());
                        productos.setDescripcionProducto(doc.getString(VariablesEstaticas.BD_DESCRIPCION_PRODUCTO));
                        productos.setPrecioProducto(doc.getDouble(VariablesEstaticas.BD_PRECIO_PRODUCTO));
                        productos.setImagenProducto(doc.getString(VariablesEstaticas.BD_IMAGEN_PRODUCTO));
                        productos.setVendedor(doc.getString(VariablesEstaticas.BD_VENDEDOR_ASOCIADO));
                        productos.setUnidadProducto(doc.getString(VariablesEstaticas.BD_UNIDAD_PRODUCTO));

                        double cantidadD = doc.getDouble(VariablesEstaticas.BD_CANTIDAD_PRODUCTO);
                        int cantidadInt = (int) cantidadD;
                        productos.setCantidadProducto(cantidadInt);

                        listProductos.add(productos);

                    }
                    adapterProductos.updateList(listProductos);
                    //progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(InfoVendedorActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
                    //progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
