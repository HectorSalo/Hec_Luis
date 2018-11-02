package com.example.hchirinos.elmejorprecio;

import android.graphics.Bitmap;
import android.widget.CheckBox;

public class ConstructorCompras {

    private String nombre_producto_compras;
    private String marca_producto_compras;
    private double precio_producto_compras;
    private Bitmap imagen_bitmap;
    private CheckBox checkBox_compras;

    public ConstructorCompras () {}

    public ConstructorCompras(String nombre_producto_compras, String marca_producto_compras, double precio_producto_compras, Bitmap imagen_bitmap, CheckBox checkBox_compras) {
        this.nombre_producto_compras = nombre_producto_compras;
        this.marca_producto_compras = marca_producto_compras;
        this.precio_producto_compras = precio_producto_compras;
        this.imagen_bitmap = imagen_bitmap;
        this.checkBox_compras = checkBox_compras;
    }


    public String getNombre_producto_compras() {
        return nombre_producto_compras;
    }

    public String getMarca_producto_compras() {
        return marca_producto_compras;
    }

    public double getPrecio_producto_compras() {
        return precio_producto_compras;
    }

    public Bitmap getImagen_bitmap() {
        return imagen_bitmap;
    }

    public CheckBox getCheckBox_compras() {
        return checkBox_compras;
    }


    public void setNombre_producto_compras(String nombre_producto_compras) {
        this.nombre_producto_compras = nombre_producto_compras;
    }

    public void setMarca_producto_compras(String marca_producto_compras) {
        this.marca_producto_compras = marca_producto_compras;
    }

    public void setPrecio_producto_compras(double precio_producto_compras) {
        this.precio_producto_compras = precio_producto_compras;
    }

    public void setImagen_bitmap(Bitmap imagen_bitmap) {
        this.imagen_bitmap = imagen_bitmap;
    }

    public void setCheckBox_compras(CheckBox checkBox_compras) {
        this.checkBox_compras = checkBox_compras;
    }
}
