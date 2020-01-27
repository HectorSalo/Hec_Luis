package com.example.hchirinos.elmejorprecio;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.hchirinos.elmejorprecio.Adaptadores.AdapterVentas;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorProductos;
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
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
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.MediaStore;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AddArticuloActivity extends AppCompatActivity {

    private NetworkInfo networkInfo;
    private ImageView imageView;
    private EditText etNombre, etDescripcion, etPrecio, etCantidad;
    private Spinner spinner;
    private RadioButton rbProducto, rbServicio, rbNuevo, rbUsado;
    private Uri imageSelected;
    private List<String> listaUnidades;
    private String nombre, descripcion, categoria, estado, unidad, imagen;
    private double precioD, cantidadD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_articulo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        imageView = findViewById(R.id.imageViewAddArticulo);
        etNombre = findViewById(R.id.editTextAddNombreArticulo);
        etDescripcion = findViewById(R.id.editTextAddDescripcionArticulo);
        etPrecio = findViewById(R.id.editTextAddPrecioArticulo);
        etCantidad = findViewById(R.id.editTextAddCantidadArticulo);
        spinner = findViewById(R.id.spinnerAddUnidadArticulo);
        rbNuevo = findViewById(R.id.radioButtonAddNuevoArticulo);
        rbUsado = findViewById(R.id.radioButtonAddUsadoArticulo);
        rbProducto = findViewById(R.id.radioButtonAddProductoArticulo);
        rbServicio = findViewById(R.id.radioButtonAddServicioArticulo);

        imageSelected = null;

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

        if (networkInfo == null || !networkInfo.isConnected()) {
            Snackbar snackbar = Snackbar.make(constraintLayout, "Sin conexión", Snackbar.LENGTH_INDEFINITE).setAction("Reintentar", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recreate();
                }
            });
            snackbar.show();
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImagen();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validaciones();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void validaciones() {
        nombre = etNombre.getText().toString();
        descripcion = etDescripcion.getText().toString();
        String precio = etPrecio.getText().toString();
        precioD = Double.valueOf(precio);
        String cantidad = etCantidad.getText().toString();
        cantidadD = Double.parseDouble(cantidad);

        if (!nombre.isEmpty()) {
            if (!descripcion.isEmpty()) {
                if (!precio.isEmpty()) {
                     if (!cantidad.isEmpty()) {
                         if (spinner.getSelectedItemPosition() > 0) {
                             unidad = spinner.getSelectedItem().toString();
                             if (rbProducto.isChecked() || rbServicio.isChecked()) {
                                 if (rbProducto.isChecked()) {
                                     categoria = "Producto";
                                 } else if (rbServicio.isChecked()) {
                                     categoria = "Servicio";
                                 }
                                 if (rbNuevo.isChecked() || rbUsado.isChecked()) {
                                     if (rbNuevo.isChecked()) {
                                         estado = "Nuevo";
                                     } else if (rbUsado.isChecked()) {
                                         estado = "Usado";
                                     }
                                     if (imageSelected != null) {
                                         guardarImagen();
                                     } else {
                                         Toast.makeText(this, "Debe seleccionar una imagen", Toast.LENGTH_SHORT).show();
                                     }
                                 } else {
                                     Toast.makeText(this, "Debe seleccionar Nuevo o Usado", Toast.LENGTH_SHORT).show();
                                 }
                             } else {
                                 Toast.makeText(this, "Debe seleccionar Producto o Servicio", Toast.LENGTH_SHORT).show();
                             }
                         } else {
                             Toast.makeText(this, "Debe seleccionar una unidad", Toast.LENGTH_SHORT).show();
                         }
                     } else {
                         etCantidad.setError("Debe ingresar la cantidad");
                     }
                } else {
                    etPrecio.setError("Debe ingresar un precio");
                }
            } else {
                etDescripcion.setError("Debe ingresar una descripción");
            }
        } else {
            etNombre.setError("Debe ingresar el nombre");
        }

    }


    private void selectImagen() {
        Intent myintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(myintent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            imageSelected = data.getData();
            Glide.with(getApplicationContext()).load(imageSelected).into(imageView);
        }
    }

    private void guardarImagen() {
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
        dialog.setTitle("Subiendo imagen y datos...")
                .setView(progressBar)
                .setCancelable(false);
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
                        imagen = uri.toString();
                        guardarArticulo();
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


    private void guardarArticulo() {
        Calendar calendar = Calendar.getInstance();
        Date fecha = calendar.getTime();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String idUsuario = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        HashMap<String, Object> articulo = new HashMap<>();
        articulo.put(VariablesEstaticas.BD_CAMBIO_PRECIO, false);
        articulo.put(VariablesEstaticas.BD_CANTIDAD_PRODUCTO, cantidadD);
        articulo.put(VariablesEstaticas.BD_CATEGORIA, categoria);
        articulo.put(VariablesEstaticas.BD_DESCRIPCION_PRODUCTO, descripcion);
        articulo.put(VariablesEstaticas.BD_ESTADO_PRODUCTO, estado);
        articulo.put(VariablesEstaticas.BD_FECHA_INGRESO, fecha);
        articulo.put(VariablesEstaticas.BD_IMAGEN_PRODUCTO, imagen);
        articulo.put(VariablesEstaticas.BD_NOMBRE_PRODUCTO, nombre);
        articulo.put(VariablesEstaticas.BD_OFERTA_SEMANA, false);
        articulo.put(VariablesEstaticas.BD_PRECIO_PRODUCTO, precioD);
        articulo.put(VariablesEstaticas.BD_PRODUCTO_ACTIVO, true);
        articulo.put(VariablesEstaticas.BD_UNIDAD_PRODUCTO, unidad);
        articulo.put(VariablesEstaticas.BD_ID_USUARIO, idUsuario);

        db.collection(VariablesEstaticas.BD_ALMACEN)
                .add(articulo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("AddDoc", "DocumentSnapshot written with ID: " + documentReference.getId());
                        Toast.makeText(getApplicationContext(), "Guardado exitosamente", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), VentasActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("AddDoc", "Error adding document", e);
                        Toast.makeText(getApplicationContext(), "Error al guardar. Intente nuevamente", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
