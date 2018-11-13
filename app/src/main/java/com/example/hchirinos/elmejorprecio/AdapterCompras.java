package com.example.hchirinos.elmejorprecio;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterCompras extends RecyclerView.Adapter<AdapterCompras.ViewHolderCompras> {

    ArrayList<ConstructorCompras> listCompras;
    Context mContext;

    public AdapterCompras(ArrayList<ConstructorCompras> listCompras, Context mContext) {
        this.listCompras = listCompras;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolderCompras onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_compras_layout, null, false);
        return new ViewHolderCompras(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCompras viewHolderCompras, int i) {

        viewHolderCompras.textView_nombre_producto_compras.setText(listCompras.get(i).getNombre_producto_compras());
        viewHolderCompras.textView_marca_producto_compras.setText(listCompras.get(i).getMarca_producto_compras());
        viewHolderCompras.textView_precio_producto_compras.setText(String.valueOf(listCompras.get(i).getPrecio_producto_compras()));



    }

    @Override
    public int getItemCount() {
        return listCompras.size();
    }

    public class ViewHolderCompras extends RecyclerView.ViewHolder {

        TextView textView_nombre_producto_compras;
        TextView textView_marca_producto_compras;
        TextView textView_precio_producto_compras;
        CheckBox checkBox_compras;

        public ViewHolderCompras(@NonNull View itemView) {
            super(itemView);

            textView_nombre_producto_compras = itemView.findViewById(R.id.textView_nombre_producto_compras);
            textView_marca_producto_compras = itemView.findViewById(R.id.textView_marca_producto_compras);
            textView_precio_producto_compras = itemView.findViewById(R.id.textView_precio_producto_compras);
            checkBox_compras = itemView.findViewById(R.id.checkBox_compras);
        }
    }

}


