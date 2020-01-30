package com.example.hchirinos.elmejorprecio.Constructores;

import com.google.firebase.firestore.GeoPoint;
import com.google.type.LatLng;

public class ConstructorVendedores {

    private String idVendedor;
    private String nombreVendedor;
    private String telefonoVendedor;
    private String correoVendedor;
    private String imagen;
    private String ubicacionPreferida;
    private GeoPoint latlong;



   public ConstructorVendedores() {}

    public ConstructorVendedores(String idVendedor, String nombreVendedor, String telefonoVendedor, String correoVendedor, String imagen) {
        this.idVendedor = idVendedor;
        this.nombreVendedor = nombreVendedor;
        this.telefonoVendedor = telefonoVendedor;
        this.correoVendedor = correoVendedor;
        this.imagen = imagen;
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

    public String getUbicacionPreferida() {
        return ubicacionPreferida;
    }

    public GeoPoint getLatlong() {
        return latlong;
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

    public void setUbicacionPreferida(String ubicacionPreferida) {
        this.ubicacionPreferida = ubicacionPreferida;
    }

    public void setLatlong(GeoPoint latlong) {
        this.latlong = latlong;
    }
}
