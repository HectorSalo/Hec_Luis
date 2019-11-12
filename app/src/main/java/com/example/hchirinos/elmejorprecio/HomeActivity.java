package com.example.hchirinos.elmejorprecio;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.view.View;

import com.example.hchirinos.elmejorprecio.Adaptadores.AdapterProductos;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorProductos;
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NetworkInfo networkInfo;
    private RecyclerView recyclerRecientes, recyclerCambioPrecio, recyclerOfertas;
    private AdapterProductos adapterRecientes, adapterCambioPrecio, adapterOferta;
    private ArrayList<ConstructorProductos> listRecientes, listCambioPrecio, listOferta;
    private ProgressBar progressBarRecientes, progressBarCambioPrecio, progressBarOfertas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

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

    private void cargarCambioPrecio() {
        listCambioPrecio = new ArrayList<>();

        adapterCambioPrecio = new AdapterProductos(listCambioPrecio, HomeActivity.this);
        recyclerCambioPrecio.setHasFixedSize(true);
        recyclerCambioPrecio.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerCambioPrecio.setAdapter(adapterCambioPrecio);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collectionGroup(VariablesEstaticas.BD_PRODUCTOS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

        db.collectionGroup(VariablesEstaticas.BD_PRODUCTOS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

        db.collectionGroup(VariablesEstaticas.BD_PRODUCTOS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.bar_buscar) {
            return true;

            }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_productos) {
            Intent ir_productos = new Intent (this, ProductosActivity.class);
            startActivity(ir_productos);

        } else if (id == R.id.nav_supermercados) {
            Intent ir_supermercado = new Intent(this, VendedoresActivity.class);
            startActivity(ir_supermercado);

        } else if (id == R.id.nav_favorito) {
            Intent irFavoritos = new Intent(this, FavoritosActivity.class);
            startActivity(irFavoritos);

        } else if (id == R.id.nav_configuracion){

        } else if (id == R.id.nav_inicio) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
