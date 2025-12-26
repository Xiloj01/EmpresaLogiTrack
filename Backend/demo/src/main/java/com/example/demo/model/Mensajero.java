package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

public class Mensajero {
    private String id;
    private String nombre;
    private int capacidad;
    private String centroId;
    private EstadoMensajero estado;
    private List<Paquete> paquetesAsignados;

    public enum EstadoMensajero {
        DISPONIBLE, EN_TRANSITO
    }

    public Mensajero() {
        this.estado = EstadoMensajero.DISPONIBLE;
        this.paquetesAsignados = new ArrayList<>();
    }

    public Mensajero(String id, String nombre, int capacidad, String centroId) {
        this.id = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.centroId = centroId;
        this.estado = EstadoMensajero.DISPONIBLE;
        this.paquetesAsignados = new ArrayList<>();
    }

    public boolean estaDisponible() {
        return estado == EstadoMensajero.DISPONIBLE;
    }

    public boolean puedeCargarPaquete() {
        return paquetesAsignados.size() < capacidad && estaDisponible();
    }

    public boolean puedeCargarPaquetes(int cantidad) {
        return (paquetesAsignados.size() + cantidad) <= capacidad && estaDisponible();
    }

    public void asignarPaquete(Paquete paquete) {
        if (!puedeCargarPaquete()) {
            throw new IllegalStateException("Mensajero sin capacidad o no disponible");
        }
        paquetesAsignados.add(paquete);
    }

    public void removerPaquete(Paquete paquete) {
        paquetesAsignados.remove(paquete);
    }

    public void marcarEnTransito() {
        if (estado == EstadoMensajero.EN_TRANSITO) {
            throw new IllegalStateException("Mensajero ya está en tránsito");
        }
        this.estado = EstadoMensajero.EN_TRANSITO;
    }

    public void marcarDisponible() {
        this.estado = EstadoMensajero.DISPONIBLE;
        this.paquetesAsignados.clear();
    }

    public int getCapacidadDisponible() {
        return capacidad - paquetesAsignados.size();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getCentroId() {
        return centroId;
    }

    public void setCentroId(String centroId) {
        this.centroId = centroId;
    }

    public EstadoMensajero getEstado() {
        return estado;
    }

    public void setEstado(EstadoMensajero estado) {
        this.estado = estado;
    }

    public List<Paquete> getPaquetesAsignados() {
        return paquetesAsignados;
    }

    public void setPaquetesAsignados(List<Paquete> paquetesAsignados) {
        this.paquetesAsignados = paquetesAsignados;
    }
}
