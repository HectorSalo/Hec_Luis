package com.skysam.hchirinos.elmejorprecio.ui.FragmentChat;

public interface InterfaceRecyclerViewConversaciones {
    void onLongItemClick(int position);

    void selectedChat(int position);

    void unSelectedChat(int position);

    void borrarSelecciones();
}
