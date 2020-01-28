package com.example.hchirinos.elmejorprecio.Adaptadores;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorMessenger;
import com.example.hchirinos.elmejorprecio.R;
import com.example.hchirinos.elmejorprecio.ui.FragmentChat.UsuariosChatFragment.OnListFragmentInteractionListener;
import com.example.hchirinos.elmejorprecio.ui.FragmentChat.dummy.DummyContent.DummyItem;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyUsuariosChatRecyclerViewAdapter extends RecyclerView.Adapter<MyUsuariosChatRecyclerViewAdapter.ViewHolder> implements View.OnClickListener {

    private ArrayList<ConstructorMessenger> listUsuarios;
    private View.OnClickListener listener;
    private Context mContext;

    public MyUsuariosChatRecyclerViewAdapter(ArrayList<ConstructorMessenger> listUsuarios, Context mContext) {
        this.listUsuarios = listUsuarios;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_usuarioschat, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.nombreUsuario.setText(listUsuarios.get(position).getNombreReceptor());
        holder.emailUsuario.setText(listUsuarios.get(position).getEmail());

        if (listUsuarios.get(position).getImagen() != null) {
            if (!listUsuarios.get(position).getImagen().isEmpty()){
                Glide.with(mContext).load(listUsuarios.get(position).getImagen()).apply(RequestOptions.circleCropTransform()).into(holder.imagenUsuario);
            } else {
                Glide.with(mContext).load(R.mipmap.ic_usuario_sin_imagen).apply(RequestOptions.circleCropTransform()).into(holder.imagenUsuario);
            }
        } else {
            Glide.with(mContext).load(R.mipmap.ic_usuario_sin_imagen).apply(RequestOptions.circleCropTransform()).into(holder.imagenUsuario);
        }




    }

    @Override
    public int getItemCount() {
        return listUsuarios.size();
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView imagenUsuario;
        public final TextView nombreUsuario;
        public final TextView emailUsuario;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            imagenUsuario = (ImageView) view.findViewById(R.id.imageUsuarioChat);
            nombreUsuario = (TextView) view.findViewById(R.id.nombre_usuario_chat);
            emailUsuario = (TextView) view.findViewById(R.id.email_usuario_chat);
        }

    }

    public void updateList (ArrayList<ConstructorMessenger> newList){

        listUsuarios = new ArrayList<>();
        listUsuarios.addAll(newList);
        notifyDataSetChanged();
    }

    public void setOnClickListener (View.OnClickListener listener) {
        this.listener = listener;
    }
}
