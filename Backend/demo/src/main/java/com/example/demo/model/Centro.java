package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

public class Centro {
    private String id;
    private String nombre;
    private String ciudad;
    private int capacidad;
    private List<Paquete> paquetesAlmacenados;
    private List<Mensajero> mensajerosAsignados;

    // Constructor vacío
    public Centro() {
        this.paquetesAlmacenados = new ArrayList<>();
        this.mensajerosAsignados = new ArrayList<>();
    }

    // Constructor con parámetros
    public Centro(String id, String nombre, String ciudad, int capacidad) {
        this.id = id;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.capacidad = capacidad;
        this.paquetesAlmacenados = new ArrayList<>();
        this.mensajerosAsignados = new ArrayList<>();
    }

    // Métodos de negocio
    public boolean puedeAlmacenarPaquete() {
        return paquetesAlmacenados.size() < capacidad;
    }

    public int getCargaActual() {
        return paquetesAlmacenados.size();
    }

    public double getPorcentajeUso() {
        return (getCargaActual() * 100.0) / capacidad;
    }

    public void agregarPaquete(Paquete paquete) {
        if (puedeAlmacenarPaquete()) {
            paquetesAlmacenados.add(paquete);
        } else {
            throw new IllegalStateException("Centro sin capacidad disponible");
        }
    }

    public void removerPaquete(Paquete paquete) {
        paquetesAlmacenados.remove(paquete);
    }

    public void agregarMensajero(Mensajero mensajero) {
        if (!mensajerosAsignados.contains(mensajero)) {
            mensajerosAsignados.add(mensajero);
        }
    }

    public void removerMensajero(Mensajero mensajero) {
        mensajerosAsignados.remove(mensajero);
    }

    // Getters y Setters
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

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public List<Paquete> getPaquetesAlmacenados() {
        return paquetesAlmacenados;
    }

    public void setPaquetesAlmacenados(List<Paquete> paquetesAlmacenados) {
        this.paquetesAlmacenados = paquetesAlmacenados;
    }

    public List<Mensajero> getMensajerosAsignados() {
        return mensajerosAsignados;
    }

    public void setMensajerosAsignados(List<Mensajero> mensajerosAsignados) {
        this.mensajerosAsignados = mensajerosAsignados;
    }

    @Override
    public String toString() {
        return "Centro{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", capacidad=" + capacidad +
                ", cargaActual=" + getCargaActual() +
                '}';
    }
}