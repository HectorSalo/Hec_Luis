package com.example.hchirinos.elmejorprecio;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.example.hchirinos.elmejorprecio.Adaptadores.AdapterVendedores;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorVendedores;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class VendedoresActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private NetworkInfo networkInfo;
    private SwipeRefreshLayout swRefresh;
    private ArrayList<ConstructorVendedores> listVendedores;
    private RecyclerView recyclerVendedores;
    private AdapterVendedores adapterVendedores;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supermercado);
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

        progressBar = findViewById(R.id.progressBarVendedores);
        ConstraintLayout constraintLayout = findViewById(R.id.constraintVendedores);

        recyclerVendedores = (RecyclerView)findViewById(R.id.recyclerViewVendedores);
        recyclerVendedores.setHasFixedSize(true);
        recyclerVendedores.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));

        listVendedores = new ArrayList<>();
        adapterVendedores = new AdapterVendedores(listVendedores, this);
        recyclerVendedores.setAdapter(adapterVendedores);

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

        adapterVendedores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VariablesGenerales.nombreInfoVendedor = listVendedores.get(recyclerVendedores.getChildAdapterPosition(v)).getNombreVendedor();
                VariablesGenerales.telefonoInfoVendedor = listVendedores.get(recyclerVendedores.getChildAdapterPosition(v)).getTelefonoVendedor();
                VariablesGenerales.correoInfoVendedor = listVendedores.get(recyclerVendedores.getChildAdapterPosition(v)).getCorreoVendedor();
                VariablesGenerales.infoProducto = false;
                VariablesGenerales.infoVendedor = true;
                startActivity(new Intent(VendedoresActivity.this, InfoActivity.class));
            }
        });

    }

    private void cargarFirestore() {
        listVendedores = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        adapterVendedores = new AdapterVendedores(listVendedores, this);
        recyclerVendedores.setHasFixedSize(true);
        recyclerVendedores.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerVendedores.setAdapter(adapterVendedores);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collectionGroup(VariablesEstaticas.BD_DETALLES_VENDEDOR).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

                        listVendedores.add(vendedor);

                    }
                    adapterVendedores.updateList(listVendedores);
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
        int id = item.getItemId();

        if (id == R.id.nav_productos) {
            Intent ir_productos = new Intent (this, ProductosActivity.class);
            startActivity(ir_productos);

        } else if (id == R.id.nav_supermercados) {

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

        if (listVendedores.isEmpty()) {
            Toast.makeText(this, "No hay lista cargada", Toast.LENGTH_SHORT).show();
        } else {
            String userInput = newText.toLowerCase();
            ArrayList<ConstructorVendedores> newList = new ArrayList<>();

            for (ConstructorVendedores name : listVendedores) {

                if (name.getNombreVendedor().toLowerCase().contains(userInput)) {

                    newList.add(name);
                }
            }

            adapterVendedores.updateList(newList);

        }
        return true;
    }

    @Override
    public void onRefresh() {
        cargarFirestore();
        swRefresh.setRefreshing(false);
    }


}
