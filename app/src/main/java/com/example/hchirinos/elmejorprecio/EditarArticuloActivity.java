package com.example.hchirinos.elmejorprecio;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorProductos;
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.example.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditarArticuloActivity extends AppCompatActivity {

    private ImageView imageView;
    private EditText etNombre, etDescripcion, etPrecio, etCantidad;
    private Spinner spinner;
    private RadioButton rbProducto, rbServicio, rbNuevo, rbUsado;
    private List<String> listaUnidades;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_articulo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView = findViewById(R.id.imageViewEditarArticulo);
        etNombre = findViewById(R.id.editTextEditarNombreArticulo);
        etDescripcion = findViewById(R.id.editTextEditarDescripcionArticulo);
        etPrecio = findViewById(R.id.editTextEditarPrecioArticulo);
        etCantidad = findViewById(R.id.editTextEditarCantidadArticulo);
        spinner = findViewById(R.id.spinnerEditarUnidadArticulo);
        rbNuevo = findViewById(R.id.radioButtonEditarNuevoArticulo);
        rbUsado = findViewById(R.id.radioButtonEditarUsadoArticulo);
        rbProducto = findViewById(R.id.radioButtonEditarProductoArticulo);
        rbServicio = findViewById(R.id.radioButtonEditarServicioArticulo);
        progressBar = findViewById(R.id.progressBarEditarArticulo);

        listaUnidades = Arrays.asList(getResources().getStringArray(R.array.unidades));
        ArrayAdapter<String> adapterUnidades = new ArrayAdapter<String>(this, R.layout.spinner_unidades, listaUnidades);
        spinner.setAdapter(adapterUnidades);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarArticulo();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        cargarArticulo();
    }

    private void actualizarArticulo() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String nombre = etNombre.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        Double precio = Double.valueOf(etPrecio.getText().toString());
        Double cantidad = Double.valueOf(etCantidad.getText().toString());
        String estado = "";
        String categoria = "";
        String unidad = spinner.getSelectedItem().toString();

        if (rbNuevo.isChecked()) {
            estado = "Nuevo";
        } else if (rbUsado.isChecked()) {
            estado = "Usado";
        }

        if (rbProducto.isChecked()) {
            categoria = "Producto";
        } else if (rbServicio.isChecked()) {
            categoria = "Servicio";
        }


        Map<String, Object> updates = new HashMap<>();

        updates.put(VariablesEstaticas.BD_NOMBRE_PRODUCTO, nombre);
        updates.put(VariablesEstaticas.BD_DESCRIPCION_PRODUCTO, descripcion);
        updates.put(VariablesEstaticas.BD_PRECIO_PRODUCTO, precio);
        updates.put(VariablesEstaticas.BD_CANTIDAD_PRODUCTO, cantidad);
        updates.put(VariablesEstaticas.BD_ESTADO_PRODUCTO, estado);
        updates.put(VariablesEstaticas.BD_CATEGORIA, categoria);
        updates.put(VariablesEstaticas.BD_UNIDAD_PRODUCTO, unidad);

        db.collection(VariablesEstaticas.BD_ALMACEN).document(VariablesGenerales.idProductoEditar).update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), "Modificación exitosa", Toast.LENGTH_SHORT).show();
               startActivity(new Intent(getApplicationContext(), VentasActivity.class));
               finish();
            }
        });
    }


    private void cargarArticulo() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_ALMACEN).document(VariablesGenerales.idProductoEditar).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {

                        etNombre.setText(doc.getString(VariablesEstaticas.BD_NOMBRE_PRODUCTO));
                        etDescripcion.setText(doc.getString(VariablesEstaticas.BD_DESCRIPCION_PRODUCTO));
                        etPrecio.setText(String.valueOf(doc.getDouble(VariablesEstaticas.BD_PRECIO_PRODUCTO)));
                        etCantidad.setText(String.valueOf(doc.getDouble(VariablesEstaticas.BD_CANTIDAD_PRODUCTO)));
                        String imagen  = doc.getString(VariablesEstaticas.BD_IMAGEN_PRODUCTO);
                        String unidad = doc.getString(VariablesEstaticas.BD_UNIDAD_PRODUCTO);
                        String estado = doc.getString(VariablesEstaticas.BD_ESTADO_PRODUCTO);
                        String categoria = doc.getString(VariablesEstaticas.BD_CATEGORIA);

                        Glide.with(getApplicationContext()).load(imagen).into(imageView);

                        if (estado.equals("Nuevo")) {
                            rbNuevo.setChecked(true);
                        } else if (estado.equals("Usado")) {
                            rbUsado.setChecked(true);
                        }

                        if (categoria.equals("Producto")) {
                            rbProducto.setChecked(true);
                        } else if (categoria.equals("Servicio")) {
                            rbServicio.setChecked(true);
                        }

                        int positionUnidad = 0;
                        for (int j = 0; j < listaUnidades.size(); j++) {
                            if (listaUnidades.get(j).equals(unidad)) {
                                positionUnidad = j;
                            }
                        }
                        spinner.setSelection(positionUnidad);

                        progressBar.setVisibility(View.GONE);
                        Log.d("Editar", "DocumentSnapshot data: " + doc.getData());

                    } else {
                        Log.d("Editar", "No such document");
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    Log.d("Editar", "get failed with ", task.getException());
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
