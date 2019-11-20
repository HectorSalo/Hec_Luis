package com.example.hchirinos.elmejorprecio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorVendedores;
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.example.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class InfoProductoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_producto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.setTitle(VariablesGenerales.descripcionInfoProducto);

        ImageView imageView = findViewById(R.id.imagenInfoProducto);
        Glide.with(this).load(VariablesGenerales.imagenInfoProducto).into(imageView);

        TextView tvVendedor = findViewById(R.id.tvVendedorInfoProducto);
        TextView tvCantidad = findViewById(R.id.tvCantidadInfoProducto);
        TextView tvPrecio = findViewById(R.id.tvPrecioInfoProducto);

        tvVendedor.setText(VariablesGenerales.vendedorInfoProducto);
        tvCantidad.setText(VariablesGenerales.cantidadesInfoProducto);
        tvPrecio.setText(VariablesGenerales.precioInfoProducto);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean temaClaro = sharedPreferences.getBoolean("temaClaro", true);
        if (!temaClaro) {

            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }

        cargarVendedor();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InfoProductoActivity.this, InfoVendedorActivity.class));
            }
        });
    }

    public void cargarVendedor() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collectionGroup(VariablesEstaticas.BD_DETALLES_VENDEDOR).whereEqualTo(VariablesEstaticas.BD_NOMBRE_VENDEDOR, VariablesGenerales.vendedorInfoProducto).orderBy(VariablesEstaticas.BD_ID_VENDEDOR, Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {

                VariablesGenerales.imagenInfoVendedor = snapshot.getString(VariablesEstaticas.BD_IMAGEN_VENDEDOR);
                VariablesGenerales.telefonoInfoVendedor = snapshot.getString(VariablesEstaticas.BD_TELEFONO_VENDEDOR);
                VariablesGenerales.correoInfoVendedor = snapshot.getString(VariablesEstaticas.BD_CORREO_VENDEDOR);
                VariablesGenerales.nombreInfoVendedor = VariablesGenerales.vendedorInfoProducto;
                VariablesGenerales.idInfoVendedor = snapshot.getString(VariablesEstaticas.BD_ID_VENDEDOR);
                VariablesGenerales.ubicacionInfoVendedor = snapshot.getString(VariablesEstaticas.BD_UBICACION_PREFERIDA);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
