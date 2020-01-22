package com.example.hchirinos.elmejorprecio;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;

import com.example.hchirinos.elmejorprecio.Adaptadores.AdapterProductos;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorProductos;
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.example.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    private NetworkInfo networkInfo;
    private RecyclerView recyclerRecientes, recyclerCambioPrecio, recyclerOfertas;
    private AdapterProductos adapterRecientes, adapterCambioPrecio, adapterOferta;
    private ArrayList<ConstructorProductos> listRecientes, listCambioPrecio, listOferta;
    private ProgressBar progressBarRecientes, progressBarCambioPrecio, progressBarOfertas;
    private NavigationView navigationView;
    private FirebaseUser user;
    private boolean temaClaro;
    private int acceso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        user = FirebaseAuth.getInstance().getCurrentUser();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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

        progressBarCambioPrecio = findViewById(R.id.progressBarCambioPrecio);
        progressBarOfertas = findViewById(R.id.progressBarOfertas);
        progressBarRecientes = findViewById(R.id.progressBarRecientes);

        listRecientes = new ArrayList<>();
        listCambioPrecio = new ArrayList<>();
        listOferta = new ArrayList<>();
        recyclerCambioPrecio = findViewById(R.id.recyclerViewCambioPrecio);
        recyclerRecientes = findViewById(R.id.recyclerViewRecientes);
        recyclerOfertas = findViewById(R.id.recyclerViewOferta);
        recyclerCambioPrecio.setHasFixedSize(true);
        recyclerRecientes.setHasFixedSize(true);
        recyclerOfertas.setHasFixedSize(true);
        recyclerCambioPrecio.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerRecientes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerOfertas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterRecientes = new AdapterProductos(listRecientes, this);
        adapterCambioPrecio = new AdapterProductos(listCambioPrecio, this);
        adapterOferta = new AdapterProductos(listOferta, this);
        recyclerRecientes.setAdapter(adapterRecientes);
        recyclerOfertas.setAdapter(adapterOferta);
        recyclerCambioPrecio.setAdapter(adapterCambioPrecio);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean subscripcionInicial = sharedPreferences.getBoolean("subsinicial", true);
        temaClaro = sharedPreferences.getBoolean("temaClaro", true);
        if (!temaClaro) {

            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }

        if (subscripcionInicial) {
            subsInicial();
        }

        ConstraintLayout constraintLayout = findViewById(R.id.constraintHome);

        ConnectivityManager conexion = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conexion != null) {
            networkInfo = conexion.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            cargarRecientes();
            cargarOfertas();
            cargarCambioPrecio();
        } else {
            Snackbar snackbar = Snackbar.make(constraintLayout, "Sin conexión", Snackbar.LENGTH_INDEFINITE).setAction("Reintentar", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recreate();
                }
            });
            snackbar.show();

        }

    }

    private void subsInicial() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("subsinicial", false);
        editor.commit();

        FirebaseMessaging.getInstance().subscribeToTopic("notif")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscripcion exitosa";
                        if (!task.isSuccessful()) {
                            msg = "Failed";
                        }
                        Log.d("suscrito", msg);

                    }
                });
    }

    private void cargarCambioPrecio() {
        listCambioPrecio = new ArrayList<>();

        adapterCambioPrecio = new AdapterProductos(listCambioPrecio, HomeActivity.this);
        recyclerCambioPrecio.setHasFixedSize(true);
        recyclerCambioPrecio.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerCambioPrecio.setAdapter(adapterCambioPrecio);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_ALMACEN).whereEqualTo(VariablesEstaticas.BD_PRODUCTO_ACTIVO, true).whereEqualTo(VariablesEstaticas.BD_CAMBIO_PRECIO, true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        ConstructorProductos productos = new ConstructorProductos();
                        productos.setIdProducto(doc.getId());
                        productos.setNombreProducto(doc.getString(VariablesEstaticas.BD_NOMBRE_PRODUCTO));
                        productos.setDescripcionProducto(doc.getString(VariablesEstaticas.BD_DESCRIPCION_PRODUCTO));
                        productos.setPrecioProducto(doc.getDouble(VariablesEstaticas.BD_PRECIO_PRODUCTO));
                        productos.setImagenProducto(doc.getString(VariablesEstaticas.BD_IMAGEN_PRODUCTO));
                        productos.setVendedor(doc.getString(VariablesEstaticas.BD_ID_USUARIO));
                        productos.setUnidadProducto(doc.getString(VariablesEstaticas.BD_UNIDAD_PRODUCTO));
                        productos.setEstadoProducto(doc.getString(VariablesEstaticas.BD_ESTADO_PRODUCTO));
                        productos.setListUsuariosFavoritos((ArrayList<String>) doc.get(VariablesEstaticas.BD_USUARIOS_FAVORITOS));

                        double cantidadD = doc.getDouble(VariablesEstaticas.BD_CANTIDAD_PRODUCTO);
                        int cantidadInt = (int) cantidadD;
                        productos.setCantidadProducto(cantidadInt);

                        listCambioPrecio.add(productos);

                    }
                    adapterCambioPrecio.updateList(listCambioPrecio);
                    progressBarCambioPrecio.setVisibility(View.GONE);
                } else {
                    Toast.makeText(HomeActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
                    progressBarCambioPrecio.setVisibility(View.GONE);
                }
            }
        });
    }

    private void cargarOfertas() {
        listOferta = new ArrayList<>();

        adapterOferta = new AdapterProductos(listOferta, HomeActivity.this);
        recyclerOfertas.setHasFixedSize(true);
        recyclerOfertas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerOfertas.setAdapter(adapterOferta);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_ALMACEN).whereEqualTo(VariablesEstaticas.BD_PRODUCTO_ACTIVO, true).whereEqualTo(VariablesEstaticas.BD_OFERTA_SEMANA, true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        ConstructorProductos productos = new ConstructorProductos();
                        productos.setIdProducto(doc.getId());
                        productos.setNombreProducto(doc.getString(VariablesEstaticas.BD_NOMBRE_PRODUCTO));
                        productos.setDescripcionProducto(doc.getString(VariablesEstaticas.BD_DESCRIPCION_PRODUCTO));
                        productos.setPrecioProducto(doc.getDouble(VariablesEstaticas.BD_PRECIO_PRODUCTO));
                        productos.setImagenProducto(doc.getString(VariablesEstaticas.BD_IMAGEN_PRODUCTO));
                        productos.setVendedor(doc.getString(VariablesEstaticas.BD_ID_USUARIO));
                        productos.setUnidadProducto(doc.getString(VariablesEstaticas.BD_UNIDAD_PRODUCTO));
                        productos.setEstadoProducto(doc.getString(VariablesEstaticas.BD_ESTADO_PRODUCTO));
                        productos.setListUsuariosFavoritos((ArrayList<String>) doc.get(VariablesEstaticas.BD_USUARIOS_FAVORITOS));

                        double cantidadD = doc.getDouble(VariablesEstaticas.BD_CANTIDAD_PRODUCTO);
                        int cantidadInt = (int) cantidadD;
                        productos.setCantidadProducto(cantidadInt);

                        listOferta.add(productos);

                    }
                    adapterOferta.updateList(listOferta);
                    progressBarOfertas.setVisibility(View.GONE);
                } else {
                    Toast.makeText(HomeActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
                    progressBarOfertas.setVisibility(View.GONE);
                }
            }
        });
    }

    private void cargarRecientes() {
        listRecientes = new ArrayList<>();

        adapterRecientes = new AdapterProductos(listRecientes, HomeActivity.this);
        recyclerRecientes.setHasFixedSize(true);
        recyclerRecientes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerRecientes.setAdapter(adapterRecientes);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_ALMACEN).whereEqualTo(VariablesEstaticas.BD_PRODUCTO_ACTIVO, true).orderBy(VariablesEstaticas.BD_FECHA_INGRESO, Query.Direction.DESCENDING).limit(6).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        ConstructorProductos productos = new ConstructorProductos();
                        productos.setIdProducto(doc.getId());
                        productos.setNombreProducto(doc.getString(VariablesEstaticas.BD_NOMBRE_PRODUCTO));
                        productos.setDescripcionProducto(doc.getString(VariablesEstaticas.BD_DESCRIPCION_PRODUCTO));
                        productos.setPrecioProducto(doc.getDouble(VariablesEstaticas.BD_PRECIO_PRODUCTO));
                        productos.setImagenProducto(doc.getString(VariablesEstaticas.BD_IMAGEN_PRODUCTO));
                        productos.setVendedor(doc.getString(VariablesEstaticas.BD_ID_USUARIO));
                        productos.setUnidadProducto(doc.getString(VariablesEstaticas.BD_UNIDAD_PRODUCTO));
                        productos.setEstadoProducto(doc.getString(VariablesEstaticas.BD_ESTADO_PRODUCTO));
                        productos.setListUsuariosFavoritos((ArrayList<String>) doc.get(VariablesEstaticas.BD_USUARIOS_FAVORITOS));

                        double cantidadD = doc.getDouble(VariablesEstaticas.BD_CANTIDAD_PRODUCTO);
                        int cantidadInt = (int) cantidadD;
                        productos.setCantidadProducto(cantidadInt);

                        listRecientes.add(productos);

                    }
                    adapterRecientes.updateList(listRecientes);
                    progressBarRecientes.setVisibility(View.GONE);
                } else {
                    Toast.makeText(HomeActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
                    progressBarRecientes.setVisibility(View.GONE);
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
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        MenuItem menuItem = menu.findItem(R.id.bar_buscar);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getResources().getString(R.string.searchview_home));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setBackgroundResource(R.drawable.fondo_searchview_home);

        searchView.setOnQueryTextListener(this);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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
            VariablesGenerales.verProductos = true;
            VariablesGenerales.verResultadosBuscar = false;
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_servicios) {
            startActivity(new Intent(this, ProductosActivity.class));
            VariablesGenerales.verProductos = false;
            VariablesGenerales.verResultadosBuscar = false;
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_supermercados) {
            Intent ir_supermercado = new Intent(this, VendedoresActivity.class);
            startActivity(ir_supermercado);
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_favorito) {
            validarInicSesion(2);
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_chat) {
            validarInicSesion(1);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_vender) {
            validarInicSesion(3);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_cerrar_sesion) {
            cerrarSesion();
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_configuracion){
           startActivity(new Intent(this, SettingsActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_inicio) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private void validarInicSesion(int i) {
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
        dialog.setTitle("¡Aviso!")
                .setMessage("¿Desea cerrar sesión?")
                .setPositiveButton("Cerrar Sesión", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(getApplicationContext(), "Sesión Cerrada", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        VariablesGenerales.textBuscar = query.trim();
        VariablesGenerales.verResultadosBuscar = true;

        startActivity(new Intent(this, ProductosActivity.class));
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
