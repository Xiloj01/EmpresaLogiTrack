package com.example.demo.model;

public class Paquete {
    private String id;
    private String cliente;
    private double peso;
    private String destinoId;
    private EstadoPaquete estado;
    private String centroActualId;

    public enum EstadoPaquete {
        PENDIENTE, EN_TRANSITO, ENTREGADO
    }

    public Paquete() {
        this.estado = EstadoPaquete.PENDIENTE;
    }

    public Paquete(String id, String cliente, double peso, String destinoId, String centroActualId) {
        this.id = id;
        this.cliente = cliente;
        this.peso = peso;
        this.destinoId = destinoId;
        this.centroActualId = centroActualId;
        this.estado = EstadoPaquete.PENDIENTE;
    }

    public boolean puedeSerEnviado() {
        return estado == EstadoPaquete.PENDIENTE;
    }

    public void marcarEnTransito() {
        if (estado != EstadoPaquete.PENDIENTE) {
            throw new IllegalStateException("Solo paquetes PENDIENTES pueden pasar a EN_TRANSITO");
        }
        this.estado = EstadoPaquete.EN_TRANSITO;
    }

    public void marcarEntregado() {
        if (estado != EstadoPaquete.EN_TRANSITO) {
            throw new IllegalStateException("Solo paquetes EN_TRANSITO pueden ser ENTREGADOS");
        }
        this.estado = EstadoPaquete.ENTREGADO;
    }

    public boolean validarPeso() {
        return peso > 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public String getDestinoId() {
        return destinoId;
    }

    public void setDestinoId(String destinoId) {
        this.destinoId = destinoId;
    }

    public EstadoPaquete getEstado() {
        return estado;
    }

    public void setEstado(EstadoPaquete estado) {
        this.estado = estado;
    }

    public String getCentroActualId() {
        return centroActualId;
    }

    public void setCentroActualId(String centroActualId) {
        this.centroActualId = centroActualId;
    }
}
