package com.skysam.hchirinos.elmejorprecio;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.skysam.hchirinos.elmejorprecio.Clases.GuardarDatosUsuario;
import com.skysam.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.skysam.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

public class InfoProductoActivity extends AppCompatActivity {

    private boolean temaClaro;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String idReceptorChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_producto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.setTitle(VariablesGenerales.nombreInfoProducto);

        mAuth = FirebaseAuth.getInstance();

        ImageView imageView = findViewById(R.id.imagenInfoProducto);
        Glide.with(this).load(VariablesGenerales.imagenInfoProducto).into(imageView);

        TextView tvDescripcion = findViewById(R.id.tvDescripcionInfoProducto);
        TextView tvCantidad = findViewById(R.id.tvCantidadInfoProducto);
        TextView tvPrecio = findViewById(R.id.tvPrecioInfoProducto);
        TextView tvEstado = findViewById(R.id.tvEstadoProducto);

        tvDescripcion.setText(VariablesGenerales.descripcionInfoProducto);
        tvCantidad.setText(VariablesGenerales.cantidadesInfoProducto);
        tvPrecio.setText(VariablesGenerales.precioInfoProducto);
        tvEstado.setText(VariablesGenerales.estadoInfoProducto);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        temaClaro = sharedPreferences.getBoolean("temaClaro", true);
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
                crearBottomSheet();
            }
        });
    }

    public void cargarVendedor() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_VENDEDORES).document(VariablesGenerales.vendedorInfoProducto).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        VariablesGenerales.imagenInfoVendedor = document.getString(VariablesEstaticas.BD_IMAGEN_VENDEDOR);
                        VariablesGenerales.telefonoInfoVendedor = document.getString(VariablesEstaticas.BD_TELEFONO_VENDEDOR);
                        VariablesGenerales.correoInfoVendedor = document.getString(VariablesEstaticas.BD_CORREO_VENDEDOR);
                        VariablesGenerales.nombreInfoVendedor = document.getString(VariablesEstaticas.BD_NOMBRE_VENDEDOR);
                        VariablesGenerales.idInfoVendedor = VariablesGenerales.vendedorInfoProducto;
                        VariablesGenerales.ubicacionInfoVendedor = document.getString(VariablesEstaticas.BD_UBICACION_PREFERIDA);
                        idReceptorChat = VariablesGenerales.vendedorInfoProducto;
                        Log.d("MSG", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("MSG", "No such document");
                    }
                } else {
                    Log.d("MSG", "get failed with ", task.getException());
                }
            }
        });

    }

    private void crearBottomSheet () {
        View viewBottomSheet = LayoutInflater.from(InfoProductoActivity.this).inflate(R.layout.bottom_sheet_vendedor, null);
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(InfoProductoActivity.this);
        bottomSheetDialog.setContentView(viewBottomSheet);
        bottomSheetDialog.show();


        Button butonVerPerfil = viewBottomSheet.findViewById(R.id.buttonVerVendedor);
        Button buttonEscribirVendedor = viewBottomSheet.findViewById(R.id.buttonEscribirVendedor);

        butonVerPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InfoProductoActivity.this, InfoVendedorActivity.class));
                bottomSheetDialog.dismiss();
            }
        });

        buttonEscribirVendedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = mAuth.getCurrentUser();
                if (user != null) {
                    if (!user.getUid().equals(idReceptorChat)) {
                        VariablesGenerales.idChatVendedor = idReceptorChat;
                        VariablesGenerales.nombreChatVendedor = VariablesGenerales.nombreInfoVendedor;
                        VariablesGenerales.correoChatVendedor = VariablesGenerales.correoInfoVendedor;
                        VariablesGenerales.imagenChatVendedor = VariablesGenerales.imagenInfoVendedor;
                        startActivity(new Intent(InfoProductoActivity.this, MessengerActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "No puede iniciar Chat con usted mismo", Toast.LENGTH_SHORT).show();
                    }

                    bottomSheetDialog.dismiss();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(InfoProductoActivity.this);
                    dialog.setTitle("¡Aviso!")
                            .setMessage("Debe iniciar sesión para enviar mensaje directo\n¿Desea iniciar sesión?")
                            .setPositiveButton("Iniciar Sesión", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    iniciarSesion();
                                    bottomSheetDialog.dismiss();
                                }
                            }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            bottomSheetDialog.dismiss();
                        }
                    }).show();
                }
            }
        });
    }

    private void iniciarSesion() {

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
                new AuthUI.IdpConfig.EmailBuilder().build());

// Create and launch sign-in intent
        if (!temaClaro) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setTheme(R.style.AppThemeNoche)
                            .build(),
                    12);
        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    12);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 12) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userID = user.getUid();
                String userEmail = user.getEmail();
                String userNombre = user.getDisplayName();

                GuardarDatosUsuario guardarDatosUsuario = new GuardarDatosUsuario();
                guardarDatosUsuario.almacenarDatos(userID, userNombre, userEmail, this);

                VariablesGenerales.idChatVendedor = idReceptorChat;
                VariablesGenerales.nombreChatVendedor = VariablesGenerales.nombreInfoVendedor;
                VariablesGenerales.correoChatVendedor = VariablesGenerales.correoInfoVendedor;
                VariablesGenerales.imagenChatVendedor = VariablesGenerales.imagenInfoVendedor;

                startActivity(new Intent(this, MessengerActivity.class));

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.

            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
