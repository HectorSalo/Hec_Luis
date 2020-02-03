package com.skysam.hchirinos.elmejorprecio.Constructores;

import java.util.Date;

public class ConstructorMessenger {

    private String emisor, receptor, mensaje, idMensaje, imagen, email, nombreReceptor;
    private Date fechaEnvio, ultimaConexion;
    private boolean onLine;

    public ConstructorMessenger(String emisor, String receptor, String mensaje, Date fechaEnvio, String idMensaje, String imagen, String email, String nombreReceptor, boolean onLine) {
        this.emisor = emisor;
        this.receptor = receptor;
        this.mensaje = mensaje;
        this.idMensaje = idMensaje;
        this.nombreReceptor = nombreReceptor;
        this.imagen = imagen;
        this.email = email;
        this.fechaEnvio = fechaEnvio;
        this.onLine = onLine;
    }


    public ConstructorMessenger (){}

    public Date getUltimaConexion() {
        return ultimaConexion;
    }

    public void setUltimaConexion(Date ultimaConexion) {
        this.ultimaConexion = ultimaConexion;
    }

    public boolean isOnLine() {
        return onLine;
    }

    public void setOnLine(boolean onLine) {
        this.onLine = onLine;
    }

    public String getNombreReceptor() {
        return nombreReceptor;
    }

    public void setNombreReceptor(String nombreReceptor) {
        this.nombreReceptor = nombreReceptor;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(String idMensaje) {
        this.idMensaje = idMensaje;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }
}
