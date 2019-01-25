package com.example.hchirinos.elmejorprecio;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
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
    String precioGuardar, precioTot;



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
    public void onBindViewHolder(@NonNull final ViewHolderCompras viewHolderCompras, final int i) {
         double precioGuar = listCompras.get(i).getPrecio_producto_compras();
         precioGuardar = String.valueOf(precioGuar);


        viewHolderCompras.textView_nombre_producto_compras.setText(listCompras.get(i).getNombre_producto_compras());
        viewHolderCompras.textView_marca_producto_compras.setText(listCompras.get(i).getMarca_producto_compras());
        viewHolderCompras.textView_precio_producto_compras.setText(String.valueOf(listCompras.get(i).getPrecio_producto_compras()));
        viewHolderCompras.checkBox_compras.setChecked(false);

        viewHolderCompras.checkBox_compras.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                String cod = listCompras.get(i).getCod_plu_compras();
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


        viewHolderCompras.btMas.setOnClickListener(new View.OnClickListener() {
            double precioUnitario  = listCompras.get(i).getPrecio_producto_compras();
            double precioTotal;
            @Override
            public void onClick(View v) {
                precioTot = cantidad(listCompras.get(i).getPrecio_producto_compras(), listCompras.get(i).getPrecio_producto_compras(),1);
                viewHolderCompras.precioTotalProducto.setText(precioTot);
            }
        });

       viewHolderCompras.btMenos.setOnClickListener(new View.OnClickListener() {
           double precioUnitario  = listCompras.get(i).getPrecio_producto_compras();
           double precioTotal;
           @Override
           public void onClick(View v) {
               precioTotal = precioTotal - precioUnitario;
               precioTot = String.valueOf(precioTotal);
               viewHolderCompras.precioTotalProducto.setText(precioTot);
           }
       });

     }

    public void delete_compras (ConstructorCompras i) {

        String url = "https://chirinoshl.000webhostapp.com/elmejorprecio/delete_compras.php?cod_plu="+ i.getCod_plu_compras();
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);

    }

    private void enviar_WS (String cod, String nombre, double precio, String marca, String imagen) {



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
        TextView textView_precio_producto_compras, precioTotalProducto;
        CheckBox checkBox_compras;
        ImageButton btMas, btMenos;

        public ViewHolderCompras(@NonNull View itemView) {
            super(itemView);

            textView_nombre_producto_compras = itemView.findViewById(R.id.textView_nombre_producto_compras);
            textView_marca_producto_compras = itemView.findViewById(R.id.textView_marca_producto_compras);
            textView_precio_producto_compras = itemView.findViewById(R.id.textView_precio_producto_compras);
            precioTotalProducto = itemView.findViewById(R.id.tvPrecioTotalProducto);
            checkBox_compras = itemView.findViewById(R.id.checkBox_compras);
            btMas = itemView.findViewById(R.id.btMas);
            btMenos = itemView.findViewById(R.id.btMenos);
        }


    }

    private String cantidad (double precioInicial, double precioUnitario, int i) {
        String precioResultado = String.valueOf(precioInicial);

        if ( i == 1) {
            precioInicial = precioInicial + precioUnitario;
            precioResultado = String.valueOf(precioInicial);
        } else if (i == 2) {
            precioInicial = precioInicial - precioUnitario;
            precioResultado = String.valueOf(precioInicial);
        }

        return precioResultado;
    }


}


