package com.example.hchirinos.elmejorprecio;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterProductos extends RecyclerView.Adapter<AdapterProductos.ViewHolderProductos> {

    ArrayList<ConstructorProductos> listProductos;

    public AdapterProductos (ArrayList<ConstructorProductos> listProductos){
        this.listProductos = listProductos;
    }


    @NonNull
    @Override
    public AdapterProductos.ViewHolderProductos onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        //Enlaza el adaptador con list_producto_layout.xml
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_producto_layout, null, false);
        return new ViewHolderProductos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterProductos.ViewHolderProductos viewHolderProductos, int i) {

        //Comunica el adaptador con la clase ViewHolderProductos
        viewHolderProductos.textView_nombre_producto.setText(listProductos.get(i).getNombre_producto());
        viewHolderProductos.textView_marca_producto.setText(listProductos.get(i).getMarca_producto());
        viewHolderProductos.textView_precio_producto.setText(String.valueOf(listProductos.get(i).getPrecio_producto()));
        //viewHolderProductos.imageView_producto.setImageResource(listProductos.get(i).getImagen_producto());

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
        ImageView imageView_producto;


        public ViewHolderProductos(@NonNull View itemView) {
            super(itemView);

            textView_nombre_producto = itemView.findViewById(R.id.textView_nombre_producto);
            textView_marca_producto = itemView.findViewById(R.id.textView_marca_producto);
            textView_precio_producto = itemView.findViewById(R.id.textView_precio_producto);
            imageView_producto = itemView.findViewById(R.id.imageView_producto);
        }
    }
}
