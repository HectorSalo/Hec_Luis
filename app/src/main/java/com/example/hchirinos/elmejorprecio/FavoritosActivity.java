package com.example.hchirinos.elmejorprecio;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.example.hchirinos.elmejorprecio.Adaptadores.AdapterFavoritos;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorFavoritos;
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

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class FavoritosActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    private NetworkInfo networkInfo;
    private ArrayList<ConstructorFavoritos> listFavoritos;
    private RecyclerView recyclerFavoritos;
    private AdapterFavoritos adapterFavoritos;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiendas_favoritas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        ConstraintLayout constraintLayout = findViewById(R.id.constraintFavoritos);
        listFavoritos = new ArrayList<>();
        recyclerFavoritos = (RecyclerView)findViewById(R.id.recyclerFavoritos);
        recyclerFavoritos.setHasFixedSize(true);
        recyclerFavoritos.setLayoutManager(new GridLayoutManager(this, 2));
        adapterFavoritos = new AdapterFavoritos(listFavoritos, this);
        recyclerFavoritos.setAdapter(adapterFavoritos);
        progressBar = findViewById(R.id.progressBarFavoritos);


        ConnectivityManager conexion = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = conexion.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            cargarFavoritos();
        } else {
            Snackbar snackbar = Snackbar.make(constraintLayout, "Sin conexión", Snackbar.LENGTH_INDEFINITE).setAction("Reintentar", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recreate();
                }
            });
            snackbar.show();
            cargarFavoritos();
        }


    }

    private void cargarFavoritos() {

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
        int id = item.getItemId();

        if (id == R.id.nav_productos) {
            // Handle the camera action
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
            Intent ir_inicio = new Intent(this, HomeActivity.class);
            startActivity(ir_inicio);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
