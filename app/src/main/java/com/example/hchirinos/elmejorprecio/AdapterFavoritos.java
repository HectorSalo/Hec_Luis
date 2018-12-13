package com.example.hchirinos.elmejorprecio;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

import org.json.JSONObject;

import java.util.ArrayList;

public class AdapterFavoritos extends RecyclerView.Adapter<AdapterFavoritos.ViewHolderFavoritos> {

    ArrayList<ConstructorFavoritos> listFavoritos;
    Context mContext;
    VistaGridList vistaGridList;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;



    public AdapterFavoritos (ArrayList<ConstructorFavoritos> listFavoritos, Context mContext) {
        this.listFavoritos = listFavoritos;
        this.mContext = mContext;
        request = Volley.newRequestQueue(mContext);
    }

    @NonNull
    @Override
    public ViewHolderFavoritos onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favoritos_grid, null, false);
        /*if (vistaGridList.getLista()==1) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favoritos_list, null, false);

        } else if (vistaGridList.getGrid()==0) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favoritos_grid, null, false);

        }*/
        return new ViewHolderFavoritos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterFavoritos.ViewHolderFavoritos viewHolderFavoritos, final int i) {

        viewHolderFavoritos.textView_sucursalFavoritos.setText(listFavoritos.get(i).getSucursal());

        /*if (vistaGridList.getLista()==1){
            viewHolderFavoritos.textView_sucursalFavoritos.setText(listFavoritos.get(i).getSucursal());
            viewHolderFavoritos.textView_nombreFavoritos.setText(listFavoritos.get(i).getNombre_tienda());
        } else if (vistaGridList.getGrid()==0) {
            viewHolderFavoritos.textView_sucursalFavoritos.setText(listFavoritos.get(i).getSucursal());
        }*/



        if (listFavoritos.get(i).getImagen()!=null) {

            cargarimagen(listFavoritos.get(i).getImagen(), viewHolderFavoritos);

        } else {
            viewHolderFavoritos.imagenFavoritos.setImageResource(R.drawable.common_google_signin_btn_icon_dark_focused);
        }


        viewHolderFavoritos.textView_menuFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PopupMenu popupMenu = new PopupMenu(mContext, viewHolderFavoritos.textView_menuFavoritos);
                popupMenu.inflate(R.menu.favoritos_option_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.option_delete_favoritos:
                                delete_Favoritos(listFavoritos.get(i));
                                listFavoritos.remove(i);
                                notifyDataSetChanged();
                                Toast.makeText(mContext, "Eliminado de Favoritos", Toast.LENGTH_LONG).show();


                                break;

                            case R.id.option_ver_productos_favoritos:

                                Toast.makeText(mContext, "Productos", Toast.LENGTH_LONG).show();


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
        return listFavoritos.size();
    }

    public class ViewHolderFavoritos extends RecyclerView.ViewHolder {

        ImageView imagenFavoritos;
        TextView textView_sucursalFavoritos;
        TextView textView_menuFavoritos;
        TextView textView_nombreFavoritos;

        public ViewHolderFavoritos(@NonNull View itemView) {
            super(itemView);

            imagenFavoritos = itemView.findViewById(R.id.imagenFavoritos);
            textView_sucursalFavoritos = itemView.findViewById(R.id.textViewSucursalFavoritos);
            textView_menuFavoritos = itemView.findViewById(R.id.menuFavoritos);
            textView_nombreFavoritos = itemView.findViewById(R.id.textViewtienda_favoritos);
        }
    }

    private void cargarimagen(String imagen, final AdapterFavoritos.ViewHolderFavoritos viewHolderFavoritos) {

        String urlImagen = "http://192.168.3.34:8080/elmejorprecio/" + imagen;
        urlImagen = urlImagen.replace(" ", "%20");

        ImageRequest imageRequest = new ImageRequest(urlImagen, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                viewHolderFavoritos.imagenFavoritos.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
            }
        });
        request.add(imageRequest);
    }

    public void delete_Favoritos (ConstructorFavoritos i) {

        String url = "http://192.168.3.34:8080/elmejorprecio/delete_favoritos.php?cod_sup="+ i.getCod_tienda();
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Toast.makeText(mContext, "Eliminado de Favoritos", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(mContext, "Error al eliminar", Toast.LENGTH_LONG).show();
            }
        });
        request.add(jsonObjectRequest);

    }

}
