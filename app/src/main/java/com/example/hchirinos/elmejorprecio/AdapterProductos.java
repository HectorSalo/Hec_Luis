package com.example.hchirinos.elmejorprecio;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.ArrayList;

public class AdapterProductos extends RecyclerView.Adapter<AdapterProductos.ViewHolderProductos> implements Response.Listener<JSONObject>, Response.ErrorListener {

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

           cargarimagen(listProductos.get(i).getImagen_producto(), viewHolderProductos);

        } else {
            viewHolderProductos.imageView_producto.setImageResource(R.drawable.common_google_signin_btn_icon_dark_focused);
        }

        viewHolderProductos.textView_option_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                                enviar_WS(listProductos.get(i));

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

    private void cargarimagen(String imagen_producto, final ViewHolderProductos viewHolderProductos) {

        String urlImagen = "https://chirinoshl.000webhostapp.com/elmejorprecio/" + imagen_producto;
        urlImagen = urlImagen.replace(" ", "%20");

        ImageRequest imageRequest = new ImageRequest(urlImagen, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {

                viewHolderProductos.imageView_producto.setImageBitmap(response);

            }
        }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mContext, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
            }
        });
        request.add(imageRequest);
    }

    private void enviar_WS (ConstructorProductos i) {



        String url = "https://chirinoshl.000webhostapp.com/elmejorprecio/enviar_compras.php?cod_plu="+ i.getCodigo_plu() +"&nombre_plu="+i.getNombre_producto()+"&precio_plu="+i.getPrecio_producto()+"&marca_plu="+i.getMarca_producto()+"&imagen="+i.getImagen_producto();
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    @Override
    public int getItemCount() {
        return listProductos.size();
    }

    @Override
    public void onErrorResponse(VolleyError error) {

        Toast.makeText(mContext, "Error al guardar", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onResponse(JSONObject response) {

        Toast.makeText(mContext, "Guardado en Lista de Compras", Toast.LENGTH_LONG).show();

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
