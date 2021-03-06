package com.skysam.hchirinos.elmejorprecio;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.View;

import com.skysam.hchirinos.elmejorprecio.Adaptadores.AdapterProductos;
import com.skysam.hchirinos.elmejorprecio.Clases.GuardarDatosUsuario;
import com.skysam.hchirinos.elmejorprecio.Constructores.ConstructorProductos;
import com.skysam.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.skysam.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductosActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private NetworkInfo networkInfo;
    private SwipeRefreshLayout swRefresh;
    private ArrayList<ConstructorProductos> listProductos;
    private RecyclerView recyclerProductos;
    private AdapterProductos adapterProductos;
    private ProgressBar progressBar;
    private NavigationView navigationView;
    private ConstraintLayout constraintLayout;
    private FirebaseUser user;
    private boolean temaClaro;
    private  int acceso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        constraintLayout = findViewById(R.id.layoutProductos);
        progressBar = findViewById(R.id.progressBarProductos);

        recyclerProductos = (RecyclerView)findViewById(R.id.recyclerView_Productos);
        recyclerProductos.setHasFixedSize(true);
        listProductos = new ArrayList<>();
        adapterProductos = new AdapterProductos(listProductos, ProductosActivity.this);



        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerProductos.setAdapter(adapterProductos);
        } else {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 3));
            recyclerProductos.setAdapter(adapterProductos);
        }

        swRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);

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


        if (networkInfo != null && networkInfo.isConnected()) {
            if (VariablesGenerales.verResultadosBuscar) {
                resultadosBuscarHome();
            } else {
                cargarProductoServicio();
            }
        } else {
            Snackbar snackbar = Snackbar.make(constraintLayout, "Sin conexión", Snackbar.LENGTH_INDEFINITE).setAction("Reintentar", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recreate();
                }
            });
            snackbar.show();
            if (VariablesGenerales.verResultadosBuscar) {
                resultadosBuscarHome();
            } else {
                cargarProductoServicio();
            }
        }

        swRefresh.setOnRefreshListener(this);

    }



     @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            startActivity(new Intent(this, HomeActivity.class));
            VariablesGenerales.verResultadosBuscar = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!VariablesGenerales.verResultadosBuscar) {
            getMenuInflater().inflate(R.menu.productos, menu);
            MenuItem listOrdenar = menu.findItem(R.id.bar_ordenar);
            MenuItem menuItem = menu.findItem(R.id.bar_buscar);
            SearchView searchView = (SearchView) menuItem.getActionView();
            searchView.setOnQueryTextListener(this);
        }
        // Inflate the menu; this adds items to the action bar if it is present.


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

        if (id == R.id.bar_ordenar) {
            listOrdenar();
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
            startActivity(new Intent(this, VendedoresActivity.class));
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

    private void cargarProductoServicio() {
        if (VariablesGenerales.verProductos) {
            cargarProductos();
        } else {
            cargarServicios();
        }
    }


    // Ordenar lista
    private void listProductosMayorPrecio() {

        listProductos = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        adapterProductos = new AdapterProductos(listProductos, ProductosActivity.this);
        recyclerProductos.setHasFixedSize(true);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerProductos.setAdapter(adapterProductos);
        } else {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 3));
            recyclerProductos.setAdapter(adapterProductos);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_ALMACEN).whereEqualTo(VariablesEstaticas.BD_CATEGORIA, VariablesEstaticas.BD_CATEGORIA_PRODUCTO).whereEqualTo(VariablesEstaticas.BD_PRODUCTO_ACTIVO, true).orderBy(VariablesEstaticas.BD_PRECIO_PRODUCTO, Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        productos.setCantidadProducto(doc.getDouble(VariablesEstaticas.BD_CANTIDAD_PRODUCTO));

                        listProductos.add(productos);

                    }
                    adapterProductos.updateList(listProductos);
                    getSupportActionBar().setTitle("Productos");
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ProductosActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void listServiciosMayorPrecio() {
        listProductos = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        adapterProductos = new AdapterProductos(listProductos, ProductosActivity.this);
        recyclerProductos.setHasFixedSize(true);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerProductos.setAdapter(adapterProductos);
        } else {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 3));
            recyclerProductos.setAdapter(adapterProductos);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_ALMACEN).whereEqualTo(VariablesEstaticas.BD_CATEGORIA, VariablesEstaticas.BD_CATEGORIA_SERVICIO).whereEqualTo(VariablesEstaticas.BD_PRODUCTO_ACTIVO, true).orderBy(VariablesEstaticas.BD_PRECIO_PRODUCTO, Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        productos.setCantidadProducto(doc.getDouble(VariablesEstaticas.BD_CANTIDAD_PRODUCTO));

                        listProductos.add(productos);

                    }
                    adapterProductos.updateList(listProductos);
                    getSupportActionBar().setTitle("Servicios");
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ProductosActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void listProductosMenorPrecio() {

        listProductos = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        adapterProductos = new AdapterProductos(listProductos, ProductosActivity.this);
        recyclerProductos.setHasFixedSize(true);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerProductos.setAdapter(adapterProductos);
        } else {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 3));
            recyclerProductos.setAdapter(adapterProductos);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_ALMACEN).whereEqualTo(VariablesEstaticas.BD_CATEGORIA, VariablesEstaticas.BD_CATEGORIA_PRODUCTO).whereEqualTo(VariablesEstaticas.BD_PRODUCTO_ACTIVO, true).orderBy(VariablesEstaticas.BD_PRECIO_PRODUCTO, Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        productos.setCantidadProducto(doc.getDouble(VariablesEstaticas.BD_CANTIDAD_PRODUCTO));

                        listProductos.add(productos);

                    }
                    adapterProductos.updateList(listProductos);
                    getSupportActionBar().setTitle("Productos");
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ProductosActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void listServiciosMenorPrecio() {
        listProductos = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        adapterProductos = new AdapterProductos(listProductos, ProductosActivity.this);
        recyclerProductos.setHasFixedSize(true);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerProductos.setAdapter(adapterProductos);
        } else {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 3));
            recyclerProductos.setAdapter(adapterProductos);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_ALMACEN).whereEqualTo(VariablesEstaticas.BD_CATEGORIA, VariablesEstaticas.BD_CATEGORIA_SERVICIO).whereEqualTo(VariablesEstaticas.BD_PRODUCTO_ACTIVO, true).orderBy(VariablesEstaticas.BD_PRECIO_PRODUCTO, Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        productos.setCantidadProducto(doc.getDouble(VariablesEstaticas.BD_CANTIDAD_PRODUCTO));

                        listProductos.add(productos);

                    }
                    adapterProductos.updateList(listProductos);
                    getSupportActionBar().setTitle("Servicios");
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ProductosActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void listProductosMasRecientes() {
        listProductos = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        adapterProductos = new AdapterProductos(listProductos, ProductosActivity.this);
        recyclerProductos.setHasFixedSize(true);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerProductos.setAdapter(adapterProductos);
        } else {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 3));
            recyclerProductos.setAdapter(adapterProductos);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_ALMACEN).whereEqualTo(VariablesEstaticas.BD_CATEGORIA, VariablesEstaticas.BD_CATEGORIA_PRODUCTO).whereEqualTo(VariablesEstaticas.BD_PRODUCTO_ACTIVO, true).orderBy(VariablesEstaticas.BD_FECHA_INGRESO, Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        productos.setCantidadProducto(doc.getDouble(VariablesEstaticas.BD_CANTIDAD_PRODUCTO));

                        listProductos.add(productos);

                    }
                    adapterProductos.updateList(listProductos);
                    getSupportActionBar().setTitle("Productos");
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ProductosActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void listServiciosMasRecientes() {
        listProductos = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        adapterProductos = new AdapterProductos(listProductos, ProductosActivity.this);
        recyclerProductos.setHasFixedSize(true);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerProductos.setAdapter(adapterProductos);
        } else {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 3));
            recyclerProductos.setAdapter(adapterProductos);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_ALMACEN).whereEqualTo(VariablesEstaticas.BD_CATEGORIA, VariablesEstaticas.BD_CATEGORIA_SERVICIO).whereEqualTo(VariablesEstaticas.BD_PRODUCTO_ACTIVO, true).orderBy(VariablesEstaticas.BD_FECHA_INGRESO, Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        productos.setCantidadProducto(doc.getDouble(VariablesEstaticas.BD_CANTIDAD_PRODUCTO));

                        listProductos.add(productos);

                    }
                    adapterProductos.updateList(listProductos);
                    getSupportActionBar().setTitle("Servicios");
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ProductosActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void listProductosMenosRecientes(){
        listProductos = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        adapterProductos = new AdapterProductos(listProductos, ProductosActivity.this);
        recyclerProductos.setHasFixedSize(true);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerProductos.setAdapter(adapterProductos);
        } else {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 3));
            recyclerProductos.setAdapter(adapterProductos);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_ALMACEN).whereEqualTo(VariablesEstaticas.BD_CATEGORIA, VariablesEstaticas.BD_CATEGORIA_PRODUCTO).whereEqualTo(VariablesEstaticas.BD_PRODUCTO_ACTIVO, true).orderBy(VariablesEstaticas.BD_FECHA_INGRESO, Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        productos.setCantidadProducto(doc.getDouble(VariablesEstaticas.BD_CANTIDAD_PRODUCTO));

                        listProductos.add(productos);

                    }
                    adapterProductos.updateList(listProductos);
                    getSupportActionBar().setTitle("Productos");
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ProductosActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void listServiciosMenosRecientes() {
        listProductos = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        adapterProductos = new AdapterProductos(listProductos, ProductosActivity.this);
        recyclerProductos.setHasFixedSize(true);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerProductos.setAdapter(adapterProductos);
        } else {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 3));
            recyclerProductos.setAdapter(adapterProductos);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_ALMACEN).whereEqualTo(VariablesEstaticas.BD_CATEGORIA, VariablesEstaticas.BD_CATEGORIA_SERVICIO).whereEqualTo(VariablesEstaticas.BD_PRODUCTO_ACTIVO, true).orderBy(VariablesEstaticas.BD_FECHA_INGRESO, Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        productos.setCantidadProducto(doc.getDouble(VariablesEstaticas.BD_CANTIDAD_PRODUCTO));

                        listProductos.add(productos);

                    }
                    adapterProductos.updateList(listProductos);
                    getSupportActionBar().setTitle("Servicios");
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ProductosActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }


    private void cargarProductos () {
        listProductos = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        adapterProductos = new AdapterProductos(listProductos, ProductosActivity.this);
        recyclerProductos.setHasFixedSize(true);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerProductos.setAdapter(adapterProductos);
        } else {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 3));
            recyclerProductos.setAdapter(adapterProductos);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_ALMACEN).whereEqualTo(VariablesEstaticas.BD_CATEGORIA, VariablesEstaticas.BD_CATEGORIA_PRODUCTO).whereEqualTo(VariablesEstaticas.BD_PRODUCTO_ACTIVO, true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        ConstructorProductos productos = new ConstructorProductos();
                        productos.setIdProducto(doc.getId());
                        productos.setNombreProducto(doc.getString(VariablesEstaticas.BD_NOMBRE_PRODUCTO));
                        productos.setNombreProducto(doc.getString(VariablesEstaticas.BD_NOMBRE_PRODUCTO));
                        productos.setDescripcionProducto(doc.getString(VariablesEstaticas.BD_DESCRIPCION_PRODUCTO));
                        productos.setPrecioProducto(doc.getDouble(VariablesEstaticas.BD_PRECIO_PRODUCTO));
                        productos.setImagenProducto(doc.getString(VariablesEstaticas.BD_IMAGEN_PRODUCTO));
                        productos.setVendedor(doc.getString(VariablesEstaticas.BD_ID_USUARIO));
                        productos.setUnidadProducto(doc.getString(VariablesEstaticas.BD_UNIDAD_PRODUCTO));
                        productos.setEstadoProducto(doc.getString(VariablesEstaticas.BD_ESTADO_PRODUCTO));
                        productos.setListUsuariosFavoritos((ArrayList<String>) doc.get(VariablesEstaticas.BD_USUARIOS_FAVORITOS));
                        productos.setCantidadProducto(doc.getDouble(VariablesEstaticas.BD_CANTIDAD_PRODUCTO));

                        listProductos.add(productos);

                    }
                    adapterProductos.updateList(listProductos);
                    getSupportActionBar().setTitle("Productos");
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ProductosActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


    }

    private void cargarServicios() {
        listProductos = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        adapterProductos = new AdapterProductos(listProductos, ProductosActivity.this);
        recyclerProductos.setHasFixedSize(true);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerProductos.setAdapter(adapterProductos);
        } else {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 3));
            recyclerProductos.setAdapter(adapterProductos);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_ALMACEN).whereEqualTo(VariablesEstaticas.BD_CATEGORIA, VariablesEstaticas.BD_CATEGORIA_SERVICIO).whereEqualTo(VariablesEstaticas.BD_PRODUCTO_ACTIVO, true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        productos.setCantidadProducto(doc.getDouble(VariablesEstaticas.BD_CANTIDAD_PRODUCTO));

                        listProductos.add(productos);

                    }
                    adapterProductos.updateList(listProductos);
                    getSupportActionBar().setTitle("Servicios");
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ProductosActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void resultadosBuscarHome () {
        listProductos = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        adapterProductos = new AdapterProductos(listProductos, ProductosActivity.this);
        recyclerProductos.setHasFixedSize(true);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerProductos.setAdapter(adapterProductos);
        } else {
            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 3));
            recyclerProductos.setAdapter(adapterProductos);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_ALMACEN).whereEqualTo(VariablesEstaticas.BD_PRODUCTO_ACTIVO, true).orderBy(VariablesEstaticas.BD_PRECIO_PRODUCTO, Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        productos.setCantidadProducto(doc.getDouble(VariablesEstaticas.BD_CANTIDAD_PRODUCTO));

                        listProductos.add(productos);

                    }

                        ArrayList<ConstructorProductos> newList = new ArrayList<>();

                        for (ConstructorProductos name : listProductos) {

                            if (name.getNombreProducto().toLowerCase().contains(VariablesGenerales.textBuscar.toLowerCase())) {
                                newList.add(name);
                            }
                        }

                        adapterProductos.updateList(newList);
                        getSupportActionBar().setTitle("'" + VariablesGenerales.textBuscar + "'");
                        Snackbar.make(constraintLayout, newList.size() + " resultados para su búsqueda", Snackbar.LENGTH_INDEFINITE).show();


                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ProductosActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


    }

    @Override
    public void onRefresh() {
        if (!VariablesGenerales.verResultadosBuscar) {
            cargarProductoServicio();
            swRefresh.setRefreshing(false);
        } else {
            resultadosBuscarHome();
            swRefresh.setRefreshing(false);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (listProductos.isEmpty()) {
            Toast.makeText(this, "No hay lista cargada", Toast.LENGTH_SHORT).show();
        } else {
            String userInput = newText.toLowerCase();
            ArrayList<ConstructorProductos> newList = new ArrayList<>();

            for (ConstructorProductos name : listProductos) {

                if (name.getNombreProducto().toLowerCase().contains(userInput)) {

                    newList.add(name);
                }
            }

            adapterProductos.updateList(newList);

        }
        return false;
    }

    public void listOrdenar() {
        final CharSequence [] opciones = {"Mayor precio", "Menor precio", "Más recientes", "Más antiguos", "Cancelar"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Ordenar Lista: ");
        dialog.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (opciones[which].equals("Mayor precio")) {
                    if (VariablesGenerales.verProductos) {
                        listProductosMayorPrecio();
                    } else {
                        listServiciosMayorPrecio();
                    }
                } else if (opciones[which].equals("Menor precio")) {
                    if (VariablesGenerales.verProductos) {
                        listProductosMenorPrecio();
                    } else {
                        listServiciosMenorPrecio();
                    }
                } else if (opciones[which].equals("Más recientes")) {
                    if (VariablesGenerales.verProductos) {
                        listProductosMasRecientes();
                    } else {
                        listServiciosMasRecientes();
                    }
                } else if (opciones[which].equals("Más antiguos")) {
                    if (VariablesGenerales.verProductos) {
                        listProductosMenosRecientes();
                    } else {
                        listServiciosMenosRecientes();
                    }
                } else if (opciones[which].equals("Cancelar")){
                    dialog.dismiss();
                }
            }

        });
        dialog.show();
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
            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(ProductosActivity.this);
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
                String userID = user.getUid();
                String userEmail = user.getEmail();
                String userNombre = user.getDisplayName();

                GuardarDatosUsuario guardarDatosUsuario = new GuardarDatosUsuario();
                guardarDatosUsuario.almacenarDatos(userID, userNombre, userEmail, this);

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
        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(ProductosActivity.this);
        dialog.setTitle("¡Aviso!")
                .setMessage("¿Desea cerrar sesión?")
                .setPositiveButton("Cerrar Sesión", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection(VariablesEstaticas.BD_USUARIOS_CHAT).document(user.getUid()).update(VariablesEstaticas.BD_TOKEN, "").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseAuth.getInstance().signOut();
                                Toast.makeText(getApplicationContext(), "Sesión Cerrada", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Error al cerrar sesión. Intente nuevamente", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}



