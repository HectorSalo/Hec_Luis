package com.example.hchirinos.elmejorprecio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.example.hchirinos.elmejorprecio.Adaptadores.AdapterFavoritos;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.ArrayList;

public class TiendasFavoritasActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Response.Listener<JSONObject>, Response.ErrorListener{

    private TextView textSinConexion;
    private Button buttonRetry;
    private ImageView imageSinConexion;
    private ConnectivityManager conexion;
    private NetworkInfo networkInfo;
    private AdminSQLiteHelper conect;

    private ArrayList<ConstructorFavoritos> listFavoritos;
    private ArrayList<String> idTiendas;
    private RecyclerView recyclerFavoritos;
    private AdapterFavoritos adapterFavoritos;
    private ProgressDialog progress;

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

        recyclerFavoritos = (RecyclerView)findViewById(R.id.recyclerView_TiendasFavoritas);
        recyclerFavoritos.setHasFixedSize(true);
        recyclerFavoritos.setLayoutManager(new GridLayoutManager(this.getApplicationContext(), 3));

        listFavoritos = new ArrayList<>();
        idTiendas = new ArrayList<>();
        request = Volley.newRequestQueue(getApplicationContext());

        adapterFavoritos = new AdapterFavoritos(listFavoritos, this);
        recyclerFavoritos.setAdapter(adapterFavoritos);
        progress = new ProgressDialog(TiendasFavoritasActivity.this);
        progress.setMessage("Cargando...");
        progress.show();
        cargarFavoritas();


    }

    private void cargarFavoritas() {
        listFavoritos = new ArrayList<>();
        SQLiteDatabase db = conect.getWritableDatabase();
        FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
        CollectionReference reference = dbFirestore.collection("Tiendas");

        Cursor cursor =db.rawQuery("SELECT * FROM tiendas", null);



            while (cursor.moveToNext()) {
                String idTienda = cursor.getString(0);

                reference.document(idTienda).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            ConstructorFavoritos tiendas = new ConstructorFavoritos();
                            tiendas.setCod_tienda(doc.getId());
                            tiendas.setNombre_tienda(doc.getString("comercio"));
                            tiendas.setSucursal(doc.getString("sucursal"));
                            tiendas.setImagen(doc.getString("imagen"));

                            listFavoritos.add(tiendas);
                            adapterFavoritos.updateList(listFavoritos);
                            progress.dismiss();
                        }
                    }
                });

            }
            progress.dismiss();
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
        } else if (id == R.id.bar_favoritas) {
            Intent myIntent = new Intent(this, SupermercadoActivity.class);
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
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(JSONObject response) {


    }

    public void setButtonRetry (View view){
        this.recreate();
    }

}
