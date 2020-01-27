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
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.hchirinos.elmejorprecio.SQLite.ConectSQLiteHelper;
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.example.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;

public class AdapterProductos extends RecyclerView.Adapter<AdapterProductos.ViewHolderProductos>{

    private ArrayList<ConstructorProductos> listProductos;
    private Context mContext;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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
        viewHolderProductos.textView_nombre_producto.setText(listProductos.get(i).getNombreProducto());
        viewHolderProductos.textView_precio_producto.setText("$" + listProductos.get(i).getPrecioProducto());

        if (listProductos.get(i).getImagenProducto()!=null) {

           Glide.with(mContext).load(listProductos.get(i).getImagenProducto()).into(viewHolderProductos.imageView_producto);

        }

        if (user != null) {
            viewHolderProductos.imageButtonAdd.setVisibility(View.VISIBLE);

            if (listProductos.get(i).getListUsuariosFavoritos() != null) {
                if (listProductos.get(i).getListUsuariosFavoritos().contains(user.getUid())) {
                    viewHolderProductos.imageButtonAdd.setChecked(true);
                }
            }
        } else {
            viewHolderProductos.imageButtonAdd.setVisibility(View.GONE);
        }


        viewHolderProductos.imageButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (viewHolderProductos.imageButtonAdd.isChecked()) {
                        agregarFavoritosFirestore(listProductos.get(i).getIdProducto());
                    } else {
                        quitarFavoritosFirestore(listProductos.get(i).getIdProducto());
                    }

            }
        });


        viewHolderProductos.imageButtonCompartir.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                nombreVendedorCompartir(listProductos.get(position).getVendedor(), position);
            }
        });


        viewHolderProductos.imageButtonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VariablesGenerales.nombreInfoProducto = listProductos.get(position).getNombreProducto();
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
        ToggleButton imageButtonAdd;


        public ViewHolderProductos(@NonNull View itemView) {
            super(itemView);

            textView_nombre_producto = itemView.findViewById(R.id.tvDescripcionProducto);
            textView_precio_producto = itemView.findViewById(R.id.textView_precio_producto);
            imageView_producto = itemView.findViewById(R.id.imageView_producto);
            imageButtonCompartir = itemView.findViewById(R.id.imageButtonCompartir);
            imageButtonInfo = itemView.findViewById(R.id.imageButtonInfoProducto);
            imageButtonAdd = itemView.findViewById(R.id.imageButtonAdd);

        }
    }

    public void updateList (ArrayList<ConstructorProductos> newList){

        listProductos = new ArrayList<>();
        listProductos.addAll(newList);
        notifyDataSetChanged();
    }


    private void agregarFavoritosFirestore(String idProducto) {
        String idUsuario = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_ALMACEN).document(idProducto).update(VariablesEstaticas.BD_USUARIOS_FAVORITOS, FieldValue.arrayUnion(idUsuario));
    }

    private void quitarFavoritosFirestore(String idProducto) {
        String idUsuario = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(VariablesEstaticas.BD_ALMACEN).document(idProducto).update(VariablesEstaticas.BD_USUARIOS_FAVORITOS, FieldValue.arrayRemove(idUsuario));
    }

    private void nombreVendedorCompartir(String idVendedor, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(VariablesEstaticas.BD_VENDEDORES).document(idVendedor).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String nombreVendedor = document.getString(VariablesEstaticas.BD_NOMBRE_VENDEDOR);
                        share(position, nombreVendedor);
                    } else {
                        share(position, "N/A");
                    }
                } else {
                    share(position, "N/A");
                }
            }

        });
    }

    private void share(int position, String vendedor){
        String selection = listProductos.get(position).getNombreProducto() + "\n$" + listProductos.get(position).getPrecioProducto() + "\nVende: " + vendedor;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, selection);
        mContext.startActivity(Intent.createChooser(intent, "Compartir con"));
    }


}
