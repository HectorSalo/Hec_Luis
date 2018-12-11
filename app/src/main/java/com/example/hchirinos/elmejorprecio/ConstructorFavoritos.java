package com.example.hchirinos.elmejorprecio;

public class ConstructorFavoritos {

    private int cod_tienda;
    private String nombre_tienda;
    private String sucursal;
    private String imagen;
    private double latitud_origen;
    private double longitud_origen;
    private double latitud;
    private double longitud;

    public ConstructorFavoritos() {}

    public int getCod_tienda() {
        return cod_tienda;
    }

    public String getNombre_tienda() {
        return nombre_tienda;
    }

    public String getSucursal() {
        return sucursal;
    }

    public String getImagen() {
        return imagen;
    }

    public double getLatitud_origen() {
        return latitud_origen;
    }

    public double getLongitud_origen() {
        return longitud_origen;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setCod_tienda(int cod_tienda) {
        this.cod_tienda = cod_tienda;
    }

    public void setNombre_tienda(String nombre_tienda) {
        this.nombre_tienda = nombre_tienda;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setLatitud_origen(double latitud_origen) {
        this.latitud_origen = latitud_origen;
    }

    public void setLongitud_origen(double longitud_origen) {
        this.longitud_origen = longitud_origen;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}
