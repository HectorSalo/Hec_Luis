package com.skysam.hchirinos.elmejorprecio;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.skysam.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.skysam.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
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

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class ConfPerfilActivity extends AppCompatActivity {

    private ImageView imageView;
    private ImageButton editarImagen, editarUbicacion;
    private EditText etNombre, etTelefono, etPass, etPassRepetir, etPassViejo;
    private TextView tvUbicacion;
    private FirebaseUser user;
    private ProgressBar progressBar;
    private NetworkInfo networkInfo;
    private String imagenVieja, imagenNueva;
    private boolean imagenCambiada = false;
    private Uri imageSelected;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LinearLayout linearLayoutPass;
    private Switch switchPass;

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
        linearLayoutPass = findViewById(R.id.linear_pass);
        etPassViejo = findViewById(R.id.editTextPassViejo);
        etPass = findViewById(R.id.editTextPass);
        etPassRepetir = findViewById(R.id.editTextPassRepeat);
        switchPass = findViewById(R.id.switch_pass);

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
                opcionUbicacion();
            }
        });

        switchPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    linearLayoutPass.setVisibility(View.VISIBLE);
                } else {
                    linearLayoutPass.setVisibility(View.GONE);
                }
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

    private void opcionUbicacion() {
        View viewBottomSheet = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_ubicacion, null);
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(viewBottomSheet);
        bottomSheetDialog.show();


        Button butonNuevaUbicacion = viewBottomSheet.findViewById(R.id.button_nueva_ubicacion);
        Button buttonQuitarUbicacion = viewBottomSheet.findViewById(R.id.button_quitar_ubicacion);

        butonNuevaUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VariablesGenerales.escogerUbiPreferida = true;
                startActivity(new Intent(getApplicationContext(), MapsInfoVendedor.class));
                bottomSheetDialog.dismiss();
            }
        });

        buttonQuitarUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeoPoint geoPoint = new GeoPoint(0, 0);
                VariablesGenerales.ubicacionUsuarioString = "";
                VariablesGenerales.ubicacionUsuarioGeoPoint = geoPoint;
                tvUbicacion.setText("Sin lugar de entregas");
                bottomSheetDialog.dismiss();
            }
        });
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

                        VariablesGenerales.ubicacionUsuarioString = document.getString(VariablesEstaticas.BD_UBICACION_PREFERIDA);
                        if (VariablesGenerales.ubicacionUsuarioString != null) {
                            if (!VariablesGenerales.ubicacionUsuarioString.isEmpty()) {
                                tvUbicacion.setText(VariablesGenerales.ubicacionUsuarioString);
                            }
                        }

                        VariablesGenerales.ubicacionUsuarioGeoPoint = document.getGeoPoint(VariablesEstaticas.BD_LATITUD_LONGITUD);
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
            if (switchPass.isChecked()) {
                String passViejo = etPassViejo.getText().toString();
                if (!passViejo.isEmpty()) {
                    String pass = etPass.getText().toString();
                    if (pass.length() >= 6) {
                        String passRepetir = etPassRepetir.getText().toString();
                        if (pass.equals(passRepetir)) {
                            if (imagenCambiada) {
                                subirImagen();
                                updatePerfilUser();
                                cambiarPass(pass, passViejo);
                            } else {
                                updatePerfilUser();
                                actualizarPerfilVendedor();
                                cambiarPass(pass, passViejo);
                            }
                        } else {
                            etPassRepetir.setError("Las contraseñas deben coincidir");
                        }
                    } else {
                        etPass.setError("Debe ingresar al menos 6 caracteres");
                    }
                } else {
                    etPassViejo.setError("Debe ingresar contraseña anterior");
                }

            } else {
                if (imagenCambiada) {
                    subirImagen();
                    updatePerfilUser();
                } else {
                    updatePerfilUser();
                    actualizarPerfilVendedor();
                }
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
        updates.put(VariablesEstaticas.BD_UBICACION_PREFERIDA, VariablesGenerales.ubicacionUsuarioString);
        updates.put(VariablesEstaticas.BD_LATITUD_LONGITUD, VariablesGenerales.ubicacionUsuarioGeoPoint);

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
        updates.put(VariablesEstaticas.BD_UBICACION_PREFERIDA, VariablesGenerales.ubicacionUsuarioString);
        updates.put(VariablesEstaticas.BD_LATITUD_LONGITUD, VariablesGenerales.ubicacionUsuarioGeoPoint);
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
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child(VariablesEstaticas.BD_USUARIOS);
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

    private void updatePerfilUser(){
        String nombre = etNombre.getText().toString();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nombre)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Profile", "User profile updated.");
                        }
                    }
                });
    }


    private void cambiarPass(String newPassword, String passViejo) {
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), passViejo);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Auth", "User re-authenticated.");

                        user.updatePassword(newPassword)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("PassWord", "User password updated.");
                                            Toast.makeText(getApplicationContext(), "Contraseña modificada exitosamente", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Contraseña no fue cambiada. Intente nuevamente", Toast.LENGTH_SHORT).show();
                                Log.e("PassWord", "" + e);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Contraseña no fue cambiada. Intente nuevamente", Toast.LENGTH_SHORT).show();
                                Log.e("PassWord", "" + e);
                            }
                        });

                    }
                });


    }

    @Override
    protected void onResume() {
        super.onResume();
        tvUbicacion.setText(VariablesGenerales.ubicacionUsuarioString);
    }
}
