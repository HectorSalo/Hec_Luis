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
import com.example.hchirinos.elmejorprecio.R;

import java.util.ArrayList;

public class AdapterConversacionesChat extends RecyclerView.Adapter<AdapterConversacionesChat.ViewHolder> implements View.OnClickListener{
    private ArrayList<ConstructorMessenger> listConversaciones;
    private View.OnClickListener listener;
    private Context mContext;

    public AdapterConversacionesChat(ArrayList<ConstructorMessenger> listConversaciones, Context mContext) {
        this.listConversaciones = listConversaciones;
        this.mContext = mContext;
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
        holder.nombreUsuario.setText(listConversaciones.get(position).getNombreReceptor());
        holder.emailUsuario.setText(listConversaciones.get(position).getEmail());

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
        public final TextView emailUsuario;
        public final TextView onLine;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            imagenUsuario = (ImageView) itemView.findViewById(R.id.imageConversacionesChat);
            nombreUsuario = (TextView) itemView.findViewById(R.id.nombre_conversaciones_chat);
            emailUsuario = (TextView) itemView.findViewById(R.id.email_conversaciones_chat);
            onLine = (TextView) itemView.findViewById(R.id.textViewOnLine);
        }
    }
}
