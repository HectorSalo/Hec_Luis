package com.example.hchirinos.elmejorprecio;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdapterTiendas extends RecyclerView.Adapter<AdapterTiendas.ViewHolderTiendas> implements Response.Listener<JSONObject>, Response.ErrorListener {

    ArrayList<ConstructorTiendas> listTiendas;
    Context mContext;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    private double latitud_origen;
    private double longitud_origen;
    private double longitud_final, latitud_final;

    public AdapterTiendas (ArrayList<ConstructorTiendas> listTiendas, Context mContext) {
        this.listTiendas = listTiendas;
        this.mContext = mContext;
        request = Volley.newRequestQueue(mContext);
    }

    @NonNull
    @Override
    public ViewHolderTiendas onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_tienda_layout, null, false);
        return new ViewHolderTiendas(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderTiendas viewHolderTiendas, final int i) {

        viewHolderTiendas.textView_nombre_tienda.setText(listTiendas.get(i).getNombre_tienda());
        viewHolderTiendas.textView_sucursal.setText(listTiendas.get(i).getSucursal());

        if (listTiendas.get(i).getImagen()!=null) {

            cargarimagen(listTiendas.get(i).getImagen(), viewHolderTiendas);

        } else {
            viewHolderTiendas.imageView_tiendas.setImageResource(R.drawable.common_google_signin_btn_icon_dark_focused);
        }


        viewHolderTiendas.textView_option_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PopupMenu popupMenu = new PopupMenu(mContext, viewHolderTiendas.textView_option_menu);
                popupMenu.inflate(R.menu.tienda_option_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.option_favoritos:

                                Toast.makeText(mContext, "Agregado a Favoritos", Toast.LENGTH_LONG).show();

                                break;

                            case R.id.option_ver_productos:

                                Toast.makeText(mContext, "Productos", Toast.LENGTH_LONG).show();


                                break;

                            case R.id.option_mapa:



                                webServicesObtenerRuta (String.valueOf(listTiendas.get(i).getLatitud()), String.valueOf(listTiendas.get(i).getLongitud()));


                                //Intent miIntent = new Intent(mContext, Maps_buscar.class);
                                //mContext.startActivity(miIntent);

                                Toast.makeText(mContext, "Mapa" + listTiendas.get(i).getLatitud(), Toast.LENGTH_LONG).show();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });

    }


    private void webServicesObtenerRuta(String latitudFinal, String longitudFinal) {

        latitud_origen = 10.4965479009811;
        longitud_origen = -66.83570075677173;


        String latitudInicial = String.valueOf(latitud_origen);
        String longitudInicial = String.valueOf(longitud_origen);


        String url="https://maps.googleapis.com/maps/api/directions/json?origin="+latitudInicial+","+longitudInicial
                +"&destination="+latitudFinal+","+longitudFinal+"&key=AIzaSyD4jiyYQBxA6zNECOpitloM1uOLcs0jCfo";

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
                        List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                        for (int j = 0; j < jLegs.length(); j++) {
                            jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                            for (int k = 0; k < jSteps.length(); k++) {
                                String polyline = "";
                                polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                                List<LatLng> list = decodePoly(polyline);

                                for (int l = 0; l < list.size(); l++) {
                                    HashMap<String, String> hm = new HashMap<String, String>();
                                    hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                                    hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                                    path.add(hm);
                                }
                            }
                            UtilidadesMaps.routes.add(path);
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
                Toast.makeText(mContext, "No se puede conectar "+error.toString(), Toast.LENGTH_LONG).show();
                System.out.println();
                Log.d("ERROR: ", error.toString());
            }
        });

        request.add(jsonObjectRequest);

    }

    public List<List<HashMap<String, String>>> parse (JSONObject jObject) {

        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    UtilidadesMaps.routes.add(path);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
        return UtilidadesMaps.routes;

    }



    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


    private void cargarimagen(String imagen, final ViewHolderTiendas viewHolderTiendas) {

        String urlImagen = "http://192.168.3.34:8080/elmejorprecio/" + imagen;
        urlImagen = urlImagen.replace(" ", "%20");

        ImageRequest imageRequest = new ImageRequest(urlImagen, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                viewHolderTiendas.imageView_tiendas.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
            }
        });
        request.add(imageRequest);
    }

    @Override
    public int getItemCount() {
        return listTiendas.size();
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(JSONObject response) {

    }

    public class ViewHolderTiendas extends RecyclerView.ViewHolder {

        TextView textView_nombre_tienda;
        TextView textView_sucursal;
        ImageView imageView_tiendas;
        TextView textView_option_menu;

        public ViewHolderTiendas(@NonNull View itemView) {
            super(itemView);

            textView_nombre_tienda = (TextView)itemView.findViewById(R.id.textView_nombre_tienda);
            textView_sucursal = (TextView)itemView.findViewById(R.id.textView_sucursal);
            imageView_tiendas = itemView.findViewById(R.id.imageView_tienda);
            textView_option_menu = itemView.findViewById(R.id.textView_tienda_option);
        }
    }

    public void updateList (ArrayList<ConstructorTiendas> newList){

        listTiendas = new ArrayList<>();
        listTiendas.addAll(newList);
        notifyDataSetChanged();
    }
}
