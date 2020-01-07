package com.example.hchirinos.elmejorprecio.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorMessenger;
import com.example.hchirinos.elmejorprecio.Variables.VariablesGenerales;
import com.example.hchirinos.elmejorprecio.ui.FragmentChat.InterfaceRecyclerViewConversaciones;
import com.example.hchirinos.elmejorprecio.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterConversacionesChat extends RecyclerView.Adapter<AdapterConversacionesChat.ViewHolder> implements View.OnClickListener{
    private ArrayList<ConstructorMessenger> listConversaciones;
    private View.OnClickListener listener;
    private InterfaceRecyclerViewConversaciones interfaceRecyclerViewConversaciones;
    private Context mContext;

    public AdapterConversacionesChat(ArrayList<ConstructorMessenger> listConversaciones, Context mContext, InterfaceRecyclerViewConversaciones interfaceRecyclerViewConversaciones) {
        this.listConversaciones = listConversaciones;
        this.mContext = mContext;
        this.interfaceRecyclerViewConversaciones = interfaceRecyclerViewConversaciones;
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
    public void onBindViewHolder(@NonNull AdapterConversacionesChat.ViewHolder holder, final int position) {
        Date fechaConexion = new Date();
        fechaConexion = listConversaciones.get(position).getUltimaConexion();


        holder.nombreUsuario.setText(listConversaciones.get(position).getNombreReceptor());

        if(VariablesGenerales.verCheckBoxes) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        interfaceRecyclerViewConversaciones.selectedChat(position);
                    } else {
                        interfaceRecyclerViewConversaciones.unSelectedChat(position);
                    }
                }
            });
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }

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
        public final CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            imagenUsuario = (ImageView) itemView.findViewById(R.id.imageConversacionesChat);
            nombreUsuario = (TextView) itemView.findViewById(R.id.nombre_conversaciones_chat);
            ultimaConexionUsuario = (TextView) itemView.findViewById(R.id.ultima_conexion_conversaciones_chat);
            onLine = (TextView) itemView.findViewById(R.id.textViewOnLine);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBoxSelectUsuarioChat);

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (!VariablesGenerales.verCheckBoxes) {
                            interfaceRecyclerViewConversaciones.onLongItemClick(getAdapterPosition());
                            VariablesGenerales.verCheckBoxes = true;
                            updateList(listConversaciones);
                            checkBox.setChecked(true);
                        }

                        return true;
                    }
                });

        }
    }
}
