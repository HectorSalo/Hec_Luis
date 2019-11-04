package com.example.hchirinos.elmejorprecio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.ArrayList;

public class SupermercadoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Response.Listener<JSONObject>, Response.ErrorListener, AdapterView.OnItemSelectedListener, SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private Spinner spinner_ordenar;
    private TextView textSinConexion;
    private Button buttonRetry;
    private ImageView imageSinConexion;
    private ConnectivityManager conexion;
    private NetworkInfo networkInfo;
    private ProgressDialog progress;
    private SwipeRefreshLayout swRefresh;

    private ArrayList<ConstructorTiendas> listTiendas;
    private RecyclerView recyclerTiendas;
    private AdapterTiendas adapterTiendas;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;


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

        spinner_ordenar = (Spinner)findViewById(R.id.spinner_ordenar);
        String [] opciones_ordenar = {"Ordenar", "A-Z (Sucursal)", "A-Z (Tiendas)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.personalizar_spinner_ordenar, opciones_ordenar);
        spinner_ordenar.setAdapter(adapter);
        spinner_ordenar.setOnItemSelectedListener(this);

        textSinConexion = (TextView)findViewById(R.id.textSinConexion);
        buttonRetry = (Button)findViewById(R.id.buttonRetry);
        imageSinConexion = (ImageView)findViewById(R.id.imageSinConexion);
        conexion = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = conexion.getActiveNetworkInfo();
        swRefresh = (SwipeRefreshLayout) findViewById(R.id.swpRefresh);

        if (networkInfo != null && networkInfo.isConnected()) {
            textSinConexion.setVisibility(View.INVISIBLE);
            buttonRetry.setVisibility(View.INVISIBLE);
            imageSinConexion.setVisibility(View.INVISIBLE);
        } else {
            textSinConexion.setVisibility(View.VISIBLE);
            buttonRetry.setVisibility(View.VISIBLE);
            imageSinConexion.setVisibility(View.VISIBLE);
        }

        recyclerTiendas = (RecyclerView)findViewById(R.id.recyclerView_tiendas);
        recyclerTiendas.setHasFixedSize(true);
        recyclerTiendas.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));

        listTiendas = new ArrayList<>();
        request = Volley.newRequestQueue(getApplicationContext());

        swRefresh.setOnRefreshListener(this);
        progress = new ProgressDialog(SupermercadoActivity.this);
        progress.setMessage("Cargando...");
        progress.show();

        cargarFirestore ();
        adapterTiendas = new AdapterTiendas(listTiendas, this);
        recyclerTiendas.setAdapter(adapterTiendas);

    }

    private void cargarFirestore() {
        listTiendas = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Tiendas");

        Query query = reference.orderBy("comercio", Query.Direction.ASCENDING);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        ConstructorTiendas tiendas = new ConstructorTiendas();
                        tiendas.setCod_tienda(doc.getId());
                        tiendas.setNombre_tienda(doc.getString("comercio"));
                        tiendas.setSucursal(doc.getString("sucursal"));
                        tiendas.setImagen(doc.getString("imagen"));
                        tiendas.setLatitud(doc.getGeoPoint("ubicacion").getLatitude());
                        tiendas.setLongitud(doc.getGeoPoint("ubicacion").getLongitude());
                        listTiendas.add(tiendas);

                    }
                    adapterTiendas.updateList(listTiendas);
                    progress.dismiss();
                } else {
                    Toast.makeText(SupermercadoActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.supermercado, menu);
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
        } else if (id == R.id.bar_Tienda) {
            Intent myIntent = new Intent(this, TiendasFavoritasActivity.class);
            startActivity(myIntent);
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
            Intent ir_supermercado = new Intent(this, SupermercadoActivity.class);
            startActivity(ir_supermercado);

        } else if (id == R.id.nav_favorito) {
            Intent irFavoritos = new Intent(this, TiendasFavoritasActivity.class);
            startActivity(irFavoritos);

        } else if (id == R.id.nav_listacompras) {
            Intent ir_lista_compras = new Intent(this, lista_compras.class);
            startActivity(ir_lista_compras);

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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String seleccion = spinner_ordenar.getSelectedItem().toString();

        if (seleccion.equals("A-Z (Sucursal)")){

            if (listTiendas.isEmpty()) {
                Toast.makeText(this, "No hay lista cargada", Toast.LENGTH_SHORT).show();

            } else {
                progress.setMessage("Cargando...");
                progress.show();
                sortListSucursal();
            }


        } else if (seleccion.equals("A-Z (Tiendas)")) {

            if (listTiendas.isEmpty()) {
                Toast.makeText(this, "No hay lista cargada", Toast.LENGTH_SHORT).show();

            } else {
                progress.setMessage("Cargando...");
                progress.show();
                sortListTiendas();
            }

         }

    }

    private void sortListSucursal() {

        listTiendas = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Tiendas");

        Query query = reference.orderBy("sucursal", Query.Direction.ASCENDING);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        ConstructorTiendas tiendas = new ConstructorTiendas();
                        tiendas.setCod_tienda(doc.getId());
                        tiendas.setNombre_tienda(doc.getString("comercio"));
                        tiendas.setSucursal(doc.getString("sucursal"));
                        tiendas.setImagen(doc.getString("imagen"));
                        tiendas.setLatitud(doc.getGeoPoint("ubicacion").getLatitude());
                        tiendas.setLongitud(doc.getGeoPoint("ubicacion").getLongitude());
                        listTiendas.add(tiendas);

                    }
                    adapterTiendas.updateList(listTiendas);
                    progress.dismiss();
                } else {
                    Toast.makeText(SupermercadoActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sortListTiendas() {

        listTiendas = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Tiendas");

        Query query = reference.orderBy("comercio", Query.Direction.ASCENDING);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        ConstructorTiendas tiendas = new ConstructorTiendas();
                        tiendas.setCod_tienda(doc.getId());
                        tiendas.setNombre_tienda(doc.getString("comercio"));
                        tiendas.setSucursal(doc.getString("sucursal"));
                        tiendas.setImagen(doc.getString("imagen"));
                        tiendas.setLatitud(doc.getGeoPoint("ubicacion").getLatitude());
                        tiendas.setLongitud(doc.getGeoPoint("ubicacion").getLongitude());
                        listTiendas.add(tiendas);

                    }
                    adapterTiendas.updateList(listTiendas);
                    progress.dismiss();
                } else {
                    Toast.makeText(SupermercadoActivity.this, "Error al cargar lista", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(JSONObject response) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (listTiendas.isEmpty()) {
            Toast.makeText(this, "No hay lista cargada", Toast.LENGTH_SHORT).show();
        } else {
            String userInput = newText.toLowerCase();
            ArrayList<ConstructorTiendas> newList = new ArrayList<>();

            for (ConstructorTiendas name : listTiendas) {

                if (name.getNombre_tienda().toLowerCase().contains(userInput) || name.getSucursal().toLowerCase().contains(userInput)) {

                    newList.add(name);
                }
            }

            adapterTiendas.updateList(newList);

        }
        return true;
    }

    public void setButtonRetry (View view){
        this.recreate();
    }

    @Override
    public void onRefresh() {
        cargarFirestore();
        swRefresh.setRefreshing(false);
    }
}

