package com.example.hchirinos.elmejorprecio.Adaptadores;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hchirinos.elmejorprecio.AdminSQLiteHelper;
import com.example.hchirinos.elmejorprecio.ConstructorCompras;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorProductos;
import com.example.hchirinos.elmejorprecio.R;

import java.util.ArrayList;

public class AdapterVentas extends RecyclerView.Adapter<AdapterVentas.ViewHolderVentas> {

    private ArrayList<ConstructorProductos> listProductos;
    private Context mContext;


    public AdapterVentas(ArrayList<ConstructorProductos> listProductos, Context mContext) {
        this.listProductos = listProductos;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolderVentas onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_ventas_layout, null, false);

        return new ViewHolderVentas(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderVentas viewHolderVentas, int i) {

        viewHolderVentas.tvNombreProducto.setText(listProductos.get(i).getDescripcionProducto());
        Log.d("Test", listProductos.get(i).getDescripcionProducto());
        viewHolderVentas.tvPrecioProducto.setText("$ " + listProductos.get(i).getPrecioProducto());

        Glide.with(mContext).load(listProductos.get(i).getImagenProducto()).apply(RequestOptions.circleCropTransform()).into(viewHolderVentas.image);

     }

    @Override
    public int getItemCount() {
        return listProductos.size();
    }


    public class ViewHolderVentas extends RecyclerView.ViewHolder {

        TextView tvNombreProducto;
        TextView tvPrecioProducto;
        ImageView image;

        public ViewHolderVentas(@NonNull View itemView) {
            super(itemView);

            tvNombreProducto = itemView.findViewById(R.id.tv_producto_ventas);
            tvPrecioProducto = itemView.findViewById(R.id.textViewprecio_ventas);
            image = itemView.findViewById(R.id.imageViewVentas);

        }


    }

    public void updateList (ArrayList<ConstructorProductos> newList){

        listProductos = new ArrayList<>();
        listProductos.addAll(newList);
        notifyDataSetChanged();
    }

}


