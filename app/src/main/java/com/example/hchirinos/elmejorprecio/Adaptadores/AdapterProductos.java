package com.example.hchirinos.elmejorprecio.Adaptadores;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.example.hchirinos.elmejorprecio.AdminSQLiteHelper;
import com.example.hchirinos.elmejorprecio.ConstructorProductos;
import com.example.hchirinos.elmejorprecio.R;
import com.example.hchirinos.elmejorprecio.lista_compras;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterProductos extends RecyclerView.Adapter<AdapterProductos.ViewHolderProductos>{

    ArrayList<ConstructorProductos> listProductos;
    Context mContext;


    public AdapterProductos (ArrayList<ConstructorProductos> listProductos, Context mContext){
        this.listProductos = listProductos;
        this.mContext = mContext;

    }


    @NonNull
    @Override
    public AdapterProductos.ViewHolderProductos onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        //Enlaza el adaptador con list_producto_layout.xml
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_producto_layout,null, false);

        return new ViewHolderProductos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderProductos viewHolderProductos, final int i) {


        //Comunica el adaptador con la clase ViewHolderProductos
        viewHolderProductos.textView_nombre_producto.setText(listProductos.get(i).getDescripcionProducto());
        viewHolderProductos.textView_precio_producto.setText("$ " + listProductos.get(i).getPrecioProducto());

        if (listProductos.get(i).getImagenProducto()!=null) {

           Glide.with(mContext).load(listProductos.get(i).getImagenProducto()).into(viewHolderProductos.imageView_producto);

        }

    }


    @Override
    public int getItemCount() {
        return listProductos.size();
    }





    public class ViewHolderProductos extends RecyclerView.ViewHolder {

        //Referencia los elementos del archivo list_producto_layout.xml
        TextView textView_nombre_producto;
        TextView textView_precio_producto;
        ImageView imageView_producto;



        public ViewHolderProductos(@NonNull View itemView) {
            super(itemView);

            textView_nombre_producto = itemView.findViewById(R.id.tvDescripcionProducto);
            textView_precio_producto = itemView.findViewById(R.id.textView_precio_producto);
            imageView_producto = itemView.findViewById(R.id.imageView_producto);

        }
    }

    public void updateList (ArrayList<ConstructorProductos> newList){

        listProductos = new ArrayList<>();
        listProductos.addAll(newList);
        notifyDataSetChanged();
    }

    private void agregarFavoritos () {

    }

    private void compartir() {

    }


}
