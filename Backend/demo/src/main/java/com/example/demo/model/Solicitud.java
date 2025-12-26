package com.example.demo.model;

public class Solicitud implements Comparable<Solicitud> {
    private String id;
    private String tipo;
    private String paqueteId;
    private int prioridad;
    private EstadoSolicitud estado;

    // Enum para estados
    public enum EstadoSolicitud {
        PENDIENTE, ATENDIDA, RECHAZADA
    }

    // Constructor vacío
    public Solicitud() {
        this.estado = EstadoSolicitud.PENDIENTE;
    }

    // Constructor con parámetros
    public Solicitud(String id, String tipo, String paqueteId, int prioridad) {
        this.id = id;
        this.tipo = tipo;
        this.paqueteId = paqueteId;
        this.prioridad = prioridad;
        this.estado = EstadoSolicitud.PENDIENTE;
    }

    // Métodos de negocio
    public boolean estaPendiente() {
        return estado == EstadoSolicitud.PENDIENTE;
    }

    public void marcarAtendida() {
        this.estado = EstadoSolicitud.ATENDIDA;
    }

    public void marcarRechazada() {
        this.estado = EstadoSolicitud.RECHAZADA;
    }

    // Implementación de Comparable para cola de prioridad
    // Mayor prioridad = se procesa primero
    @Override
    public int compareTo(Solicitud otra) {
        // Orden descendente: prioridades más altas primero
        return Integer.compare(otra.prioridad, this.prioridad);
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPaqueteId() {
        return paqueteId;
    }

    public void setPaqueteId(String paqueteId) {
        this.paqueteId = paqueteId;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Solicitud{" +
                "id='" + id + '\'' +
                ", tipo='" + tipo + '\'' +
                ", paqueteId='" + paqueteId + '\'' +
                ", prioridad=" + prioridad +
                ", estado=" + estado +
                '}';
    }
}