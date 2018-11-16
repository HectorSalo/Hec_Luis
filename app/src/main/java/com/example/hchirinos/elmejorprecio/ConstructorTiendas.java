package com.example.hchirinos.elmejorprecio;

public class ConstructorTiendas {

    private int cod_tienda;
    private String nombre_tienda;
    private String sucursal;
    private String imagen;

    public ConstructorTiendas() {}


    public ConstructorTiendas(String nombre_tienda, String sucursal) {
        this.nombre_tienda = nombre_tienda;
        this.sucursal = sucursal;
    }

    public int getCod_tienda() {
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

    public void setNombre_tienda(String nombre_tienda) {
        this.nombre_tienda = nombre_tienda;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public void setCod_tienda(int cod_tienda) {
        this.cod_tienda = cod_tienda;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
