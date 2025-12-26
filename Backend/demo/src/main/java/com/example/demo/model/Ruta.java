package com.example.demo.model;

public class Ruta {
     private String id;
    private String origenId;
    private String destinoId;
    private double distancia;

    public Ruta() {
    }

    public Ruta(String id, String origenId, String destinoId, double distancia) {
        this.id = id;
        this.origenId = origenId;
        this.destinoId = destinoId;
        this.distancia = distancia;
    }

    public boolean conecta(String origen, String destino) {
        return this.origenId.equals(origen) && this.destinoId.equals(destino);
    }

    public boolean validarDistancia() {
        return distancia > 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrigenId() {
        return origenId;
    }

    public void setOrigenId(String origenId) {
        this.origenId = origenId;
    }

    public String getDestinoId() {
        return destinoId;
    }

    public void setDestinoId(String destinoId) {
        this.destinoId = destinoId;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }
}
