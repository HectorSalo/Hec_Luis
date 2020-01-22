package com.example.hchirinos.elmejorprecio;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;

import com.example.hchirinos.elmejorprecio.Adaptadores.AdapterVendedores;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorVendedores;
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.example.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VendedoresActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private NetworkInfo networkInfo;
    private SwipeRefreshLayout swRefresh;
    private ArrayList<ConstructorVendedores> listVendedores;
    private RecyclerView recyclerVendedores;
    private AdapterVendedores adapterVendedores;
    private ProgressBar progressBar;
    private NavigationView navigationView;
    private FirebaseUser user;
    private boolean temaClaro;
    private int acceso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendedores);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user = FirebaseAuth.getInstance().getCurrentUser();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        Menu menu = navigationView.getMenu();
        MenuItem itemCerrarSesion = menu.findItem(R.id.nav_cerrar_sesion);
        if (user != null) {
            itemCerrarSesion.setVisible(true);
        } else {
            itemCerrarSesion.setVisible(false);
        }

        progressBar = findViewById(R.id.progressBarVendedores);
        ConstraintLayout constraintLayout = findViewById(R.id.constraintVendedores);

        recyclerVendedores = (RecyclerView)findViewById(R.id.recyclerViewVendedores);
        recyclerVendedores.setHasFixedSize(true);
        listVendedores = new ArrayList<>();
        adapterVendedores = new AdapterVendedores(listVendedores, this);


        recyclerVendedores.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        recyclerVendedores.setAdapter(adapterVendedores);



        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        temaClaro = sharedPreferences.getBoolean("temaClaro", true);
        if (!temaClaro) {

            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }

        ConnectivityManager conexion = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conexion != null) {
            networkInfo = conexion.getActiveNetworkInfo();
        }
        swRefresh = (SwipeRefreshLayout) findViewById(R.id.swpRefresh);

        if (networkInfo != null && networkInfo.isConnected()) {
            cargarFirestore();
        } else {
            Snackbar snackbar = Snackbar.make(constraintLayout, "Sin conexión", Snackbar.LENGTH_INDEFINITE).setAction("Reintentar", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recreate();
                }
            });
            snackbar.show();
            cargarFirestore();
        }

        swRefresh.setOnRefreshListener(this);

        selecVendedor();

    }

    private void cargarFirestore() {
        listVendedores = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        adapterVendedores = new AdapterVendedores(listVendedores, this);
        recyclerVendedores.setHasFixedSize(true);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerVendedores.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerVendedores.setAdapter(adapterVendedores);
        } else {
            recyclerVendedores.setLayoutManager(new GridLayoutManager(this, 3));
            recyclerVendedores.setAdapter(adapterVendedores);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_VENDEDORES).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        ConstructorVendedores vendedor = new ConstructorVendedores();
                        vendedor.setIdVendedor(doc.getId());
                        vendedor.setNombreVendedor(doc.getString(VariablesEstaticas.BD_NOMBRE_VENDEDOR));
                        vendedor.setCorreoVendedor(doc.getString(VariablesEstaticas.BD_CORREO_VENDEDOR));
                        vendedor.setTelefonoVendedor(doc.getString(VariablesEstaticas.BD_TELEFONO_VENDEDOR));
                        vendedor.setImagen(doc.getString(VariablesEstaticas.BD_IMAGEN_VENDEDOR));
                        vendedor.setUbicacionPreferida(doc.getString(VariablesEstaticas.BD_UBICACION_PREFERIDA));
                        vendedor.setLatlong(doc.getGeoPoint(VariablesEstaticas.BD_LATITUD_LONGITUD));

                        listVendedores.add(vendedor);

                    }
                    adapterVendedores.updateList(listVendedores);
                    selecVendedor();
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(VendedoresActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            startActivity(new Intent(this, HomeActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vendedores, menu);
        MenuItem menuItem = menu.findItem(R.id.bar_buscar);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.bar_buscar) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Menu menu = navigationView.getMenu();

        int id = item.getItemId();


        if (id == R.id.nav_catalogos) {
            MenuItem itemServicios = menu.findItem(R.id.nav_servicios);
            MenuItem itemProductos = menu.findItem(R.id.nav_productos);

            SpannableString textServicios = new SpannableString(itemServicios.getTitle());
            textServicios.setSpan(new TextAppearanceSpan(this, R.style.TextAppearanceCatalogo), 0, textServicios.length(), 0);
            itemServicios.setTitle(textServicios);

            SpannableString textProductos = new SpannableString(itemProductos.getTitle());
            textProductos.setSpan(new TextAppearanceSpan(this, R.style.TextAppearanceCatalogo), 0, textProductos.length(), 0);
            itemProductos.setTitle(textProductos);

            if (menu.findItem(R.id.nav_productos).isVisible()) {
                menu.findItem(R.id.nav_servicios).setVisible(false);
                menu.findItem(R.id.nav_productos).setVisible(false);
            } else {
                menu.findItem(R.id.nav_servicios).setVisible(true);
                menu.findItem(R.id.nav_productos).setVisible(true);
            }

        } else if (id == R.id.nav_productos) {
            startActivity(new Intent(this, ProductosActivity.class));
            drawer.closeDrawer(GravityCompat.START);
            VariablesGenerales.verProductos = true;
            VariablesGenerales.verResultadosBuscar = false;
        } else if (id == R.id.nav_servicios) {
            startActivity(new Intent(this, ProductosActivity.class));
            drawer.closeDrawer(GravityCompat.START);
            VariablesGenerales.verProductos = false;
            VariablesGenerales.verResultadosBuscar = false;
        } else if (id == R.id.nav_supermercados) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_chat) {
            validarInicSesion(1);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_vender) {
            validarInicSesion(3);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_favorito) {
            validarInicSesion(2);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_cerrar_sesion) {
            cerrarSesion();
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_configuracion){
            startActivity(new Intent(this, SettingsActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_inicio) {
            startActivity(new Intent(this, HomeActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (listVendedores.isEmpty()) {
            Toast.makeText(this, "No hay lista cargada", Toast.LENGTH_SHORT).show();
        } else {
            String userInput = newText.toLowerCase();
            final ArrayList<ConstructorVendedores> newList = new ArrayList<>();

            for (ConstructorVendedores name : listVendedores) {

                if (name.getNombreVendedor().toLowerCase().contains(userInput)) {

                    newList.add(name);
                }
            }

            adapterVendedores.updateList(newList);

            adapterVendedores.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VariablesGenerales.idInfoVendedor = newList.get(recyclerVendedores.getChildAdapterPosition(v)).getIdVendedor();
                    VariablesGenerales.imagenInfoVendedor = newList.get(recyclerVendedores.getChildAdapterPosition(v)).getImagen();
                    VariablesGenerales.nombreInfoVendedor = newList.get(recyclerVendedores.getChildAdapterPosition(v)).getNombreVendedor();
                    VariablesGenerales.telefonoInfoVendedor = newList.get(recyclerVendedores.getChildAdapterPosition(v)).getTelefonoVendedor();
                    VariablesGenerales.correoInfoVendedor = newList.get(recyclerVendedores.getChildAdapterPosition(v)).getCorreoVendedor();
                    VariablesGenerales.ubicacionInfoVendedor = newList.get(recyclerVendedores.getChildAdapterPosition(v)).getUbicacionPreferida();
                    VariablesGenerales.latlongInfoVendedor = newList.get(recyclerVendedores.getChildAdapterPosition(v)).getLatlong();

                    startActivity(new Intent(VendedoresActivity.this, InfoVendedorActivity.class));
                }
            });

        }
        return true;
    }

    private void selecVendedor () {
        adapterVendedores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VariablesGenerales.idInfoVendedor = listVendedores.get(recyclerVendedores.getChildAdapterPosition(v)).getIdVendedor();
                VariablesGenerales.nombreInfoVendedor = listVendedores.get(recyclerVendedores.getChildAdapterPosition(v)).getNombreVendedor();
                VariablesGenerales.telefonoInfoVendedor = listVendedores.get(recyclerVendedores.getChildAdapterPosition(v)).getTelefonoVendedor();
                VariablesGenerales.correoInfoVendedor = listVendedores.get(recyclerVendedores.getChildAdapterPosition(v)).getCorreoVendedor();
                VariablesGenerales.imagenInfoVendedor = listVendedores.get(recyclerVendedores.getChildAdapterPosition(v)).getImagen();
                VariablesGenerales.ubicacionInfoVendedor = listVendedores.get(recyclerVendedores.getChildAdapterPosition(v)).getUbicacionPreferida();
                VariablesGenerales.latlongInfoVendedor = listVendedores.get(recyclerVendedores.getChildAdapterPosition(v)).getLatlong();

                startActivity(new Intent(getApplicationContext(), InfoVendedorActivity.class));
            }
        });
    }


    private void validarInicSesion(int i) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            switch (i) {
                case 1:
                    startActivity(new Intent(this, ChatActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(this, FavoritosActivity.class));
                    break;
                case 3:
                    startActivity(new Intent(this, VentasActivity.class));
                    break;
            }
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(VendedoresActivity.this);
            dialog.setTitle("¡Aviso!")
                    .setMessage("Debe iniciar sesión para esta opción\n¿Desea iniciar sesión?")
                    .setPositiveButton("Iniciar Sesión", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            acceso = i;
                            iniciarSesion();
                        }
                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
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

                switch (acceso) {
                    case 1:
                        startActivity(new Intent(this, ChatActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(this, FavoritosActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(this, VentasActivity.class));
                        break;
                }

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.

            }
        }
    }

    private void cerrarSesion() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(VendedoresActivity.this);
        dialog.setTitle("¡Aviso!")
                .setMessage("¿Desea cerrar sesión?")
                .setPositiveButton("Cerrar Sesión", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(getApplicationContext(), "Sesión Cerrada", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), VendedoresActivity.class));
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void onRefresh() {
        cargarFirestore();
        swRefresh.setRefreshing(false);
    }


}

