package com.example.hchirinos.elmejorprecio.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.hchirinos.elmejorprecio.AdminSQLiteHelper;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorProductos;
import com.example.hchirinos.elmejorprecio.InfoProductoActivity;
import com.example.hchirinos.elmejorprecio.ProductosActivity;
import com.example.hchirinos.elmejorprecio.R;
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.example.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
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

        viewHolderFavoritos.tvDescripcion.setText(listFavoritos.get(position).getNombreProducto());
        viewHolderFavoritos.tvPrecio.setText("$" + listFavoritos.get(position).getPrecioProducto());
        viewHolderFavoritos.imageButtonQuitar.setChecked(true);

        viewHolderFavoritos.imageButtonQuitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!viewHolderFavoritos.imageButtonQuitar.isChecked()) {
                    quitarFavoritos(listFavoritos.get(position));
                }
            }
        });

        if (listFavoritos.get(position).getImagenProducto() != null) {
            Glide.with(mContext).load(listFavoritos.get(position).getImagenProducto()).into(viewHolderFavoritos.imagen);
        }


        viewHolderFavoritos.imageButtonCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selection = listFavoritos.get(position).getDescripcionProducto() + "\n$" + listFavoritos.get(position).getPrecioProducto() + "\nVende: " + listFavoritos.get(position).getVendedor();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, selection);
                mContext.startActivity(Intent.createChooser(intent, "Compartir con"));
            }
        });


        viewHolderFavoritos.imageButtonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VariablesGenerales.descripcionInfoProducto = listFavoritos.get(position).getDescripcionProducto();
                VariablesGenerales.cantidadesInfoProducto = listFavoritos.get(position).getCantidadProducto() + " " + listFavoritos.get(position).getUnidadProducto();
                VariablesGenerales.vendedorInfoProducto = listFavoritos.get(position).getVendedor();
                VariablesGenerales.imagenInfoProducto = listFavoritos.get(position).getImagenProducto();
                VariablesGenerales.precioInfoProducto = "$" + listFavoritos.get(position).getPrecioProducto();
                VariablesGenerales.estadoInfoProducto = listFavoritos.get(position).getEstadoProducto();

                mContext.startActivity(new Intent(mContext, InfoProductoActivity.class));
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
        ToggleButton imageButtonQuitar;

        public ViewHolderFavoritos(@NonNull View itemView) {
            super(itemView);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionProductoFavorito);
            tvPrecio = itemView.findViewById(R.id.textViewPrecioFavorito);
            imagen = itemView.findViewById(R.id.imageViewProductoFavorito);
            imageButtonCompartir = itemView.findViewById(R.id.imageButtonCompartirFavorito);
            imageButtonInfo = itemView.findViewById(R.id.imageButtonInfoProductoFavorito);
            imageButtonQuitar = itemView.findViewById(R.id.imageButtonQuitarFavorito);

        }
    }

    public void updateList (ArrayList<ConstructorProductos> newList){

        listFavoritos = new ArrayList<>();
        listFavoritos.addAll(newList);
        notifyDataSetChanged();
    }

    public void quitarFavoritos(ConstructorProductos i) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = i.getIdProducto();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_ALMACEN).document(id).update(VariablesEstaticas.BD_USUARIOS_FAVORITOS, FieldValue.arrayRemove(user.getUid()));

        listFavoritos.remove(i);
        updateList(listFavoritos);
    }

}
