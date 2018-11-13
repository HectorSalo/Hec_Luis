package com.example.hchirinos.elmejorprecio;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.SearchView;
import android.widget.TextView;

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

public class lista_compras extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener, SearchView.OnQueryTextListener, Response.Listener<JSONObject>, Response.ErrorListener  {

    TextView textView_total_compras;
    //TextView textView_precio_compras;

    ArrayList<ConstructorCompras> listCompras;
    RecyclerView recyclerCompras;
    AdapterCompras adapterCompras;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_compras);
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

        textView_total_compras = (TextView)findViewById(R.id.textView_total_compras);



        recyclerCompras = (RecyclerView)findViewById(R.id.recyclerView_listcompras);
        recyclerCompras.setHasFixedSize(true);
        recyclerCompras.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));

        listCompras = new ArrayList<>();
        request = Volley.newRequestQueue(getApplicationContext());

        cargarWebServices2 ();



    }


    private void cargarWebServices2() {

        String url = "http://192.168.3.34:8080/elmejorprecio/conectar_compras.php";

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
        getMenuInflater().inflate(R.menu.lista_compras, menu);
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
            Intent ir_supermercado = new Intent(this, SupermercadoActivity.class);
            startActivity(ir_supermercado);

        } else if (id == R.id.nav_favorito) {

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
        return false;
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(JSONObject response) {

        ConstructorCompras compras = null;

        JSONArray json = response.optJSONArray("compras");

        try {
            for (int i=0; i<json.length(); i++) {
                compras = new ConstructorCompras();
                JSONObject jsonObject = null;
                jsonObject = json.getJSONObject(i);

                compras.setCod_plu_compras((jsonObject.optInt("cod_plu")));
                compras.setNombre_producto_compras(jsonObject.optString("nombre_plu"));
                compras.setMarca_producto_compras(jsonObject.optString("marca_plu"));
                compras.setPrecio_producto_compras(jsonObject.optDouble("precio_plu"));

                listCompras.add(compras);


            }

            //Envio de ArrayList al Adaptador
            adapterCompras = new AdapterCompras(listCompras, this);
            recyclerCompras.setAdapter(adapterCompras);

            pruebasuma();


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void pruebasuma() {

        double suma = 0;


        for (int i = 0; i<listCompras.size(); i++) {

            double numero = listCompras.get(i).getPrecio_producto_compras();
            suma = suma + numero;
        }

        String total = "" + suma;
        textView_total_compras.setText(total);
    }
}
