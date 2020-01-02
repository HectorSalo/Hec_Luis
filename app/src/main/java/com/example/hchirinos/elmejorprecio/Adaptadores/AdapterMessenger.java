package com.example.hchirinos.elmejorprecio.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.emoji.widget.EmojiTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hchirinos.elmejorprecio.Constructores.ConstructorMessenger;
import com.example.hchirinos.elmejorprecio.Constructores.ConstructorVendedores;
import com.example.hchirinos.elmejorprecio.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class AdapterMessenger extends RecyclerView.Adapter<AdapterMessenger.ViewHolderMessenger> {

    private Context mctx;
    private ArrayList<ConstructorMessenger> listMensajes;
    private static final int MSG_DERECHA = 0;
    private static final int MSG_IZQUIERDA = 1;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public AdapterMessenger(Context mctx, ArrayList<ConstructorMessenger> listMensajes) {
        this.mctx = mctx;
        this.listMensajes = listMensajes;
    }


    @NonNull
    @Override
    public AdapterMessenger.ViewHolderMessenger onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_DERECHA) {
            View view = LayoutInflater.from(mctx).inflate(R.layout.list_chat_derecha, parent, false);
            return new AdapterMessenger.ViewHolderMessenger(view);
        } else {
            View view = LayoutInflater.from(mctx).inflate(R.layout.list_chat_izquierda, parent, false);
            return new AdapterMessenger.ViewHolderMessenger(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMessenger.ViewHolderMessenger holder, int position) {

        holder.mostrarMsg.setText(listMensajes.get(position).getMensaje());
    }

    @Override
    public int getItemCount() {
        return listMensajes.size();
    }

    public class ViewHolderMessenger extends RecyclerView.ViewHolder {

        EmojiTextView mostrarMsg;
        public ViewHolderMessenger(@NonNull View itemView) {
            super(itemView);

            mostrarMsg = itemView.findViewById(R.id.textView_showmsg);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (listMensajes.get(position).getEmisor().equals(user.getUid())) {
            return MSG_DERECHA;
        } else {
            return MSG_IZQUIERDA;
        }
    }

    public void updateList (ArrayList<ConstructorMessenger> newList){

        listMensajes = new ArrayList<>();
        listMensajes.addAll(newList);
        notifyDataSetChanged();
    }
}
