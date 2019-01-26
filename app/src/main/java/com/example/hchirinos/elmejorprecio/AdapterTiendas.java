package com.example.hchirinos.elmejorprecio;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.bumptech.glide.Glide;
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

            Glide.with(mContext).load(listTiendas.get(i).getImagen()).into(viewHolderTiendas.imageView_tiendas);

        } else {
            viewHolderTiendas.imageView_tiendas.setImageResource(R.drawable.common_google_signin_btn_icon_dark_focused);
        }


        viewHolderTiendas.textView_option_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


                PopupMenu popupMenu = new PopupMenu(mContext, viewHolderTiendas.textView_option_menu);
                popupMenu.inflate(R.menu.tienda_option_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.option_favoritos:
                                enviar_WS(v, listTiendas.get(i));

                                break;

                            case R.id.option_compartir:

                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                intent.putExtra(Intent.EXTRA_TEXT, "Esta tienda te puede gustar: \n"+listTiendas.get(i).getNombre_tienda()+" en " +listTiendas.get(i).getSucursal() + " " + listTiendas.get(i).getCod_tienda());
                                mContext.startActivity(Intent.createChooser(intent, "Compartir con"));

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

    private void enviar_WS (View v, ConstructorTiendas i) {

        AdminSQLiteHelper conectDB = new AdminSQLiteHelper(mContext, "MyList", null, AdminSQLiteHelper.VERSION);
        SQLiteDatabase db = conectDB.getWritableDatabase();

        String idTienda = i.getCod_tienda();

        ContentValues registro = new ContentValues();
        registro.put("idTienda", idTienda);

        db.insert("tiendas", null, registro);
        db.close();

        Snackbar.make(v, "Guardado en Tiendas Favoritas", Snackbar.LENGTH_LONG).setAction("Ver Lista", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TiendasFavoritasActivity.class);
                mContext.startActivity(intent);
            }
        }).show();
    }
}
