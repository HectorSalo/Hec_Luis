package com.example.hchirinos.elmejorprecio;

import android.content.Context;
import android.content.Intent;
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

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterProductos extends RecyclerView.Adapter<AdapterProductos.ViewHolderProductos> {

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
        viewHolderProductos.textView_nombre_producto.setText(listProductos.get(i).getNombre_producto());
        viewHolderProductos.textView_marca_producto.setText(listProductos.get(i).getMarca_producto());
        viewHolderProductos.textView_precio_producto.setText(String.valueOf(listProductos.get(i).getPrecio_producto()));
        //viewHolderProductos.imageView_producto.setImageBitmap(listProductos.get(i).getImagen_bitmap());
        viewHolderProductos.textView_option_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display option menu


                PopupMenu popupMenu = new PopupMenu(mContext, viewHolderProductos.textView_option_item);
                popupMenu.inflate(R.menu.producto_option_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.option_detalles:

                                Toast.makeText(mContext, "Detalles", Toast.LENGTH_LONG).show();

                                break;

                            case R.id.option_compras:

                                Intent compras = new Intent(mContext, lista_compras.class);

                                Bundle comprasBundle = new Bundle();
                                comprasBundle.putString("nombre", listProductos.get(i).getNombre_producto());
                                comprasBundle.putString("marca", listProductos.get(i).getMarca_producto());
                                comprasBundle.putDouble("precio", listProductos.get(i).getPrecio_producto());
                                Toast.makeText(mContext, "Guardado en Lista de Compras", Toast.LENGTH_LONG).show();
                                compras.putExtras(comprasBundle);
                                mContext.startActivity(compras);
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
        return listProductos.size();
    }

    public class ViewHolderProductos extends RecyclerView.ViewHolder {

        //Referencia los elementos del archivo list_producto_layout.xml
        TextView textView_nombre_producto;
        TextView textView_marca_producto;
        TextView textView_precio_producto;
        TextView textView_option_item;
        ImageView imageView_producto;


        public ViewHolderProductos(@NonNull View itemView) {
            super(itemView);

            textView_nombre_producto = (TextView) itemView.findViewById(R.id.textView_nombre_producto);
            textView_marca_producto = (TextView) itemView.findViewById(R.id.textView_marca_producto);
            textView_precio_producto = (TextView) itemView.findViewById(R.id.textView_precio_producto);
            textView_option_item=  itemView.findViewById(R.id.textView_producto_option);
            imageView_producto = (ImageView) itemView.findViewById(R.id.imageView_producto);

        }
    }

    public void updateList (ArrayList<ConstructorProductos> newList){

        listProductos = new ArrayList<>();
        listProductos.addAll(newList);
        notifyDataSetChanged();
    }
}
