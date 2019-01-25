package com.example.hchirinos.elmejorprecio;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.NumberPicker;
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

public class lista_compras extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener, SearchView.OnQueryTextListener, Response.Listener<JSONObject>, Response.ErrorListener  {

    private TextView textView_total_compras, textSinConexion;
    private Button buttonRetry;
    private ImageView imageSinConexion;
    private ConnectivityManager conexion;
    private NetworkInfo networkInfo;
    private AdminSQLiteHelper conect;
    private ArrayList<ConstructorCompras> listCompras;
    private RecyclerView recyclerCompras;
    private AdapterCompras adapterCompras;

    private RequestQueue request;
    private JsonObjectRequest jsonObjectRequest;


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

        textSinConexion = (TextView)findViewById(R.id.textSinConexion);
        buttonRetry = (Button)findViewById(R.id.buttonRetry);
        imageSinConexion = (ImageView)findViewById(R.id.imageSinConexion);
        conexion = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = conexion.getActiveNetworkInfo();

        conect = new AdminSQLiteHelper(this, "MyList", null, AdminSQLiteHelper.VERSION);

        if (networkInfo != null && networkInfo.isConnected()) {
            textSinConexion.setVisibility(View.INVISIBLE);
            buttonRetry.setVisibility(View.INVISIBLE);
            imageSinConexion.setVisibility(View.INVISIBLE);
        } else {
            textSinConexion.setVisibility(View.VISIBLE);
            buttonRetry.setVisibility(View.VISIBLE);
            imageSinConexion.setVisibility(View.VISIBLE);
        }
        LinearLayoutManager llM = new LinearLayoutManager(this.getApplicationContext());
        recyclerCompras = (RecyclerView)findViewById(R.id.recyclerView_listcompras);
        recyclerCompras.setHasFixedSize(true);
        recyclerCompras.setLayoutManager(llM);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, llM.getOrientation());
        recyclerCompras.addItemDecoration(dividerItemDecoration);

        listCompras = new ArrayList<>();
        request = Volley.newRequestQueue(getApplicationContext());

        cargarList ();


    }


    public void cargarList() {
        SQLiteDatabase db = conect.getWritableDatabase();

        Cursor cursor =db.rawQuery("SELECT * FROM compras", null);

        while (cursor.moveToNext()) {
            ConstructorCompras plu = new ConstructorCompras();
            plu.setCod_plu_compras(cursor.getString(0));
            plu.setNombre_producto_compras(cursor.getString(1));
            plu.setMarca_producto_compras(cursor.getString(2));
            plu.setPrecio_producto_compras(cursor.getDouble(3));


            listCompras.add(plu);
        }

        adapterCompras = new AdapterCompras(listCompras, this);
        recyclerCompras.setAdapter(adapterCompras);

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
        if (id == R.id.bar_refresh) {
            Toast.makeText(this, "Lista actualizada", Toast.LENGTH_SHORT).show();
            this.recreate();
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


    }

    public void totalsuma() {

        double suma = 0;


        for (int i = 0; i<listCompras.size(); i++) {

            double numero = listCompras.get(i).getPrecio_producto_compras();
            suma = suma + numero;
        }

        String total = "" + suma;
        textView_total_compras.setText(total);

    }

    public void setButtonRetry (View view){
        this.recreate();
    }


}
