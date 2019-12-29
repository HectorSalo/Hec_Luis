package com.example.hchirinos.elmejorprecio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.View;

import com.example.hchirinos.elmejorprecio.Adaptadores.AdapterFavoritos;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorProductos;
import com.example.hchirinos.elmejorprecio.SQLite.ConectSQLiteHelper;
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.example.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FavoritosActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private NetworkInfo networkInfo;
    private ArrayList<ConstructorProductos> listFavoritos;
    private ArrayList<String> listaEnviarFavoritos;
    private RecyclerView recyclerFavoritos;
    private AdapterFavoritos adapterFavoritos;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ConstraintLayout constraintLayout;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        constraintLayout = findViewById(R.id.constraintFavoritos);
        listFavoritos = new ArrayList<>();
        recyclerFavoritos = (RecyclerView)findViewById(R.id.recyclerFavoritos);
        recyclerFavoritos.setHasFixedSize(true);
        recyclerFavoritos.setLayoutManager(new LinearLayoutManager(this));
        adapterFavoritos = new AdapterFavoritos(listFavoritos, this);
        recyclerFavoritos.setAdapter(adapterFavoritos);
        progressBar = findViewById(R.id.progressBarFavoritos);
        swipeRefreshLayout = findViewById(R.id.swpRefreshFavoritos);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean temaClaro = sharedPreferences.getBoolean("temaClaro", true);
        if (!temaClaro) {

            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }


        ConnectivityManager conexion = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = conexion.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            llenarLista();
        } else {
            Snackbar snackbar = Snackbar.make(constraintLayout, "Sin conexión", Snackbar.LENGTH_INDEFINITE).setAction("Reintentar", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recreate();
                }
            });
            snackbar.show();
            llenarLista();
        }

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    public void llenarLista() {
        progressBar.setVisibility(View.VISIBLE);

        listaEnviarFavoritos = new ArrayList<>();

        ConectSQLiteHelper conectSQLiteHelper = new ConectSQLiteHelper(getApplicationContext(), VariablesEstaticas.BD_PRODUCTOS, null, VariablesEstaticas.VERSION_SQLITE);
        SQLiteDatabase db = conectSQLiteHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from " + VariablesEstaticas.BD_FAVORITOS , null);

        while (cursor.moveToNext()) {
            listaEnviarFavoritos.add(cursor.getString(0));
        }

        if (!listaEnviarFavoritos.isEmpty()) {
            for (int i = 0; i < listaEnviarFavoritos.size(); i++) {
                cargarFavoritos(listaEnviarFavoritos.get(i));
            }

        } else {
            progressBar.setVisibility(View.GONE);
            Snackbar snackbar = Snackbar.make(constraintLayout, "No tiene Favoritos", Snackbar.LENGTH_INDEFINITE).setAction("Agregar", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VariablesGenerales.verProductos = true;
                    startActivity(new Intent(FavoritosActivity.this, ProductosActivity.class));
                }
            });
            snackbar.show();
        }


    }

    private void cargarFavoritos(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collectionGroup(VariablesEstaticas.BD_PRODUCTOS).whereEqualTo(VariablesEstaticas.BD_ID_PRODUCTO_FAVORITO, id).orderBy(VariablesEstaticas.BD_PRECIO_PRODUCTO, Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        ConstructorProductos productos = new ConstructorProductos();
                        productos.setIdProducto(doc.getId());
                        productos.setDescripcionProducto(doc.getString(VariablesEstaticas.BD_DESCRIPCION_PRODUCTO));
                        productos.setPrecioProducto(doc.getDouble(VariablesEstaticas.BD_PRECIO_PRODUCTO));
                        productos.setImagenProducto(doc.getString(VariablesEstaticas.BD_IMAGEN_PRODUCTO));
                        productos.setVendedor(doc.getString(VariablesEstaticas.BD_VENDEDOR_ASOCIADO));
                        productos.setUnidadProducto(doc.getString(VariablesEstaticas.BD_UNIDAD_PRODUCTO));
                        productos.setEstadoProducto(doc.getString(VariablesEstaticas.BD_ESTADO_PRODUCTO));

                        double cantidadD = doc.getDouble(VariablesEstaticas.BD_CANTIDAD_PRODUCTO);
                        int cantidadInt = (int) cantidadD;
                        productos.setCantidadProducto(cantidadInt);

                        listFavoritos.add(productos);
                    }
                    adapterFavoritos.updateList(listFavoritos);
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(FavoritosActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.favoritos, menu);
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
            Intent ir_supermercado = new Intent(this, VendedoresActivity.class);
            startActivity(ir_supermercado);
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_chat) {
            startActivity(new Intent(this, ChatActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_favorito) {
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

        if (listFavoritos.isEmpty()) {
            Toast.makeText(this, "No hay lista cargada", Toast.LENGTH_SHORT).show();
        } else {
            String userInput = newText.toLowerCase();
            ArrayList<ConstructorProductos> newList = new ArrayList<>();

            for (ConstructorProductos name : listFavoritos) {

                if (name.getDescripcionProducto().toLowerCase().contains(userInput)) {

                    newList.add(name);
                }
            }

            adapterFavoritos.updateList(newList);

        }

        return false;
    }

    @Override
    public void onRefresh() {
        llenarLista();
        swipeRefreshLayout.setRefreshing(false);
    }
}
