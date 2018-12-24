package com.example.hchirinos.elmejorprecio;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SupermercadoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Response.Listener<JSONObject>, Response.ErrorListener, AdapterView.OnItemSelectedListener, SearchView.OnQueryTextListener {

    private Spinner spinner_ordenar;
    TextView textSinConexion;
    Button buttonRetry;
    ImageView imageSinConexion;
    ConnectivityManager conexion;
    NetworkInfo networkInfo;

    ArrayList<ConstructorTiendas> listTiendas;
    RecyclerView recyclerTiendas;
    AdapterTiendas adapterTiendas;

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

        cargarWebservices ();

    }

    private void cargarWebservices() {

        String url = "http://192.168.3.34:8080/elmejorprecio/conectar_tienda.php";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);


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
                sortListSucursal();
            }


        } else if (seleccion.equals("A-Z (Tiendas)")) {

            if (listTiendas.isEmpty()) {
                Toast.makeText(this, "No hay lista cargada", Toast.LENGTH_SHORT).show();

            } else {
                sortListTiendas();
            }

         }

    }

    private void sortListSucursal() {

        Collections.sort(listTiendas, new Comparator<ConstructorTiendas>() {
            @Override
            public int compare(ConstructorTiendas o1, ConstructorTiendas o2) {
                return o1.getSucursal().compareTo(o2.getSucursal());
            }
        });
        adapterTiendas.notifyDataSetChanged();
        recyclerTiendas.setAdapter(adapterTiendas);
    }

    private void sortListTiendas() {

        Collections.sort(listTiendas, new Comparator<ConstructorTiendas>() {
            @Override
            public int compare(ConstructorTiendas o1, ConstructorTiendas o2) {
                return o1.getNombre_tienda().compareTo(o2.getNombre_tienda());
            }
        });
        adapterTiendas.notifyDataSetChanged();
        recyclerTiendas.setAdapter(adapterTiendas);
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(JSONObject response) {

        ConstructorTiendas tiendas = null;

        JSONArray json = response.optJSONArray("tienda");

        try {
            for (int i=0; i<json.length(); i++) {
                tiendas = new ConstructorTiendas();
                JSONObject jsonObject = null;
                jsonObject = json.getJSONObject(i);

                tiendas.setCod_tienda(jsonObject.optInt("cod_sup"));
                tiendas.setNombre_tienda(jsonObject.optString("nombre_sup"));
                tiendas.setSucursal(jsonObject.optString("sucursal"));
                tiendas.setImagen(jsonObject.optString("imagen"));
                tiendas.setLatitud(jsonObject.optDouble("latitud"));
                tiendas.setLongitud(jsonObject.optDouble("longitud"));


                listTiendas.add(tiendas);
            }

            //Envio de ArrayList al Adaptador
            adapterTiendas = new AdapterTiendas(listTiendas, this);
            recyclerTiendas.setAdapter(adapterTiendas);

        } catch (JSONException e) {
            e.printStackTrace();
        }

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
}

