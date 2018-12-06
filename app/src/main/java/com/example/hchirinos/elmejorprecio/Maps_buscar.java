package com.example.hchirinos.elmejorprecio;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Maps_buscar extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Marker tu_ubicacion;

    Double latInicial, longInicial, latFinal, longFinal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_buscar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_encontrar);
        mapFragment.getMapAsync(this);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



        // Add a marker in Sydney and move the camera
        //LatLng habitacion = new LatLng(10.4854118, -66.8217642);
        //mMap.addMarker(new MarkerOptions().position(habitacion).title("Habitacion").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(habitacion, 15));

        //mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.getUiSettings().setZoomGesturesEnabled(true);

        LatLng center = null;
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;

        for (int i=0; i<UtilidadesMaps.routes.size(); i++) {
            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();

            // Obteniendo el detalle de la ruta
            List<HashMap<String, String>> path = UtilidadesMaps.routes.get(i);

            // Obteniendo todos los puntos y/o coordenadas de la ruta
            for(int j=0;j<path.size();j++){
                HashMap<String,String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                if (center == null) {
                    //Obtengo la 1ra coordenada para centrar el mapa en la misma.
                    center = new LatLng(lat, lng);
                }
                points.add(position);
            }

            // Agregamos todos los puntos en la ruta al objeto LineOptions
            lineOptions.addAll(points);
            //Definimos el grosor de las Polilíneas
            lineOptions.width(8);
            //Definimos el color de la Polilíneas
            lineOptions.color(Color.BLUE);
        }

        mMap.addPolyline(lineOptions);

        LatLng origen = new LatLng(10.4965479009811, -66.83570075677173);
        mMap.addMarker(new MarkerOptions().position(origen));

        LatLng destino = new LatLng(10.493929202111417, -66.81519098602028);
        mMap.addMarker(new MarkerOptions().position(destino));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origen, 15));

    }
}
