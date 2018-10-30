package com.example.hchirinos.elmejorprecio;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterTiendas extends RecyclerView.Adapter<AdapterTiendas.ViewHolderTiendas> {

    ArrayList<ConstructorTiendas> listTiendas;

    public AdapterTiendas (ArrayList<ConstructorTiendas> listTiendas) {
        this.listTiendas = listTiendas;
    }

    @NonNull
    @Override
    public ViewHolderTiendas onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_tienda_layout, null, false);
        return new ViewHolderTiendas(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTiendas viewHolderTiendas, int i) {

        viewHolderTiendas.textView_nombre_tienda.setText(listTiendas.get(i).getNombre_tienda());
        viewHolderTiendas.textView_sucursal.setText(listTiendas.get(i).getSucursal());

    }

    @Override
    public int getItemCount() {
        return listTiendas.size();
    }

    public class ViewHolderTiendas extends RecyclerView.ViewHolder {

        TextView textView_nombre_tienda;
        TextView textView_sucursal;

        public ViewHolderTiendas(@NonNull View itemView) {
            super(itemView);

            textView_nombre_tienda = (TextView)itemView.findViewById(R.id.textView_nombre_tienda);
            textView_sucursal = (TextView)itemView.findViewById(R.id.textView_sucursal);
        }
    }
}
