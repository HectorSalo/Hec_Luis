package com.skysam.hchirinos.elmejorprecio.Constructores;

import java.util.ArrayList;
import java.util.Date;

public class ConstructorProductos {

    private String idProducto;
    private String descripcionProducto;
    private double precioProducto;
    private String imagenProducto;
    private double cantidadProducto;
    private String vendedor;
    private String unidadProducto;
    private Date fechaIngreso;
    private String estadoProducto;
    private String nombreProducto;
    private ArrayList<String> listUsuariosFavoritos;
    private boolean productoActivo, oferta;


    public ConstructorProductos() {}

    public ConstructorProductos(String idProducto, String descripcionProducto, double precioProducto, String imagenProducto, double cantidadProducto,
                                String vendedor, String unidadProducto, Date fechaIngreso, String nombreProducto, ArrayList<String> listUsuariosFavoritos, boolean productoActivo, boolean oferta) {
        this.idProducto = idProducto;
        this.descripcionProducto = descripcionProducto;
        this.precioProducto = precioProducto;
        this.imagenProducto = imagenProducto;
        this.cantidadProducto = cantidadProducto;
        this.vendedor = vendedor;
        this.unidadProducto = unidadProducto;
        this.fechaIngreso = fechaIngreso;
        this.nombreProducto = nombreProducto;
        this.listUsuariosFavoritos = listUsuariosFavoritos;
        this.productoActivo = productoActivo;
        this.oferta = oferta;
    }


    public boolean isOferta() {
        return oferta;
    }

    public void setOferta(boolean oferta) {
        this.oferta = oferta;
    }

    public boolean isProductoActivo() {
        return productoActivo;
    }

    public void setProductoActivo(boolean productoActivo) {
        this.productoActivo = productoActivo;
    }

    public ArrayList<String> getListUsuariosFavoritos() {
        return listUsuariosFavoritos;
    }

    public void setListUsuariosFavoritos(ArrayList<String> listUsuariosFavoritos) {
        this.listUsuariosFavoritos = listUsuariosFavoritos;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
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

    public double getCantidadProducto() {
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

    public void setCantidadProducto(double cantidadProducto) {
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


