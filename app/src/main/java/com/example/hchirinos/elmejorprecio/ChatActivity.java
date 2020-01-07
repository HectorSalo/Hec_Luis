package com.example.hchirinos.elmejorprecio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.hchirinos.elmejorprecio.Clases.UsuarioEnLinea;
import com.example.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.example.hchirinos.elmejorprecio.ui.FragmentChat.InterfaceRecyclerViewConversaciones;
import com.example.hchirinos.elmejorprecio.ui.FragmentChat.UsuariosChatFragment;
import com.example.hchirinos.elmejorprecio.ui.FragmentChat.dummy.DummyContent;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuItem;

import com.example.hchirinos.elmejorprecio.Adaptadores.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.Date;

public class ChatActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, UsuariosChatFragment.OnListFragmentInteractionListener {

    private NavigationView navigationView;
    private UsuarioEnLinea usuarioEnLinea;
    private String usuario;
    private Calendar calendario;
    private InterfaceRecyclerViewConversaciones interfaceRecyclerViewConversaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        usuarioEnLinea = new UsuarioEnLinea();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        usuario = user.getUid();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean temaClaro = sharedPreferences.getBoolean("temaClaro", true);
        if (!temaClaro) {

            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }

    }


    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Menu menu = navigationView.getMenu();

        int id = menuItem.getItemId();

        if (id == R.id.nav_catalogos) {
            MenuItem itemServicios = menu.findItem(R.id.nav_servicios);
            MenuItem itemProductos = menu.findItem(R.id.nav_productos);

            SpannableString textServicios = new SpannableString(itemServicios.getTitle());
            textServicios.setSpan(new TextAppearanceSpan(this, R.style.TextAppearanceCatalogo), 0, textServicios.length(), 0);
            itemServicios.setTitle(textServicios);

            SpannableString textProductos = new SpannableString(itemProductos.getTitle());
            textProductos.setSpan(new TextAppearanceSpan(this, R.style.TextAppearanceCatalogo), 0, textProductos.length(), 0);
            itemProductos.setTitle(textProductos);

            if (menu.findItem(R.id.nav_productos).isVisible()) {
                menu.findItem(R.id.nav_servicios).setVisible(false);
                menu.findItem(R.id.nav_productos).setVisible(false);
            } else {
                menu.findItem(R.id.nav_servicios).setVisible(true);
                menu.findItem(R.id.nav_productos).setVisible(true);
            }

        } else if (id == R.id.nav_productos) {
            startActivity(new Intent(this, ProductosActivity.class));
            drawer.closeDrawer(GravityCompat.START);
            VariablesGenerales.verProductos = true;
            VariablesGenerales.verResultadosBuscar = false;
        } else if (id == R.id.nav_servicios) {
            startActivity(new Intent(this, ProductosActivity.class));
            drawer.closeDrawer(GravityCompat.START);
            VariablesGenerales.verProductos = false;
            VariablesGenerales.verResultadosBuscar = false;
        } else if (id == R.id.nav_supermercados) {
            startActivity(new Intent(this, VendedoresActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_chat) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_favorito) {
            startActivity(new Intent(this, FavoritosActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_configuracion){
            startActivity(new Intent(this, SettingsActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_inicio) {
            startActivity(new Intent(this, HomeActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        calendario = Calendar.getInstance();
        Date date = calendario.getTime();
        usuarioEnLinea.modificarStatus(true, date, usuario);
    }

    @Override
    protected void onPause() {
        super.onPause();
        calendario = Calendar.getInstance();
        Date date = calendario.getTime();
        usuarioEnLinea.modificarStatus(false, date, usuario);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (VariablesGenerales.verCheckBoxes) {
            interfaceRecyclerViewConversaciones.borrarSelecciones();
        }
    }
}