package com.example.hchirinos.elmejorprecio.Constructores;

import java.util.Date;

public class ConstructorMessenger {

    private String emisor, receptor, mensaje;
    private Date fechaEnvio;

    public ConstructorMessenger(String emisor, String receptor, String mensaje, Date fechaEnvio) {
        this.emisor = emisor;
        this.receptor = receptor;
        this.mensaje = mensaje;
        this.fechaEnvio = fechaEnvio;
    }

    public ConstructorMessenger (){}

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
