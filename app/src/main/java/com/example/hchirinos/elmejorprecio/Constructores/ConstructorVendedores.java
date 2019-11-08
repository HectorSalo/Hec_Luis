package com.example.hchirinos.elmejorprecio.Constructores;

import com.google.firebase.firestore.GeoPoint;
import com.google.type.LatLng;

public class ConstructorVendedores {

    private String idVendedor;
    private String nombreVendedor;
    private String telefonoVendedor;
    private String correoVendedor;
    private String imagen;
    private double latitud;
    private double longitud;



   public ConstructorVendedores() {}

    public ConstructorVendedores(String idVendedor, String nombreVendedor, String telefonoVendedor, String correoVendedor, String imagen, double latitud, double longitud) {
        this.idVendedor = idVendedor;
        this.nombreVendedor = nombreVendedor;
        this.telefonoVendedor = telefonoVendedor;
        this.correoVendedor = correoVendedor;
        this.imagen = imagen;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getIdVendedor() {
        return idVendedor;
    }

    public String getNombreVendedor() {
        return nombreVendedor;
    }

    public String getTelefonoVendedor() {
        return telefonoVendedor;
    }

    public String getCorreoVendedor() {
        return correoVendedor;
    }

    public String getImagen() {
        return imagen;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setIdVendedor(String idVendedor) {
        this.idVendedor = idVendedor;
    }

    public void setNombreVendedor(String nombreVendedor) {
        this.nombreVendedor = nombreVendedor;
    }

    public void setTelefonoVendedor(String telefonoVendedor) {
        this.telefonoVendedor = telefonoVendedor;
    }

    public void setCorreoVendedor(String correoVendedor) {
        this.correoVendedor = correoVendedor;
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
