package com.example.hchirinos.elmejorprecio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.hchirinos.elmejorprecio.Fragments.ProductoInfoFragment;
import com.example.hchirinos.elmejorprecio.Fragments.VendedorInfoFragment;
import com.example.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class InfoActivity extends AppCompatActivity implements ProductoInfoFragment.OnFragmentInteractionListener, VendedorInfoFragment.OnFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        ProductoInfoFragment productoInfoFragment = new ProductoInfoFragment();
        VendedorInfoFragment vendedorInfoFragment = new VendedorInfoFragment();

        if (VariablesGenerales.infoProducto) {
            getSupportFragmentManager().beginTransaction().add(R.id.frameInfo, productoInfoFragment).commit();
        } else if (VariablesGenerales.infoVendedor) {
            getSupportFragmentManager().beginTransaction().add(R.id.frameInfo, vendedorInfoFragment).commit();
        }

    }

    @Override
    public void onBackPressed() {
            finish();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
