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
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.example.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class ConfPerfilActivity extends AppCompatActivity {

    private ImageView imageView;
    private ImageButton editarImagen, editarUbicacion;
    private EditText etNombre, etTelefono;
    private TextView tvUbicacion;
    private FirebaseUser user;
    private ProgressBar progressBar;
    private NetworkInfo networkInfo;
    private String imagenVieja, imagenNueva;
    private boolean imagenCambiada = false;
    private Uri imageSelected;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf_perfil);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        imageView = findViewById(R.id.imageViewPerfil);
        editarImagen = findViewById(R.id.imageButtonEditPerfil);
        editarUbicacion = findViewById(R.id.imageButtonEditUbicacion);
        etNombre = findViewById(R.id.editTextNombrePerfil);
        etTelefono = findViewById(R.id.editTextTelefonoPerfil);
        tvUbicacion = findViewById(R.id.editTextUbicacion);
        progressBar = findViewById(R.id.progressBarPerfil);
        ConstraintLayout constraintLayout = findViewById(R.id.constraintPerfil);

        user = FirebaseAuth.getInstance().getCurrentUser();


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean temaClaro = sharedPreferences.getBoolean("temaClaro", true);
        if (!temaClaro) {

            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }

        ConnectivityManager conexion = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conexion != null) {
            networkInfo = conexion.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            cargarPerfil();
        } else {
            Snackbar snackbar = Snackbar.make(constraintLayout, "Sin conexión", Snackbar.LENGTH_INDEFINITE).setAction("Reintentar", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recreate();
                }
            });
            snackbar.show();
            cargarPerfil();
        }

        editarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarImagen();
            }
        });


        editarUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VariablesGenerales.verSearchMap = true;
                startActivity(new Intent(getApplicationContext(), MapsInfoVendedor.class));
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

    private void cargarPerfil() {
        progressBar.setVisibility(View.VISIBLE);
        String idPerfil = user.getUid();

        db.collection(VariablesEstaticas.BD_VENDEDORES).document(idPerfil).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Perfil", "DocumentSnapshot data: " + document.getData());

                        etNombre.setText(document.getString(VariablesEstaticas.BD_NOMBRE_VENDEDOR));

                        String telefono = document.getString(VariablesEstaticas.BD_TELEFONO_VENDEDOR);
                        if(telefono != null) {
                            if (!telefono.isEmpty()) {
                                etTelefono.setText(telefono);
                            }
                        }

                        imagenVieja = document.getString(VariablesEstaticas.BD_IMAGEN_VENDEDOR);
                        if (imagenVieja != null) {
                            if (!imagenVieja.isEmpty()) {
                                Glide.with(getApplicationContext()).load(imagenVieja).into(imageView);
                            }
                        }

                        String ubicacion = document.getString(VariablesEstaticas.BD_UBICACION_PREFERIDA);
                        if (ubicacion != null) {
                            if (!ubicacion.isEmpty()) {
                                tvUbicacion.setText(ubicacion);
                            }
                        }
                    } else {
                        Log.d("Perfil", "No such document");
                    }

                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    Log.d("Perfil", "get failed with ", task.getException());
                }
            }
        });
    }

    private void validaciones() {
        String nombre = etNombre.getText().toString();

        if (!nombre.isEmpty()) {
            if (imagenCambiada) {
                subirImagen();
            } else {
                actualizarPerfilVendedor();
            }
        } else {
            etNombre.setError("Debe ingresar un nombre");
        }
    }

    private void actualizarPerfilVendedor() {
        String idPerfil = user.getUid();
        String nombre = etNombre.getText().toString();

        String telefono = "";

        if (!etTelefono.getText().toString().isEmpty()) {
            telefono = etTelefono.getText().toString();
        }

        HashMap<String, Object> updates = new HashMap<>();
        updates.put(VariablesEstaticas.BD_NOMBRE_VENDEDOR, nombre);
        updates.put(VariablesEstaticas.BD_TELEFONO_VENDEDOR, telefono);
        //updates.put(VariablesEstaticas.BD_UBICACION_PREFERIDA, ubicacion);

        db.collection(VariablesEstaticas.BD_VENDEDORES).document(idPerfil).update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                actualizarPerfilChat();
            }
        });
    }

    private void actualizarPerfilVendedorconImagen() {
        String idPerfil = user.getUid();
        String nombre = etNombre.getText().toString();

        String telefono = "";

        if (!etTelefono.getText().toString().isEmpty()) {
            telefono = etTelefono.getText().toString();
        }

        HashMap<String, Object> updates = new HashMap<>();
        updates.put(VariablesEstaticas.BD_NOMBRE_VENDEDOR, nombre);
        updates.put(VariablesEstaticas.BD_TELEFONO_VENDEDOR, telefono);
        //updates.put(VariablesEstaticas.BD_UBICACION_PREFERIDA, ubicacion);
        updates.put(VariablesEstaticas.BD_IMAGEN_VENDEDOR, imagenNueva);

        db.collection(VariablesEstaticas.BD_VENDEDORES).document(idPerfil).update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                actualizarPerfilChatconImagen();
            }
        });
    }

    private void actualizarPerfilChat() {
        String idPerfil = user.getUid();
        String nombre = etNombre.getText().toString();

        db.collection(VariablesEstaticas.BD_USUARIOS_CHAT).document(idPerfil).update(VariablesEstaticas.BD_NOMBRE_USUARIO, nombre).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Modificación exitosa", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                finish();
            }
        });
    }

    private void actualizarPerfilChatconImagen() {
        String idPerfil = user.getUid();
        String nombre = etNombre.getText().toString();


        HashMap<String, Object> updates = new HashMap<>();
        updates.put(VariablesEstaticas.BD_NOMBRE_USUARIO, nombre);
        updates.put(VariablesEstaticas.BD_IMAGEN_USUARIO, imagenNueva);

        db.collection(VariablesEstaticas.BD_USUARIOS_CHAT).document(idPerfil).update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Modificación exitosa", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                finish();
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


    private void subirImagen() {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child(VariablesEstaticas.BD_VENDEDORES);
        StorageReference imageRef = mStorageRef.child(imageSelected.getLastPathSegment());


        ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setPadding(30, 15, 30, 15);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Subiendo imagen y datos...")
                .setView(progressBar)
                .setCancelable(false);
        dialog.show();

// Register observers to listen for when the download is done or if it fails
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
                        actualizarPerfilVendedorconImagen();
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

}
