package com.example.hchirinos.elmejorprecio.Adaptadores;

import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorProductos;
import com.example.hchirinos.elmejorprecio.EditarArticuloActivity;
import com.example.hchirinos.elmejorprecio.R;
import com.example.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.example.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.example.hchirinos.elmejorprecio.VentasActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdapterVentas extends RecyclerView.Adapter<AdapterVentas.ViewHolderVentas> {

    private ArrayList<ConstructorProductos> listProductos;
    private Context mContext;
    private boolean tema;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AdapterVentas(ArrayList<ConstructorProductos> listProductos, Context mContext, boolean tema) {
        this.listProductos = listProductos;
        this.mContext = mContext;
        this.tema = tema;
    }

    @NonNull
    @Override
    public ViewHolderVentas onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_ventas_layout, null, false);

        return new ViewHolderVentas(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderVentas viewHolderVentas, int i) {
        int position = i;

        viewHolderVentas.tvNombreProducto.setText(listProductos.get(i).getNombreProducto());
        viewHolderVentas.tvPrecioProducto.setText("$ " + listProductos.get(i).getPrecioProducto());

        Glide.with(mContext).load(listProductos.get(i).getImagenProducto()).apply(RequestOptions.circleCropTransform()).into(viewHolderVentas.image);

        if (tema) {
            viewHolderVentas.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.md_cyan_100));
        } else {
            viewHolderVentas.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.md_blue_grey_300));
        }

        viewHolderVentas.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, viewHolderVentas.menu);

                if (VariablesGenerales.productoActivo) {
                    popupMenu.inflate(R.menu.menu_items_ventas);
                } else {
                    //popupMenu.inflate(R.menu.menu_items_ventas_);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_ventas_editar:
                                VariablesGenerales.idProductoEditar = listProductos.get(position).getIdProducto();
                                mContext.startActivity(new Intent(mContext, EditarArticuloActivity.class));
                                break;

                            case R.id.menu_ventas_oferta:
                                break;

                            case R.id.menu_ventas_pausar:
                                if (VariablesGenerales.productoActivo) {
                                    pausarPublicacion(listProductos.get(position).getIdProducto(), false);
                                } else {
                                    pausarPublicacion(listProductos.get(position).getIdProducto(), true);
                                }
                                break;

                            case R.id.menu_ventas_quitar:
                                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                                dialog.setTitle("Confirmar");
                                dialog.setMessage("¿Desea eliminar este artículo?");

                                dialog.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.setIcon(R.drawable.ic_delete);
                                dialog.show();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

     }

    private void pausarPublicacion(String idProducto, boolean activo) {
        db.collection(VariablesEstaticas.BD_ALMACEN).document(idProducto).update(VariablesEstaticas.BD_PRODUCTO_ACTIVO, activo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateList(listProductos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listProductos.size();
    }


    public class ViewHolderVentas extends RecyclerView.ViewHolder {

        TextView tvNombreProducto;
        TextView tvPrecioProducto;
        TextView menu;
        ImageView image;
        CardView cardView;

        public ViewHolderVentas(@NonNull View itemView) {
            super(itemView);

            tvNombreProducto = itemView.findViewById(R.id.tv_producto_ventas);
            tvPrecioProducto = itemView.findViewById(R.id.textViewprecio_ventas);
            menu = itemView.findViewById(R.id.menu_ventas);
            image = itemView.findViewById(R.id.imageViewVentas);
            cardView = itemView.findViewById(R.id.cardViewVentas);

        }


    }

    public void updateList (ArrayList<ConstructorProductos> newList){
        listProductos = new ArrayList<>();
        listProductos.addAll(newList);
        notifyDataSetChanged();
    }

}


