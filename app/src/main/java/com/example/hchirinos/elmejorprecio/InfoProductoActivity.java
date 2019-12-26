package com.example.hchirinos.elmejorprecio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorVendedores;
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.example.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

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
        TextView tvEstado = findViewById(R.id.tvEstadoProducto);

        tvVendedor.setText(VariablesGenerales.vendedorInfoProducto);
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

    private void crearBottomSheet () {
        View viewBottomSheet = LayoutInflater.from(InfoProductoActivity.this).inflate(R.layout.bottom_sheet_vendedor, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(InfoProductoActivity.this);
        bottomSheetDialog.setContentView(viewBottomSheet);
        bottomSheetDialog.show();

        Button butonVerPerfil = viewBottomSheet.findViewById(R.id.buttonVerVendedor);
        Button buttonEscribirVendedor = viewBottomSheet.findViewById(R.id.buttonEscribirVendedor);

        butonVerPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InfoProductoActivity.this, InfoVendedorActivity.class));
            }
        });

        buttonEscribirVendedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarSesion();
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
