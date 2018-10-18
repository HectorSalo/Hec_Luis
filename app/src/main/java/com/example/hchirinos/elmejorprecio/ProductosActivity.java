package com.example.hchirinos.elmejorprecio;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class ProductosActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Spinner spinner_ordenar;
    private FloatingActionButton fab_agregar, fab_producto, fab_supermercado;
    private Animation fabOpen, fabClose, rotate_forward, rotate_backward;
    private LinearLayout layout_producto, layout_supermercado;
    boolean isOpen= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*Botones flotantes
        fab_agregar = (FloatingActionButton) findViewById(R.id.fab_agregar);
        fab_producto = (FloatingActionButton)findViewById(R.id.fab_producto);
        fab_supermercado = (FloatingActionButton)findViewById(R.id.fab_supermercado);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_closed);
        rotate_forward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        layout_producto = (LinearLayout)findViewById(R.id.linearLayout_producto);
        layout_supermercado = (LinearLayout)findViewById(R.id.linearLayout_supermercado);




        fab_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
            }
        });

        layout_producto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_producto();
            }
        });


        layout_supermercado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_supermercado();
            }
        }); */



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        spinner_ordenar = (Spinner)findViewById(R.id.spinner_ordenar);

        String [] opciones_ordenar = {"Menor a mayor", "Mayor a menor", "A-Z"};
        ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, R.layout.personalizar_spinner_ordenar, opciones_ordenar);
        spinner_ordenar.setAdapter(adapter);
    }

    //Metodo para floating_button

    /*private void animateFab (){

        if (isOpen){
            fab_agregar.startAnimation(rotate_forward);
            layout_supermercado.startAnimation(fabClose);
            layout_producto.startAnimation(fabClose);
            layout_supermercado.setClickable(false);
            layout_producto.setClickable(false);
            isOpen = false;
        } else {
            fab_agregar.startAnimation(rotate_backward);
            layout_supermercado.startAnimation(fabOpen);
            layout_producto.startAnimation(fabOpen);
            layout_supermercado.setClickable(true);
            layout_producto.setClickable(true);
            isOpen = true;
        }
    } */

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

        } else if (id == R.id.nav_configuracion){

        } else if (id == R.id.nav_inicio) {
            Intent ir_inicio = new Intent(this, HomeActivity.class);
            startActivity(ir_inicio);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void add_producto (){
        AlertDialog.Builder add_producto_alert = new AlertDialog.Builder(ProductosActivity.this);
        View add_producto_view = getLayoutInflater().inflate(R.layout.activity_add_producto,null);
        add_producto_alert.setView(add_producto_view);
        AlertDialog dialog = add_producto_alert.create();
        dialog.show();
        }


    public void add_supermercado () {
        AlertDialog.Builder add_supermercado_alert = new AlertDialog.Builder(ProductosActivity.this);
        View add_supermercado_view = getLayoutInflater().inflate(R.layout.activity_add__supermercado,null);
        add_supermercado_alert.setView(add_supermercado_view);
        AlertDialog dialog = add_supermercado_alert.create();
        dialog.show();
    }
}
