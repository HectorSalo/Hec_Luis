package com.example.hchirinos.elmejorprecio.Adaptadores;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.hchirinos.elmejorprecio.AdminSQLiteHelper;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorFavoritos;
import com.example.hchirinos.elmejorprecio.R;

import java.util.ArrayList;

public class AdapterFavoritos extends RecyclerView.Adapter<AdapterFavoritos.ViewHolderFavoritos> {

    private ArrayList<ConstructorFavoritos> listFavoritos;
    private Context mContext;

    public AdapterFavoritos (ArrayList<ConstructorFavoritos> listFavoritos, Context mContext) {
        this.listFavoritos = listFavoritos;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolderFavoritos onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {



        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favoritos_list, null, false);
        return new ViewHolderFavoritos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterFavoritos.ViewHolderFavoritos viewHolderFavoritos, final int i) {


    }

    @Override
    public int getItemCount() {
        return listFavoritos.size();
    }

    public class ViewHolderFavoritos extends RecyclerView.ViewHolder {



        public ViewHolderFavoritos(@NonNull View itemView) {
            super(itemView);



        }
    }

    public void updateList (ArrayList<ConstructorFavoritos> newList){

        listFavoritos = new ArrayList<>();
        listFavoritos.addAll(newList);
        notifyDataSetChanged();
    }

}
