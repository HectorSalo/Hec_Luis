package com.example.hchirinos.elmejorprecio.Adaptadores;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorProductos;
import com.example.hchirinos.elmejorprecio.R;
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.example.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;

public class AdapterFavoritos extends RecyclerView.Adapter<AdapterFavoritos.ViewHolderFavoritos> {

    private ArrayList<ConstructorProductos> listFavoritos;
    private Context mContext;

    public AdapterFavoritos (ArrayList<ConstructorProductos> listFavoritos, Context mContext) {
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
    public void onBindViewHolder(@NonNull final AdapterFavoritos.ViewHolderFavoritos viewHolderFavoritos, int i) {
        final int position = i;

        viewHolderFavoritos.tvDescripcion.setText(listFavoritos.get(position).getDescripcionProducto());
        viewHolderFavoritos.tvPrecio.setText("$" + listFavoritos.get(position).getPrecioProducto());

        if (listFavoritos.get(position).getImagenProducto() != null) {
            Glide.with(mContext).load(listFavoritos.get(position).getImagenProducto()).into(viewHolderFavoritos.imagen);
        }

        viewHolderFavoritos.likeButton.setLiked(true);
        viewHolderFavoritos.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

            }

            @Override
            public void unLiked(LikeButton likeButton) {
                quitarFavoritos(listFavoritos.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return listFavoritos.size();
    }

    public class ViewHolderFavoritos extends RecyclerView.ViewHolder {

        TextView tvDescripcion;
        TextView tvPrecio;
        ImageView imagen;
        ImageButton imageButtonCompartir, imageButtonInfo;
        LikeButton likeButton;

        public ViewHolderFavoritos(@NonNull View itemView) {
            super(itemView);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionProductoFavorito);
            tvPrecio = itemView.findViewById(R.id.textViewPrecioFavorito);
            imagen = itemView.findViewById(R.id.imageViewProductoFavorito);
            imageButtonCompartir = itemView.findViewById(R.id.imageButtonCompartirFavorito);
            imageButtonInfo = itemView.findViewById(R.id.imageButtonInfoProductoFavorito);
            likeButton = itemView.findViewById(R.id.likeButtonFavorito);
        }
    }

    public void updateList (ArrayList<ConstructorProductos> newList){

        listFavoritos = new ArrayList<>();
        listFavoritos.addAll(newList);
        notifyDataSetChanged();
    }

    public void quitarFavoritos(ConstructorProductos i) {
        String id = i.getIdProducto();
        AdminSQLiteHelper adminSQLiteHelper = new AdminSQLiteHelper(mContext, VariablesEstaticas.BD_PRODUCTOS, null, VariablesEstaticas.VERSION_SQLITE);
        SQLiteDatabase db = adminSQLiteHelper.getWritableDatabase();

        db.delete(VariablesEstaticas.BD_FAVORITOS, "idProducto= '" + id + "'", null);
        db.close();

        listFavoritos.remove(i);
        notifyDataSetChanged();
    }

}
