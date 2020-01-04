package com.example.hchirinos.elmejorprecio.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorMessenger;
import com.example.hchirinos.elmejorprecio.ui.FragmentChat.OnLongClickRecyclerView;
import com.example.hchirinos.elmejorprecio.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterConversacionesChat extends RecyclerView.Adapter<AdapterConversacionesChat.ViewHolder> implements View.OnClickListener{
    private ArrayList<ConstructorMessenger> listConversaciones;
    private View.OnClickListener listener;
    private OnLongClickRecyclerView onLongClickRecyclerView;
    private Context mContext;

    public AdapterConversacionesChat(ArrayList<ConstructorMessenger> listConversaciones, Context mContext, OnLongClickRecyclerView onLongClickRecyclerView) {
        this.listConversaciones = listConversaciones;
        this.mContext = mContext;
        this.onLongClickRecyclerView = onLongClickRecyclerView;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    public void updateList (ArrayList<ConstructorMessenger> newList){
        listConversaciones = new ArrayList<>();
        listConversaciones.addAll(newList);
        notifyDataSetChanged();
    }

    public void setOnClickListener (View.OnClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public AdapterConversacionesChat.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_conversacioneschat, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterConversacionesChat.ViewHolder holder, int position) {
        Date fechaConexion = new Date();
        fechaConexion = listConversaciones.get(position).getUltimaConexion();

        holder.nombreUsuario.setText(listConversaciones.get(position).getNombreReceptor());

        if(listConversaciones.get(position).isOnLine()) {
            holder.ultimaConexionUsuario.setText("Conectado");
        } else {
            holder.ultimaConexionUsuario.setText(new SimpleDateFormat("EEE d MMM h:mm a").format(fechaConexion));
        }


        if(listConversaciones.get(position).isOnLine()) {
            holder.onLine.setBackgroundColor(ContextCompat.getColor(mContext, R.color.md_teal_A400));
        } else {
            holder.onLine.setBackgroundColor(ContextCompat.getColor(mContext, R.color.md_blue_grey_500_75));
        }

        Glide.with(mContext).load(listConversaciones.get(position).getImagen()).apply(RequestOptions.circleCropTransform()).into(holder.imagenUsuario);

    }

    @Override
    public int getItemCount() {
        return listConversaciones.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView imagenUsuario;
        public final TextView nombreUsuario;
        public final TextView ultimaConexionUsuario;
        public final TextView onLine;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            imagenUsuario = (ImageView) itemView.findViewById(R.id.imageConversacionesChat);
            nombreUsuario = (TextView) itemView.findViewById(R.id.nombre_conversaciones_chat);
            ultimaConexionUsuario = (TextView) itemView.findViewById(R.id.ultima_conexion_conversaciones_chat);
            onLine = (TextView) itemView.findViewById(R.id.textViewOnLine);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onLongClickRecyclerView.onLongItemClick(getAdapterPosition());
                    return true;
                }
            });
        }
    }
}
