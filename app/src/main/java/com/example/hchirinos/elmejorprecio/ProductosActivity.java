package com.example.hchirinos.elmejorprecio;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductosActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Response.Listener<JSONObject>, Response.ErrorListener, AdapterView.OnItemSelectedListener, SearchView.OnQueryTextListener {

    private Spinner spinner_ordenar;

    ArrayList<ConstructorProductos> listProductos;
    RecyclerView recyclerProductos;
    AdapterProductos adapterProductos;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;




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

        spinner_ordenar = (Spinner)findViewById(R.id.spinner_ordenar);
        String [] opciones_ordenar = {"Ordenar", "Menor a mayor", "Mayor a menor", "A-Z"};
        ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, R.layout.personalizar_spinner_ordenar, opciones_ordenar);
        spinner_ordenar.setAdapter(adapter);
        spinner_ordenar.setOnItemSelectedListener(this);


        recyclerProductos = (RecyclerView)findViewById(R.id.recyclerView_Productos);
        recyclerProductos.setHasFixedSize(true);
        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));

        listProductos = new ArrayList<>();
        request = Volley.newRequestQueue(getApplicationContext());

        cargarWebservices ();

    }

    private void cargarWebservices() {

        String url = "http://192.168.3.34:8080/elmejorprecio/conectar.php";

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
        getMenuInflater().inflate(R.menu.productos, menu);

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
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(JSONObject response) {

        ConstructorProductos productos = null;

        JSONArray json = response.optJSONArray("producto");

        try {
            for (int i=0; i<json.length(); i++) {
                productos = new ConstructorProductos();
                JSONObject jsonObject = null;
                jsonObject = json.getJSONObject(i);

                productos.setCodigo_plu((jsonObject.optInt("cod_plu")));
                productos.setNombre_producto(jsonObject.optString("nombre_plu"));
                productos.setMarca_producto(jsonObject.optString("marca_plu"));
                productos.setPrecio_producto(jsonObject.optDouble("precio_plu"));
                productos.setImagen_producto(jsonObject.optString("imagen"));

                listProductos.add(productos);
            }

            //Envio de ArrayList al Adaptador
            adapterProductos = new AdapterProductos(listProductos, this);
            recyclerProductos.setAdapter(adapterProductos);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //spinner

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String seleccion = spinner_ordenar.getSelectedItem().toString();

        if (seleccion.equals("Menor a mayor")){

            if (listProductos.isEmpty()) {
                Toast.makeText(this, "No hay lista cargada", Toast.LENGTH_SHORT).show();

            } else {
                sortListProductos_menor();
            }

        } else if (seleccion.equals("Mayor a menor")) {

            if (listProductos.isEmpty()) {
                Toast.makeText(this, "No hay lista cargada", Toast.LENGTH_SHORT).show();

            } else {
                sortListProductos_mayor();
            }

        } else if (seleccion.equals("A-Z")){
            if (listProductos.isEmpty()) {
                Toast.makeText(this, "No hay lista cargada", Toast.LENGTH_SHORT).show();

            } else {
                sortlistProductos();
            }

        }


    }

    // Ordenar lista
    private void sortListProductos_mayor() {

        Collections.sort(listProductos, new Comparator<ConstructorProductos>() {
            @Override
            public int compare(ConstructorProductos o1, ConstructorProductos o2) {

                return Double.compare(o2.getPrecio_producto(), o1.getPrecio_producto());
            }
        });

        adapterProductos.notifyDataSetChanged();
        recyclerProductos.setAdapter(adapterProductos);
    }

    private void sortListProductos_menor() {

        Collections.sort(listProductos, new Comparator<ConstructorProductos>() {
            @Override
            public int compare(ConstructorProductos o1, ConstructorProductos o2) {

                return Double.compare(o1.getPrecio_producto(), o2.getPrecio_producto());
            }
        });

        adapterProductos.notifyDataSetChanged();
        recyclerProductos.setAdapter(adapterProductos);
    }


    private void sortlistProductos() {
        Collections.sort(listProductos, new Comparator<ConstructorProductos>() {
            @Override
            public int compare(ConstructorProductos o1, ConstructorProductos o2) {
                return o1.getNombre_producto().compareTo(o2.getNombre_producto());
            }
        });
        adapterProductos.notifyDataSetChanged();
        recyclerProductos.setAdapter(adapterProductos);
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

                if (name.getNombre_producto().toLowerCase().contains(userInput) || name.getMarca_producto().toLowerCase().contains(userInput)) {

                    newList.add(name);
                }
            }

            adapterProductos.updateList(newList);

        }
        return true;
    }


}
