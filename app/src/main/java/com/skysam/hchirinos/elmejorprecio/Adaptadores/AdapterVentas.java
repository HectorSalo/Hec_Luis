package com.skysam.hchirinos.elmejorprecio.Adaptadores;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.skysam.hchirinos.elmejorprecio.Clases.AlarmReceiverOferta;
import com.skysam.hchirinos.elmejorprecio.Constructores.ConstructorProductos;
import com.skysam.hchirinos.elmejorprecio.EditarArticuloActivity;
import com.skysam.hchirinos.elmejorprecio.R;
import com.skysam.hchirinos.elmejorprecio.Variables.VariablesEstaticas;
import com.skysam.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class AdapterVentas extends RecyclerView.Adapter<AdapterVentas.ViewHolderVentas> {

    private ArrayList<ConstructorProductos> listProductos;
    private Context mContext;
    private boolean tema;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int numeroDiasOferta;

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
                popupMenu.inflate(R.menu.menu_items_ventas);
                Menu menu = popupMenu.getMenu();
                MenuItem menuItemPausar = menu.findItem(R.id.menu_ventas_pausar);
                MenuItem menuItemOferta = menu.findItem(R.id.menu_ventas_oferta);

                if (listProductos.get(position).isProductoActivo()) {
                    menuItemPausar.setTitle(mContext.getResources().getString(R.string.menu_ventas_pausar));
                    menuItemOferta.setVisible(true);

                    if (listProductos.get(position).isOferta()) {
                        menuItemOferta.setTitle(mContext.getResources().getString(R.string.menu_ventas_oferta_cancelar));
                    } else {
                        menuItemOferta.setTitle(mContext.getResources().getString(R.string.menu_ventas_oferta));
                    }
                } else {
                    menuItemPausar.setTitle(mContext.getResources().getString(R.string.menu_ventas_reanudar));
                    menuItemOferta.setVisible(false);
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
                                if (listProductos.get(position).isOferta()) {
                                    cancelarOferta(position, listProductos.get(position).getIdProducto(), listProductos.get(position).getPrecioProducto());
                                } else {
                                    colocarEnOferta(position, listProductos.get(position).getPrecioProducto());
                                }
                                break;

                            case R.id.menu_ventas_pausar:
                                if (listProductos.get(position).isProductoActivo()) {
                                    pausarPublicacion(listProductos.get(position).getIdProducto(), false);
                                    listProductos.get(position).setProductoActivo(false);
                                } else {
                                    pausarPublicacion(listProductos.get(position).getIdProducto(), true);
                                    listProductos.get(position).setProductoActivo(true);
                                }
                                break;

                            case R.id.menu_ventas_quitar:
                                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                                dialog.setTitle("Confirmar");
                                dialog.setMessage("¿Desea eliminar este artículo?");

                                dialog.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        eliminarArticulo(listProductos.get(position).getIdProducto(), position);
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

    private void cancelarOferta(int position, String idProducto, double precioOferta) {
        EditText editText = new EditText(mContext);
        editText.setText(String.valueOf(precioOferta));
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("Confirmar");
        dialog.setMessage("¿Desea cancelar esta oferta?");
        dialog.setView(editText);

        dialog.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                double precio = Double.parseDouble(editText.getText().toString());
                db.collection(VariablesEstaticas.BD_ALMACEN).document(idProducto).update(VariablesEstaticas.BD_OFERTA_SEMANA, false, VariablesEstaticas.BD_PRECIO_PRODUCTO, precio).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        cancelarIntent(listProductos.get(position).getFechaIngreso());
                        listProductos.get(position).setPrecioProducto(precio);
                        listProductos.get(position).setOferta(false);
                        updateList(listProductos);
                    }
                });
            }
        });
        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setIcon(R.drawable.ic_delete);
        dialog.show();
    }

    private void cancelarIntent(Date fecha) {
        int idIntent = (int) fecha.getTime();

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, AlarmReceiverOferta.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,idIntent , intent, 0);

        pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);
    }

    private void colocarEnOferta(int position, double precio) {
        Date fecha = listProductos.get(position).getFechaIngreso();
        String idProducto = listProductos.get(position).getIdProducto();
        String nombre = listProductos.get(position).getNombreProducto();
        EditText editText = new EditText(mContext);
        editText.setHint("Ingrese el precio de Oferta");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        String[] listaTiempoOferta = mContext.getResources().getStringArray(R.array.tiempo_en_oferta);

        AlertDialog.Builder dialogOferta = new AlertDialog.Builder(mContext);
        dialogOferta.setTitle("Tiempo en oferta:");
        dialogOferta.setSingleChoiceItems(listaTiempoOferta, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        numeroDiasOferta = 1;
                        break;
                    case 1:
                        numeroDiasOferta = 3;
                        break;
                    case 2:
                        numeroDiasOferta = 5;
                        break;
                    case 3:
                        numeroDiasOferta = 7;
                        break;
                }
            }
        });
        dialogOferta.setView(editText);
        dialogOferta.setPositiveButton("OK", null);
        dialogOferta.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = dialogOferta.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button buttonPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                buttonPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (numeroDiasOferta == 0) {
                            numeroDiasOferta = 1;
                        }

                        if (editText.getText().toString().isEmpty()) {
                            editText.setError("Este campo no  puede estar vacío");
                        } else {
                            double precioNuevo = Double.parseDouble(editText.getText().toString());
                            if (precioNuevo >= precio) {
                                editText.setError("El precio debe ser menor que el anterior");
                            } else {
                                db.collection(VariablesEstaticas.BD_ALMACEN).document(idProducto).update(VariablesEstaticas.BD_OFERTA_SEMANA, true, VariablesEstaticas.BD_PRECIO_PRODUCTO, precioNuevo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        programarTiempoOferta(fecha, numeroDiasOferta, idProducto, precio, nombre);
                                        listProductos.get(position).setPrecioProducto(precioNuevo);
                                        listProductos.get(position).setOferta(true);
                                        updateList(listProductos);
                                    }
                                });
                                alertDialog.dismiss();
                            }
                        }
                    }
                });
            }
        });

        alertDialog.show();
    }

    private void programarTiempoOferta(Date fecha, int numeroDiasOferta, String idProducto, double precioViejo, String nombre) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String idUsuario = user.getUid();

        int idIntent = (int) fecha.getTime();

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, AlarmReceiverOferta.class);
        Bundle bundle = new Bundle();
        bundle.putString("idProducto", idProducto);
        bundle.putDouble("precioViejo", precioViejo);
        bundle.putString("nombre", nombre);
        bundle.putString("idUsuario", idUsuario);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,idIntent , intent, 0);

        alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + (1000 * 60 * 60 * 24 * numeroDiasOferta), pendingIntent);
    }

    private void eliminarArticulo(String idProducto, int position) {
        db.collection(VariablesEstaticas.BD_ALMACEN).document(idProducto).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(mContext, "Artículo eliminado", Toast.LENGTH_SHORT).show();
                listProductos.remove(position);
                updateList(listProductos);
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


