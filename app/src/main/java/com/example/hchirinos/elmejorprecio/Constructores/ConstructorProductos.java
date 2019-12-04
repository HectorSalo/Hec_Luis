package com.example.hchirinos.elmejorprecio.Constructores;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.util.Date;

public class ConstructorProductos {

    private String idProducto;
    private String descripcionProducto;
    private double precioProducto;
    private String imagenProducto;
    private int cantidadProducto;
    private String vendedor;
    private String unidadProducto;
    private Date fechaIngreso;
    private String estadoProducto;


    public ConstructorProductos() {}

    public ConstructorProductos(String idProducto, String descripcionProducto, double precioProducto, String imagenProducto, int cantidadProducto, String vendedor, String unidadProducto, Date fechaIngreso) {
        this.idProducto = idProducto;
        this.descripcionProducto = descripcionProducto;
        this.precioProducto = precioProducto;
        this.imagenProducto = imagenProducto;
        this.cantidadProducto = cantidadProducto;
        this.vendedor = vendedor;
        this.unidadProducto = unidadProducto;
        this.fechaIngreso = fechaIngreso;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public String getDescripcionProducto() {
        return descripcionProducto;
    }

    public double getPrecioProducto() {
        return precioProducto;
    }

    public String getImagenProducto() {
        return imagenProducto;
    }

    public int getCantidadProducto() {
        return cantidadProducto;
    }

    public String getVendedor() {
        return vendedor;
    }

    public String getUnidadProducto() {
        return unidadProducto;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public String getEstadoProducto() {
        return estadoProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }

    public void setPrecioProducto(double precioProducto) {
        this.precioProducto = precioProducto;
    }

    public void setImagenProducto(String imagenProducto) {
        this.imagenProducto = imagenProducto;
    }

    public void setCantidadProducto(int cantidadProducto) {
        this.cantidadProducto = cantidadProducto;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public void setUnidadProducto(String unidadProducto) {
        this.unidadProducto = unidadProducto;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public void setEstadoProducto(String estadoProducto) {
        this.estadoProducto = estadoProducto;
    }
}


