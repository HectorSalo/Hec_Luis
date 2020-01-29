package com.example.hchirinos.elmejorprecio;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MapsInfoVendedor extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SearchView searchView;
    private Boolean actualPosition = true;
    private Double latUbicacion, lngUbicacion, latDestino, lngDestino;
    private String latUbicacionS, lngUbicacionS, latDestinoS, lngDestinoS;
    private Location locationActual;
    private FusedLocationProviderClient fusedLocationProviderClient;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_buscar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_encontrar);
        mapFragment.getMapAsync(this);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyBP8xRaD-7sA3oCQTx24czDGiEJ4ts39EE");
        }

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.e("Place", "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.e("Place", "An error occurred: " + status);
            }
        });

        searchView = findViewById(R.id.search_map);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (VariablesGenerales.verSearchMap) {
            searchView.setVisibility(View.VISIBLE);
        } else {
            searchView.setVisibility(View.GONE);
            latDestino = VariablesGenerales.latlongInfoVendedor.getLatitude();
            lngDestino = VariablesGenerales.latlongInfoVendedor.getLongitude();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s != null && !s.isEmpty()) {
                    buscarDireccion(s);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);

            }

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
               ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            return;
        }

        mMap.setMyLocationEnabled(true);

        if (VariablesGenerales.verSearchMap) {
            obtenerUbicacion();
        } else {
            LatLng miPosicion = new LatLng(latDestino, lngDestino);
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_foreground)).position(miPosicion).title("Lugar de Entrega"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miPosicion, 15));
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

                    if (VariablesGenerales.verSearchMap) {
                        obtenerUbicacion();
                    } else {
                        LatLng miPosicion = new LatLng(latDestino, lngDestino);
                        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_foreground)).position(miPosicion).title("Lugar de Entrega"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miPosicion, 15));
                    }

                    mMap.setMyLocationEnabled(true);

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


    private void obtenerUbicacion() {
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    locationActual = location;
                    LatLng latLng = new LatLng(locationActual.getLatitude(), locationActual.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Mi ubicación"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            }
        });
    }


    private void buscarDireccion(String location) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addressList = null;

        try {
            addressList = geocoder.getFromLocationName(location, 100);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address address = addressList.get(0);
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title(location));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }


    public void establecerOrigenDestino() {
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

                    //Rutas(latUbicacionS, lngUbicacionS, latDestinoS, lngDestinoS);


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
