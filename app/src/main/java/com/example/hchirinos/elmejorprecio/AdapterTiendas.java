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

                                Intent miIntent = new Intent(mContext, Maps_buscar.class);
                                Bundle miBundle = new Bundle();
                                miBundle.putDouble("lat", listTiendas.get(i).getLatitud());
                                miBundle.putDouble("lng", listTiendas.get(i).getLongitud());
                                miIntent.putExtras(miBundle);
                                mContext.startActivity(miIntent);


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
