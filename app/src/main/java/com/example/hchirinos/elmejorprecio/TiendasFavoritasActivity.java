package com.example.hchirinos.elmejorprecio;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
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

public class TiendasFavoritasActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Response.Listener<JSONObject>, Response.ErrorListener{

    TextView textSinConexion;
    Button buttonRetry;
    ImageView imageSinConexion;
    ConnectivityManager conexion;
    NetworkInfo networkInfo;


    ArrayList<ConstructorFavoritos> listFavoritos;
    RecyclerView recyclerFavoritos;
    AdapterFavoritos adapterFavoritos;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

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

        recyclerFavoritos = (RecyclerView)findViewById(R.id.recyclerView_TiendasFavoritas);
        recyclerFavoritos.setHasFixedSize(true);
        recyclerFavoritos.setLayoutManager(new GridLayoutManager(this.getApplicationContext(), 3));

        listFavoritos = new ArrayList<>();
        request = Volley.newRequestQueue(getApplicationContext());

        cargarWebservices ();
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
        getMenuInflater().inflate(R.menu.tiendas_favoritas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.bar_viewlist) {
            if (listFavoritos.isEmpty()){
                Toast.makeText(this, "No hay lista cargada", Toast.LENGTH_SHORT).show();
            } else {
                VistaGridList.visualizacion = VistaGridList.List;
                recyclerFavoritos.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
                adapterFavoritos = new AdapterFavoritos(listFavoritos, this);
                recyclerFavoritos.setAdapter(adapterFavoritos);
                Toast.makeText(this, "Lista", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.bar_viewgrid){
            if (listFavoritos.isEmpty()) {
                Toast.makeText(this, "No hay lista cargada", Toast.LENGTH_SHORT).show();
            } else {
                recyclerFavoritos.setLayoutManager(new GridLayoutManager(this.getApplicationContext(), 3));
                adapterFavoritos = new AdapterFavoritos(listFavoritos, this);
                recyclerFavoritos.setAdapter(adapterFavoritos);
                VistaGridList.visualizacion = VistaGridList.Grid;
                Toast.makeText(this, "Cuadricula", Toast.LENGTH_SHORT).show();
            }
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

    private void cargarWebservices () {
        String url = "https://chirinoshl.000webhostapp.com/elmejorprecio/conectar_favoritos.php";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);

    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(JSONObject response) {

        ConstructorFavoritos favoritos = null;

        JSONArray json = response.optJSONArray("favoritos");

        try {
            for (int i=0; i<json.length(); i++) {
                favoritos = new ConstructorFavoritos();
                JSONObject jsonObject = null;
                jsonObject = json.getJSONObject(i);

                favoritos.setCod_tienda(jsonObject.optInt("cod_sup"));
                favoritos.setNombre_tienda(jsonObject.optString("nombre_sup"));
                favoritos.setSucursal(jsonObject.optString("sucursal"));
                favoritos.setImagen(jsonObject.optString("imagen"));
                favoritos.setLatitud(jsonObject.optDouble("latitud"));
                favoritos.setLongitud(jsonObject.optDouble("longitud"));


                listFavoritos.add(favoritos);
            }

            //Envio de ArrayList al Adaptador
            adapterFavoritos = new AdapterFavoritos(listFavoritos, this);
            recyclerFavoritos.setAdapter(adapterFavoritos);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setButtonRetry (View view){
        this.recreate();
    }

}
