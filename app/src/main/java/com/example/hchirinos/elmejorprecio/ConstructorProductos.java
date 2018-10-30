package com.example.hchirinos.elmejorprecio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class ConstructorProductos {

    private String nombre_producto;
    private String marca_producto;
    private double precio_producto;
    private String imagen_producto;
    private Bitmap imagen_bitmap;

    public ConstructorProductos() {}


    public ConstructorProductos(String nombre_producto, String marca_producto, double precio_producto, String imagen_producto) {
        this.nombre_producto = nombre_producto;
        this.marca_producto = marca_producto;
        this.precio_producto = precio_producto;
        this.imagen_producto = imagen_producto;
    }


    public String getNombre_producto() {
        return nombre_producto;
    }

    public String getMarca_producto() {
        return marca_producto;
    }

    public double getPrecio_producto() {
        return precio_producto;
    }

    public String getImagen_producto() {
        return imagen_producto;
    }


    public void setNombre_producto(String nombre_producto) {
        this.nombre_producto = nombre_producto;
    }

    public void setMarca_producto(String marca_producto) {
        this.marca_producto = marca_producto;
    }

    public void setPrecio_producto(double precio_producto) {
        this.precio_producto = precio_producto;
    }

    public void setImagen_producto(String imagen_producto) {
        this.imagen_producto = imagen_producto;

        byte [] byteCode = Base64.decode(imagen_producto, Base64.DEFAULT);
        this.imagen_bitmap = BitmapFactory.decodeByteArray(byteCode, 0, byteCode.length);
    }

    public Bitmap getImagen_bitmap() {
        return imagen_bitmap;
    }

    public void setImagen_bitmap(Bitmap imagen_bitmap) {
        this.imagen_bitmap = imagen_bitmap;
    }
}


