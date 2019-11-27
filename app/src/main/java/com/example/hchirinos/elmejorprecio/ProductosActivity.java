package com.example.hchirinos.elmejorprecio;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.example.hchirinos.elmejorprecio.Adaptadores.AdapterProductos;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorProductos;
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;

public class ProductosActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private NetworkInfo networkInfo;
    private SwipeRefreshLayout swRefresh;
    private ArrayList<ConstructorProductos> listProductos;
    private RecyclerView recyclerProductos;
    private AdapterProductos adapterProductos;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        ConstraintLayout constraintLayout = findViewById(R.id.layoutProductos);
        progressBar = findViewById(R.id.progressBarProductos);

        recyclerProductos = (RecyclerView)findViewById(R.id.recyclerView_Productos);
        recyclerProductos.setHasFixedSize(true);
        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));
        listProductos = new ArrayList<>();
        adapterProductos = new AdapterProductos(listProductos, ProductosActivity.this);
        recyclerProductos.setAdapter(adapterProductos);

        swRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);

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
        getMenuInflater().inflate(R.menu.productos, menu);
        MenuItem listOrdenar = menu.findItem(R.id.bar_ordenar);
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

        if (id == R.id.bar_ordenar) {
            listOrdenar();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_productos) {

        } else if (id == R.id.nav_supermercados) {
            Intent ir_supermercado = new Intent(this, VendedoresActivity.class);
            startActivity(ir_supermercado);

        } else if (id == R.id.nav_favorito) {
            Intent irFavoritos = new Intent(this, FavoritosActivity.class);
            startActivity(irFavoritos);

        } else if (id == R.id.nav_configuracion){
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_inicio) {
            Intent ir_inicio = new Intent(this, HomeActivity.class);
            startActivity(ir_inicio);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // Ordenar lista
    private void ListProductosMayorPrecio() {

        listProductos = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        adapterProductos = new AdapterProductos(listProductos, ProductosActivity.this);
        recyclerProductos.setHasFixedSize(true);
        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));
        recyclerProductos.setAdapter(adapterProductos);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collectionGroup(VariablesEstaticas.BD_PRODUCTOS).whereEqualTo(VariablesEstaticas.BD_PRODUCTO_ACTIVO, true).orderBy(VariablesEstaticas.BD_PRECIO_PRODUCTO, Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        ConstructorProductos productos = new ConstructorProductos();
                        productos.setIdProducto(doc.getId());
                        productos.setDescripcionProducto(doc.getString(VariablesEstaticas.BD_DESCRIPCION_PRODUCTO));
                        productos.setPrecioProducto(doc.getDouble(VariablesEstaticas.BD_PRECIO_PRODUCTO));
                        productos.setImagenProducto(doc.getString(VariablesEstaticas.BD_IMAGEN_PRODUCTO));
                        productos.setVendedor(doc.getString(VariablesEstaticas.BD_VENDEDOR_ASOCIADO));
                        productos.setUnidadProducto(doc.getString(VariablesEstaticas.BD_UNIDAD_PRODUCTO));

                        double cantidadD = doc.getDouble(VariablesEstaticas.BD_CANTIDAD_PRODUCTO);
                        int cantidadInt = (int) cantidadD;
                        productos.setCantidadProducto(cantidadInt);

                        listProductos.add(productos);

                    }
                    adapterProductos.updateList(listProductos);
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ProductosActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void ListProductosMenorPrecio() {

        listProductos = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        adapterProductos = new AdapterProductos(listProductos, ProductosActivity.this);
        recyclerProductos.setHasFixedSize(true);
        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));
        recyclerProductos.setAdapter(adapterProductos);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collectionGroup(VariablesEstaticas.BD_PRODUCTOS).whereEqualTo(VariablesEstaticas.BD_PRODUCTO_ACTIVO, true).orderBy(VariablesEstaticas.BD_PRECIO_PRODUCTO, Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        ConstructorProductos productos = new ConstructorProductos();
                        productos.setIdProducto(doc.getId());
                        productos.setDescripcionProducto(doc.getString(VariablesEstaticas.BD_DESCRIPCION_PRODUCTO));
                        productos.setPrecioProducto(doc.getDouble(VariablesEstaticas.BD_PRECIO_PRODUCTO));
                        productos.setImagenProducto(doc.getString(VariablesEstaticas.BD_IMAGEN_PRODUCTO));
                        productos.setVendedor(doc.getString(VariablesEstaticas.BD_VENDEDOR_ASOCIADO));
                        productos.setUnidadProducto(doc.getString(VariablesEstaticas.BD_UNIDAD_PRODUCTO));

                        double cantidadD = doc.getDouble(VariablesEstaticas.BD_CANTIDAD_PRODUCTO);
                        int cantidadInt = (int) cantidadD;
                        productos.setCantidadProducto(cantidadInt);

                        listProductos.add(productos);

                    }
                    adapterProductos.updateList(listProductos);
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ProductosActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void ListProductosMasRecientes() {
        listProductos = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        adapterProductos = new AdapterProductos(listProductos, ProductosActivity.this);
        recyclerProductos.setHasFixedSize(true);
        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));
        recyclerProductos.setAdapter(adapterProductos);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collectionGroup(VariablesEstaticas.BD_PRODUCTOS).whereEqualTo(VariablesEstaticas.BD_PRODUCTO_ACTIVO, true).orderBy(VariablesEstaticas.BD_FECHA_INGRESO, Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        ConstructorProductos productos = new ConstructorProductos();
                        productos.setIdProducto(doc.getId());
                        productos.setDescripcionProducto(doc.getString(VariablesEstaticas.BD_DESCRIPCION_PRODUCTO));
                        productos.setPrecioProducto(doc.getDouble(VariablesEstaticas.BD_PRECIO_PRODUCTO));
                        productos.setImagenProducto(doc.getString(VariablesEstaticas.BD_IMAGEN_PRODUCTO));
                        productos.setVendedor(doc.getString(VariablesEstaticas.BD_VENDEDOR_ASOCIADO));
                        productos.setUnidadProducto(doc.getString(VariablesEstaticas.BD_UNIDAD_PRODUCTO));

                        double cantidadD = doc.getDouble(VariablesEstaticas.BD_CANTIDAD_PRODUCTO);
                        int cantidadInt = (int) cantidadD;
                        productos.setCantidadProducto(cantidadInt);

                        listProductos.add(productos);

                    }
                    adapterProductos.updateList(listProductos);
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ProductosActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void ListProductosMasViejos() {
        listProductos = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        adapterProductos = new AdapterProductos(listProductos, ProductosActivity.this);
        recyclerProductos.setHasFixedSize(true);
        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));
        recyclerProductos.setAdapter(adapterProductos);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collectionGroup(VariablesEstaticas.BD_PRODUCTOS).whereEqualTo(VariablesEstaticas.BD_PRODUCTO_ACTIVO, true).orderBy(VariablesEstaticas.BD_FECHA_INGRESO, Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        ConstructorProductos productos = new ConstructorProductos();
                        productos.setIdProducto(doc.getId());
                        productos.setDescripcionProducto(doc.getString(VariablesEstaticas.BD_DESCRIPCION_PRODUCTO));
                        productos.setPrecioProducto(doc.getDouble(VariablesEstaticas.BD_PRECIO_PRODUCTO));
                        productos.setImagenProducto(doc.getString(VariablesEstaticas.BD_IMAGEN_PRODUCTO));
                        productos.setVendedor(doc.getString(VariablesEstaticas.BD_VENDEDOR_ASOCIADO));
                        productos.setUnidadProducto(doc.getString(VariablesEstaticas.BD_UNIDAD_PRODUCTO));

                        double cantidadD = doc.getDouble(VariablesEstaticas.BD_CANTIDAD_PRODUCTO);
                        int cantidadInt = (int) cantidadD;
                        productos.setCantidadProducto(cantidadInt);

                        listProductos.add(productos);

                    }
                    adapterProductos.updateList(listProductos);
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ProductosActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }


    private void cargarFirestore () {
        listProductos = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        adapterProductos = new AdapterProductos(listProductos, ProductosActivity.this);
        recyclerProductos.setHasFixedSize(true);
        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));
        recyclerProductos.setAdapter(adapterProductos);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collectionGroup(VariablesEstaticas.BD_PRODUCTOS).whereEqualTo(VariablesEstaticas.BD_PRODUCTO_ACTIVO, true).orderBy(VariablesEstaticas.BD_DESCRIPCION_PRODUCTO, Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        ConstructorProductos productos = new ConstructorProductos();
                        productos.setIdProducto(doc.getId());
                        productos.setDescripcionProducto(doc.getString(VariablesEstaticas.BD_DESCRIPCION_PRODUCTO));
                        productos.setPrecioProducto(doc.getDouble(VariablesEstaticas.BD_PRECIO_PRODUCTO));
                        productos.setImagenProducto(doc.getString(VariablesEstaticas.BD_IMAGEN_PRODUCTO));
                        productos.setVendedor(doc.getString(VariablesEstaticas.BD_VENDEDOR_ASOCIADO));
                        productos.setUnidadProducto(doc.getString(VariablesEstaticas.BD_UNIDAD_PRODUCTO));

                        double cantidadD = doc.getDouble(VariablesEstaticas.BD_CANTIDAD_PRODUCTO);
                        int cantidadInt = (int) cantidadD;
                        productos.setCantidadProducto(cantidadInt);

                        listProductos.add(productos);

                    }
                    adapterProductos.updateList(listProductos);
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
        cargarFirestore();
        swRefresh.setRefreshing(false);
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

                if (name.getDescripcionProducto().toLowerCase().contains(userInput)) {

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
                    ListProductosMayorPrecio();
                } else if (opciones[which].equals("Menor precio")) {
                    ListProductosMenorPrecio();
                } else if (opciones[which].equals("Más recientes")) {
                    ListProductosMasRecientes();
                } else if (opciones[which].equals("Más antiguos")) {
                    ListProductosMasViejos();
                } else if (opciones[which].equals("Cancelar")){
                    dialog.dismiss();
                }
            }

        });
        dialog.show();
    }
}
