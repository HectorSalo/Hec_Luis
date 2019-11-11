package com.example.hchirinos.elmejorprecio.Adaptadores;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.hchirinos.elmejorprecio.Constructores.ConstructorVendedores;
import com.example.hchirinos.elmejorprecio.R;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.drm.DrmStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterVendedores extends RecyclerView.Adapter<AdapterVendedores.ViewHolderTiendas> {

    private ArrayList<ConstructorVendedores> listVendedores;
    private Context mContext;

    public AdapterVendedores(ArrayList<ConstructorVendedores> listVendedores, Context mContext) {
        this.listVendedores = listVendedores;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolderTiendas onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_vendedores, null, false);
        return new ViewHolderTiendas(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderTiendas viewHolderTiendas, final int i) {


        viewHolderTiendas.textViewNombreVendedor.setText(listVendedores.get(i).getNombreVendedor());

        if (listVendedores.get(i).getImagen()!=null) {

            Glide.with(mContext).load(listVendedores.get(i).getImagen()).into(viewHolderTiendas.imageViewImagenVendedor);

        }


    }


    @Override
    public int getItemCount() {
        return listVendedores.size();
    }


    public class ViewHolderTiendas extends RecyclerView.ViewHolder {

        TextView textViewNombreVendedor;
        ImageView imageViewImagenVendedor;


        public ViewHolderTiendas(@NonNull View itemView) {
            super(itemView);

            textViewNombreVendedor = itemView.findViewById(R.id.textViewNombreVendedor);
            imageViewImagenVendedor = itemView.findViewById(R.id.imageViewVendedor);

        }
    }

    public void updateList (ArrayList<ConstructorVendedores> newList){

        listVendedores = new ArrayList<>();
        listVendedores.addAll(newList);
        notifyDataSetChanged();
    }

}
