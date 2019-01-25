package com.example.hchirinos.elmejorprecio;

import com.google.firebase.firestore.GeoPoint;
import com.google.type.LatLng;

public class ConstructorTiendas {

    private String cod_tienda;
    private String nombre_tienda;
    private String sucursal;
    private String imagen;
    private double latitud;
    private double longitud;



    public ConstructorTiendas() {}


    public ConstructorTiendas(String nombre_tienda, String sucursal) {
        this.nombre_tienda = nombre_tienda;
        this.sucursal = sucursal;
    }

    public String getCod_tienda() {
        return cod_tienda;
    }

    public String getImagen() {
        return imagen;
    }

    public String getNombre_tienda() {
        return nombre_tienda;
    }

    public String getSucursal() {
        return sucursal;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }



    public void setNombre_tienda(String nombre_tienda) {
        this.nombre_tienda = nombre_tienda;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public void setCod_tienda(String cod_tienda) {
        this.cod_tienda = cod_tienda;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }


}
