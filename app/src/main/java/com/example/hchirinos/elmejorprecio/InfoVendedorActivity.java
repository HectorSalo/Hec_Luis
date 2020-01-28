package com.example.hchirinos.elmejorprecio;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean temaClaro = sharedPreferences.getBoolean("temaClaro", true);
        if (!temaClaro) {

            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }

        this.setTitle(VariablesGenerales.nombreInfoVendedor);

        ImageView imageView = findViewById(R.id.imagenInfoVendedor);

        Glide.with(this).load(VariablesGenerales.imagenInfoVendedor).into(imageView);

        TextView tvUbicacion = findViewById(R.id.tvUbicacionPreferida);
        TextView tvTelefono = findViewById(R.id.tvTelefonoVendedor);
        TextView tvCorreo = findViewById(R.id.tvCorreoVendedor);
        recyclerView = findViewById(R.id.recyclerOtrosProductos);

        tvTelefono.setText(VariablesGenerales.telefonoInfoVendedor);
        tvCorreo.setText(VariablesGenerales.correoInfoVendedor);
        if (!VariablesGenerales.ubicacionInfoVendedor.isEmpty()) {
            tvUbicacion.setText(VariablesGenerales.ubicacionInfoVendedor);
        } else {
            tvUbicacion.setText("Este vendedor no tiene un lugar específico para la entrega");
        }

        listProductos = new ArrayList<>();
        adapterProductos = new AdapterProductos(listProductos, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterProductos);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!VariablesGenerales.ubicacionInfoVendedor.isEmpty()) {
                    VariablesGenerales.verSearchMap = false;
                    startActivity(new Intent(InfoVendedorActivity.this, MapsInfoVendedor.class));
                } else {
                    Snackbar.make(view, "Este vendedor no tiene un lugar específico para la entrega", Snackbar.LENGTH_LONG)
                            .show();
                }
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

        db.collection(VariablesEstaticas.BD_ALMACEN).whereEqualTo(VariablesEstaticas.BD_ID_USUARIO, VariablesGenerales.idInfoVendedor).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        ConstructorProductos productos = new ConstructorProductos();
                        productos.setIdProducto(doc.getId());
                        productos.setNombreProducto(doc.getString(VariablesEstaticas.BD_NOMBRE_PRODUCTO));
                        productos.setDescripcionProducto(doc.getString(VariablesEstaticas.BD_DESCRIPCION_PRODUCTO));
                        productos.setPrecioProducto(doc.getDouble(VariablesEstaticas.BD_PRECIO_PRODUCTO));
                        productos.setImagenProducto(doc.getString(VariablesEstaticas.BD_IMAGEN_PRODUCTO));
                        productos.setVendedor(doc.getString(VariablesEstaticas.BD_ID_USUARIO));
                        productos.setUnidadProducto(doc.getString(VariablesEstaticas.BD_UNIDAD_PRODUCTO));
                        productos.setEstadoProducto(doc.getString(VariablesEstaticas.BD_ESTADO_PRODUCTO));

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
