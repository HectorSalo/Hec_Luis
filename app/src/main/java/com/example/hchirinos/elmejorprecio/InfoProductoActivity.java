package com.example.hchirinos.elmejorprecio;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorVendedores;
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void cargarVendedor() {
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

                        //listVendedores.add(vendedor);

                    }
                    //adapterVendedores.updateList(listVendedores);
                    //progressBar.setVisibility(View.GONE);
                } else {
                    //Toast.makeText(VendedoresActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
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
