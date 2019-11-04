package com.example.hchirinos.elmejorprecio;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterProductos extends RecyclerView.Adapter<AdapterProductos.ViewHolderProductos>{

    ArrayList<ConstructorProductos> listProductos;
    Context mContext;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;


    public AdapterProductos (ArrayList<ConstructorProductos> listProductos, Context mContext){
        this.listProductos = listProductos;
        this.mContext = mContext;
        request = Volley.newRequestQueue(mContext);
    }


    @NonNull
    @Override
    public AdapterProductos.ViewHolderProductos onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        //Enlaza el adaptador con list_producto_layout.xml
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_producto_layout,null, false);

        return new ViewHolderProductos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderProductos viewHolderProductos, final int i) {



        //Comunica el adaptador con la clase ViewHolderProductos
        viewHolderProductos.textView_nombre_producto.setText(listProductos.get(i).getNombre_producto());
        viewHolderProductos.textView_marca_producto.setText(listProductos.get(i).getMarca_producto());
        viewHolderProductos.textView_precio_producto.setText(String.valueOf(listProductos.get(i).getPrecio_producto()));

        if (listProductos.get(i).getImagen_producto()!=null) {

           Glide.with(mContext).load(listProductos.get(i).getImagen_producto()).into(viewHolderProductos.imageView_producto);

        } else {
            viewHolderProductos.imageView_producto.setImageResource(R.drawable.common_google_signin_btn_icon_dark_focused);
        }

        viewHolderProductos.textView_option_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // Display option menu


                PopupMenu popupMenu = new PopupMenu(mContext, viewHolderProductos.textView_option_item);
                popupMenu.inflate(R.menu.producto_option_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.option_compartir:

                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                intent.putExtra(Intent.EXTRA_TEXT, "¿Qué te parece esta oferta? \n"+listProductos.get(i).getNombre_producto()+"\n" + "Bs. "+listProductos.get(i).getPrecio_producto());
                                mContext.startActivity(Intent.createChooser(intent, "Compartir con"));

                                break;

                            case R.id.option_compras:

                                enviar_WS(v, listProductos.get(i));

                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });


    }


    private void enviar_WS (View v, ConstructorProductos i) {
        AdminSQLiteHelper conectDB = new AdminSQLiteHelper(mContext, "MyList", null, AdminSQLiteHelper.VERSION);

        SQLiteDatabase db = conectDB.getWritableDatabase();

        String idPlu = i.getCodigo_plu();
        String cantidad = String.valueOf(1);

        ContentValues registro = new ContentValues();
        registro.put("idProducto", idPlu);
        registro.put("cantidad", cantidad);


        db.insert("compras", null, registro);
        db.close();

        Snackbar.make(v, "Guardado en Lista de Compras", Snackbar.LENGTH_LONG).setAction("Ver Lista", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, lista_compras.class);
                mContext.startActivity(intent);
            }
        }).show();
    }

    @Override
    public int getItemCount() {
        return listProductos.size();
    }





    public class ViewHolderProductos extends RecyclerView.ViewHolder {

        //Referencia los elementos del archivo list_producto_layout.xml
        TextView textView_nombre_producto;
        TextView textView_marca_producto;
        TextView textView_precio_producto;
        TextView textView_option_item;
        ImageView imageView_producto;



        public ViewHolderProductos(@NonNull View itemView) {
            super(itemView);

            textView_nombre_producto = (TextView) itemView.findViewById(R.id.textView_nombre_producto);
            textView_marca_producto = (TextView) itemView.findViewById(R.id.textView_marca_producto);
            textView_precio_producto = (TextView) itemView.findViewById(R.id.textView_precio_producto);
            textView_option_item=  itemView.findViewById(R.id.textView_producto_option);
            imageView_producto = (ImageView) itemView.findViewById(R.id.imageView_producto);

        }
    }

    public void updateList (ArrayList<ConstructorProductos> newList){

        listProductos = new ArrayList<>();
        listProductos.addAll(newList);
        notifyDataSetChanged();
    }


}
