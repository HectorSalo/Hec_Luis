package com.example.hchirinos.elmejorprecio;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.BreakIterator;
import java.util.ArrayList;

public class AdapterCompras extends RecyclerView.Adapter<AdapterCompras.ViewHolderCompras> implements Response.Listener<JSONObject>, Response.ErrorListener {

    ArrayList<ConstructorCompras> listCompras;
    Context mContext;
    lista_compras lista_compras;


    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;


    public AdapterCompras(ArrayList<ConstructorCompras> listCompras, Context mContext) {
        this.listCompras = listCompras;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolderCompras onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_compras_layout, null, false);
        request = Volley.newRequestQueue(mContext);

        return new ViewHolderCompras(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCompras viewHolderCompras, final int i) {

        viewHolderCompras.textView_nombre_producto_compras.setText(listCompras.get(i).getNombre_producto_compras());
        viewHolderCompras.textView_marca_producto_compras.setText(listCompras.get(i).getMarca_producto_compras());
        viewHolderCompras.textView_precio_producto_compras.setText(String.valueOf(listCompras.get(i).getPrecio_producto_compras()));
        viewHolderCompras.checkBox_compras.setChecked(false);


        if (listCompras.get(i).getImagen_compras()!=null) {

            cargarimagen(listCompras.get(i).getImagen_compras(), viewHolderCompras);

        } else {
            viewHolderCompras.imageView_compras.setImageResource(R.drawable.common_google_signin_btn_icon_dark_focused);
        }



        viewHolderCompras.checkBox_compras.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                int cod = listCompras.get(i).getCod_plu_compras();
                String nombre = listCompras.get(i).getNombre_producto_compras();
                double precio = listCompras.get(i).getPrecio_producto_compras();
                String marca = listCompras.get(i).getMarca_producto_compras();
                String imagen = listCompras.get(i).getImagen_compras();


                if (isChecked) {
                    delete_compras(listCompras.get(i));
                    Toast.makeText(mContext, "Producto comprado", Toast.LENGTH_SHORT).show();

                } else {
                    enviar_WS(cod, nombre, precio, marca, imagen);
                    Toast.makeText(mContext, "Producto por comprar", Toast.LENGTH_SHORT).show();
                }
            }
        });

     }

    private void cargarimagen(String imagen_compras, final ViewHolderCompras viewHolderCompras) {

        String urlImagen = "https://chirinoshl.000webhostapp.com/elmejorprecio/" + imagen_compras;
        urlImagen = urlImagen.replace(" ", "%20");

        ImageRequest imageRequest = new ImageRequest(urlImagen, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                viewHolderCompras.imageView_compras.setImageBitmap(response);

            }
        }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
            }
        });
        request.add(imageRequest);
    }


    public void delete_compras (ConstructorCompras i) {

        String url = "https://chirinoshl.000webhostapp.com/elmejorprecio/delete_compras.php?cod_plu="+ i.getCod_plu_compras();
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);

    }

    private void enviar_WS (int cod, String nombre, double precio, String marca, String imagen) {



        String url = "https://chirinoshl.000webhostapp.com/elmejorprecio/enviar_compras.php?cod_plu="+ cod +"&nombre_plu="+ nombre +"&precio_plu="+ precio +"&marca_plu="+marca+"&imagen="+imagen;
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    @Override
    public int getItemCount() {
        return listCompras.size();
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(JSONObject response) {




    }

    public class ViewHolderCompras extends RecyclerView.ViewHolder {

        TextView textView_nombre_producto_compras;
        TextView textView_marca_producto_compras;
        TextView textView_precio_producto_compras;
        ImageView imageView_compras;
        CheckBox checkBox_compras;

        public ViewHolderCompras(@NonNull View itemView) {
            super(itemView);

            textView_nombre_producto_compras = itemView.findViewById(R.id.textView_nombre_producto_compras);
            textView_marca_producto_compras = itemView.findViewById(R.id.textView_marca_producto_compras);
            textView_precio_producto_compras = itemView.findViewById(R.id.textView_precio_producto_compras);
            imageView_compras = itemView.findViewById(R.id.imageView_producto_compras);
            checkBox_compras = itemView.findViewById(R.id.checkBox_compras);


        }


    }


}


