package com.example.hchirinos.elmejorprecio.Adaptadores;

import android.content.ContentValues;
import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.request.RequestOptions;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorProductos;
import com.example.hchirinos.elmejorprecio.InfoProductoActivity;
import com.example.hchirinos.elmejorprecio.R;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hchirinos.elmejorprecio.SQLite.ConectSQLiteHelper;
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.example.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;

public class AdapterProductos extends RecyclerView.Adapter<AdapterProductos.ViewHolderProductos>{

    private ArrayList<ConstructorProductos> listProductos;
    private Context mContext;

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
    public void onBindViewHolder(@NonNull final ViewHolderProductos viewHolderProductos, int i) {

        final int position = i;

        //Comunica el adaptador con la clase ViewHolderProductos
        viewHolderProductos.textView_nombre_producto.setText(listProductos.get(i).getDescripcionProducto());
        viewHolderProductos.textView_precio_producto.setText("$" + listProductos.get(i).getPrecioProducto());

        if (listProductos.get(i).getImagenProducto()!=null) {

           Glide.with(mContext).load(listProductos.get(i).getImagenProducto()).into(viewHolderProductos.imageView_producto);

        }

        viewHolderProductos.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                agregarFavoritos(listProductos.get(position));
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                quitarFavoritos(listProductos.get(position));
            }
        });

        viewHolderProductos.imageButtonCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selection = listProductos.get(position).getDescripcionProducto() + "\n$" + listProductos.get(position).getPrecioProducto() + "\nVende: " + listProductos.get(position).getVendedor();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, selection);
                mContext.startActivity(Intent.createChooser(intent, "Compartir con"));
            }
        });


        viewHolderProductos.imageButtonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VariablesGenerales.descripcionInfoProducto = listProductos.get(position).getDescripcionProducto();
                VariablesGenerales.cantidadesInfoProducto = listProductos.get(position).getCantidadProducto() + " " + listProductos.get(position).getUnidadProducto();
                VariablesGenerales.vendedorInfoProducto = listProductos.get(position).getVendedor();
                VariablesGenerales.imagenInfoProducto = listProductos.get(position).getImagenProducto();
                VariablesGenerales.precioInfoProducto = "$" + listProductos.get(position).getPrecioProducto();
                VariablesGenerales.estadoInfoProducto = listProductos.get(position).getEstadoProducto();

                mContext.startActivity(new Intent(mContext, InfoProductoActivity.class));
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
        TextView textView_precio_producto;
        ImageView imageView_producto;
        ImageButton imageButtonCompartir, imageButtonInfo;
        LikeButton likeButton;


        public ViewHolderProductos(@NonNull View itemView) {
            super(itemView);

            textView_nombre_producto = itemView.findViewById(R.id.tvDescripcionProducto);
            textView_precio_producto = itemView.findViewById(R.id.textView_precio_producto);
            imageView_producto = itemView.findViewById(R.id.imageView_producto);
            imageButtonCompartir = itemView.findViewById(R.id.imageButtonCompartir);
            imageButtonInfo = itemView.findViewById(R.id.imageButtonInfoProducto);
            likeButton = itemView.findViewById(R.id.likeButton);

        }
    }

    public void updateList (ArrayList<ConstructorProductos> newList){

        listProductos = new ArrayList<>();
        listProductos.addAll(newList);
        notifyDataSetChanged();
    }

    private void agregarFavoritos (ConstructorProductos i) {
        String id = i.getIdProducto();
        String descripcion = i.getDescripcionProducto();
        ConectSQLiteHelper conectSQLiteHelper = new ConectSQLiteHelper(mContext, VariablesEstaticas.BD_PRODUCTOS, null, VariablesEstaticas.VERSION_SQLITE);
        SQLiteDatabase db = conectSQLiteHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(VariablesEstaticas.BD_ID_PRODUCTO_FAVORITO, id);
        values.put(VariablesEstaticas.BD_DESCRIPCION_PRODUCTO, descripcion);

        db.insert(VariablesEstaticas.BD_FAVORITOS, null, values);
        db.close();
    }

    private void quitarFavoritos (ConstructorProductos i) {
        String id = i.getIdProducto();
        ConectSQLiteHelper conectSQLiteHelper = new ConectSQLiteHelper(mContext, VariablesEstaticas.BD_PRODUCTOS, null, VariablesEstaticas.VERSION_SQLITE);
        SQLiteDatabase db = conectSQLiteHelper.getWritableDatabase();

        db.delete(VariablesEstaticas.BD_FAVORITOS, "idProducto= '" + id + "'", null);
        db.close();
    }

}
