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
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Maps_buscar extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Boolean actualPosition = true;
    private Double latUbicacion, lngUbicacion, latDestino, lngDestino;
    private String latUbicacionS, lngUbicacionS, latDestinoS, lngDestinoS;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {


            } else {


                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);


            }

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {


            } else {

               ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);

            }
            return;
        }
        mMap.setMyLocationEnabled(true);

        request = Volley.newRequestQueue(this);


        Bundle miBundle = this.getIntent().getExtras();
        latDestino = miBundle.getDouble("lat");
        lngDestino = miBundle.getDouble("lng");

        if (miBundle.getDouble("lat")!=0) {

            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {

                    latUbicacion = location.getLatitude();
                    lngUbicacion = location.getLongitude();

                    latDestinoS = String.valueOf(latDestino);
                    lngDestinoS = String.valueOf(lngDestino);
                    latUbicacionS = String.valueOf(latUbicacion);
                    lngUbicacionS = String.valueOf(lngUbicacion);

                    Rutas(latUbicacionS, lngUbicacionS, latDestinoS, lngDestinoS);


                }
            });


        } else if (miBundle.getString("inicio")!=null) {
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    if (actualPosition) {
                        latUbicacion = location.getLatitude();
                        lngUbicacion = location.getLongitude();
                        actualPosition = false;

                        LatLng miPosicion = new LatLng(latUbicacion, lngUbicacion);
                        mMap.addMarker(new MarkerOptions().position(miPosicion).title("Mi Ubicacion"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miPosicion, 15));

                    }
                }
            });
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void Rutas (String latitudInicial, String longitudInicial, String latitudFinal, String longitudFinal) {


        String url="https://maps.googleapis.com/maps/api/directions/json?origin="+latitudInicial+","+longitudInicial
                +"&destination="+latitudFinal+","+longitudFinal+"&key=AIzaSyApXaL18Ejg4MIBK1jiyeGutrRckhLt-tQ";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("jsonRoute", ""+response);
                JSONArray jRoutes = null;
                JSONArray jLegs = null;
                JSONArray jSteps = null;

                try {
                    jRoutes = response.getJSONArray("routes");

                    for (int i = 0; i < jRoutes.length(); i++) {
                        jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");


                        for (int j = 0; j < jLegs.length(); j++) {
                            jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                            for (int k = 0; k < jSteps.length(); k++) {
                                String polyline = "";
                                polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                                List<LatLng> list = PolyUtil.decode(polyline);

                                mMap.addPolyline(new PolylineOptions().addAll(list).color(Color.BLUE).width(8));

                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });

        request.add(jsonObjectRequest);

        LatLng ubicacion = new LatLng(latUbicacion, lngUbicacion);
        mMap.addMarker(new MarkerOptions().position(ubicacion).title("Ubicación Actual"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 13));

        LatLng destino = new LatLng(latDestino, lngDestino);
        mMap.addMarker(new MarkerOptions().position(destino).title("Destino"));
    }
}
