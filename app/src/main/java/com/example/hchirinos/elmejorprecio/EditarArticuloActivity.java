package com.example.hchirinos.elmejorprecio;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.hchirinos.elmejorprecio.Clases.AlarmReceiverCambioPrecio;
import com.example.hchirinos.elmejorprecio.Clases.AlarmReceiverOferta;
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.example.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditarArticuloActivity extends AppCompatActivity {

    private NetworkInfo networkInfo;
    private ImageView imageView;
    private EditText etNombre, etDescripcion, etPrecio, etCantidad;
    private Spinner spinner;
    private RadioButton rbProducto, rbServicio, rbNuevo, rbUsado;
    private List<String> listaUnidades;
    private ProgressBar progressBar;
    private Uri imageSelected;
    private double precioViejo;
    private boolean imagenCambiada = false;
    private String imagenNueva;

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


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean temaClaro = sharedPreferences.getBoolean("temaClaro", true);
        if (!temaClaro) {

            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }

        ConstraintLayout constraintLayout = findViewById(R.id.constraintVentas);

        ConnectivityManager conexion = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conexion != null) {
            networkInfo = conexion.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            cargarArticulo();
        } else {
            Snackbar snackbar = Snackbar.make(constraintLayout, "Sin conexión", Snackbar.LENGTH_INDEFINITE).setAction("Reintentar", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recreate();
                }
            });
            snackbar.show();

        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imagenCambiada) {
                    uploadImagen();
                } else {
                    actualizarArticulo();
                }
            }
        });

        Button button = findViewById(R.id.buttonCambiarImagenArticulo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarImagen();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    private void uploadImagen() {
        String pathImagen = "";
        if (rbProducto.isChecked()) {
            pathImagen = VariablesEstaticas.BD_CATEGORIA_PRODUCTO;
        } else if (rbServicio.isChecked()) {
            pathImagen = VariablesEstaticas.BD_CATEGORIA_SERVICIO;
        }

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child(pathImagen);
        StorageReference imageRef = mStorageRef.child(imageSelected.getLastPathSegment());


        ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setPadding(30, 15, 30, 15);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Subiendo imagen...")
                .setView(progressBar)
                .setCancelable(false)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        imageRef.putFile(imageSelected).cancel();
                    }
                });
        dialog.show();

// Register observers to listen for when the download is done or if it fails
        String finalPathImagen = pathImagen;
        imageRef.putFile(imageSelected).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getApplicationContext(), "Error al guardar imagen. Intente nuevamente", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
               imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                   @Override
                   public void onSuccess(Uri uri) {
                       imagenNueva = uri.toString();
                       actualizarArticuloConImagen(finalPathImagen);
                   }
               });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                int progresInt = (int) progress;
                progressBar.setProgress(progresInt);
            }
        });
    }


    private void actualizarArticulo() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String nombre = etNombre.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        double precioNuevo = Double.valueOf(etPrecio.getText().toString());
        double cantidad = Double.valueOf(etCantidad.getText().toString());
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

        if (precioNuevo != precioViejo) {
            updates.put(VariablesEstaticas.BD_CAMBIO_PRECIO, true);
            programarTiempoCambioPrecio();
        }

        updates.put(VariablesEstaticas.BD_NOMBRE_PRODUCTO, nombre);
        updates.put(VariablesEstaticas.BD_DESCRIPCION_PRODUCTO, descripcion);
        updates.put(VariablesEstaticas.BD_PRECIO_PRODUCTO, precioNuevo);
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

    private void actualizarArticuloConImagen(String categoria) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String nombre = etNombre.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        double precioNuevo = Double.valueOf(etPrecio.getText().toString());
        double cantidad = Double.valueOf(etCantidad.getText().toString());
        String estado = "";
        String unidad = spinner.getSelectedItem().toString();

        if (rbNuevo.isChecked()) {
            estado = "Nuevo";
        } else if (rbUsado.isChecked()) {
            estado = "Usado";
        }

        Map<String, Object> updates = new HashMap<>();

        if (precioNuevo != precioViejo) {
            updates.put(VariablesEstaticas.BD_CAMBIO_PRECIO, true);
            programarTiempoCambioPrecio();
        }

        updates.put(VariablesEstaticas.BD_NOMBRE_PRODUCTO, nombre);
        updates.put(VariablesEstaticas.BD_DESCRIPCION_PRODUCTO, descripcion);
        updates.put(VariablesEstaticas.BD_PRECIO_PRODUCTO, precioNuevo);
        updates.put(VariablesEstaticas.BD_CANTIDAD_PRODUCTO, cantidad);
        updates.put(VariablesEstaticas.BD_ESTADO_PRODUCTO, estado);
        updates.put(VariablesEstaticas.BD_CATEGORIA, categoria);
        updates.put(VariablesEstaticas.BD_UNIDAD_PRODUCTO, unidad);
        updates.put(VariablesEstaticas.BD_IMAGEN_PRODUCTO, imagenNueva);

        db.collection(VariablesEstaticas.BD_ALMACEN).document(VariablesGenerales.idProductoEditar).update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), "Modificación exitosa", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), VentasActivity.class));
                finish();
            }
        });
    }


    private void programarTiempoCambioPrecio() {
        int idIntent = (int) System.currentTimeMillis();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiverCambioPrecio.class);
        Bundle bundle = new Bundle();
        bundle.putString("idProducto", VariablesGenerales.idProductoEditar);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,idIntent , intent, 0);

        alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + (1000 * 60 * 60 * 24 * 7), pendingIntent);
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

                        precioViejo = doc.getDouble(VariablesEstaticas.BD_PRECIO_PRODUCTO);

                        etNombre.setText(doc.getString(VariablesEstaticas.BD_NOMBRE_PRODUCTO));
                        etDescripcion.setText(doc.getString(VariablesEstaticas.BD_DESCRIPCION_PRODUCTO));
                        etPrecio.setText(String.valueOf(precioViejo));
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

    private void cambiarImagen() {
        Intent myintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(myintent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            imageSelected = data.getData();
            Glide.with(getApplicationContext()).load(imageSelected).into(imageView);
            imagenCambiada = true;

        }

    }
}
