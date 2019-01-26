package com.example.hchirinos.elmejorprecio;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.bumptech.glide.Glide;

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


        int layout = 0;
        if (VistaGridList.visualizacion==VistaGridList.List) {
            layout = R.layout.favoritos_list;

        } else if (VistaGridList.visualizacion == VistaGridList.Grid) {
            layout = R.layout.favoritos_grid;

        }
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layout, null, false);
        return new ViewHolderFavoritos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterFavoritos.ViewHolderFavoritos viewHolderFavoritos, final int i) {


        if (VistaGridList.visualizacion == VistaGridList.List) {
            viewHolderFavoritos.textView_sucursalFavoritos.setText(listFavoritos.get(i).getSucursal());
            viewHolderFavoritos.textView_nombreFavoritos.setText(listFavoritos.get(i).getNombre_tienda());
        } else if (VistaGridList.visualizacion == VistaGridList.Grid) {
        viewHolderFavoritos.textView_sucursalFavoritos.setText(listFavoritos.get(i).getSucursal());
    }



        if (listFavoritos.get(i).getImagen()!=null) {

            Glide.with(mContext).load(listFavoritos.get(i).getImagen()).into(viewHolderFavoritos.imagenFavoritos);

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

            if (VistaGridList.visualizacion == VistaGridList.List) {
                imagenFavoritos = itemView.findViewById(R.id.imagenFavoritos);
                textView_sucursalFavoritos = itemView.findViewById(R.id.textViewSucursalFavoritos);
                textView_menuFavoritos = itemView.findViewById(R.id.menuFavoritos);
                textView_nombreFavoritos = itemView.findViewById(R.id.textViewtienda_favoritos);

            } else if (VistaGridList.visualizacion == VistaGridList.Grid) {
                imagenFavoritos = itemView.findViewById(R.id.imagenFavoritos);
                textView_sucursalFavoritos = itemView.findViewById(R.id.textViewSucursalFavoritos);
                textView_menuFavoritos = itemView.findViewById(R.id.menuFavoritos);


            }

        }
    }


    public void delete_Favoritos (ConstructorFavoritos i) {
        String idTienda = i.getCod_tienda();

        AdminSQLiteHelper conect = new AdminSQLiteHelper(mContext, "MyList", null, AdminSQLiteHelper.VERSION);
        SQLiteDatabase db = conect.getWritableDatabase();

        db.delete("tiendas", "idTienda ='" + idTienda + "'", null);
        db.close();

        Toast.makeText(mContext, "Eliminado de Favoritos", Toast.LENGTH_SHORT).show();

    }

    public void updateList (ArrayList<ConstructorFavoritos> newList){

        listFavoritos = new ArrayList<>();
        listFavoritos.addAll(newList);
        notifyDataSetChanged();
    }

}
