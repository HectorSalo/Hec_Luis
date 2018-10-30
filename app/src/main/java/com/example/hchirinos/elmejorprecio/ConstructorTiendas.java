package com.example.hchirinos.elmejorprecio;

public class ConstructorTiendas {

    private String nombre_tienda;
    private String sucursal;

    public ConstructorTiendas() {}


    public ConstructorTiendas(String nombre_tienda, String sucursal) {
        this.nombre_tienda = nombre_tienda;
        this.sucursal = sucursal;
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
}
