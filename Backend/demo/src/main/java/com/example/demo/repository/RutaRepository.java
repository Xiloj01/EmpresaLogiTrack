package com.example.demo.repository;


import com.example.demo.model.Ruta;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class RutaRepository {
    private Map<String, Ruta> rutas;

    public RutaRepository() {
        this.rutas = new HashMap<>();
    }

    public Ruta save(Ruta ruta) {
        rutas.put(ruta.getId(), ruta);
        return ruta;
    }

    public Optional<Ruta> findById(String id) {
        return Optional.ofNullable(rutas.get(id));
    }

    public List<Ruta> findAll() {
        return new ArrayList<>(rutas.values());
    }

    public boolean existsById(String id) {
        return rutas.containsKey(id);
    }

    public void deleteById(String id) {
        rutas.remove(id);
    }

    public void deleteAll() {
        rutas.clear();
    }

    public int count() {
        return rutas.size();
    }

    // Búsquedas específicas
    public Optional<Ruta> findByOrigenAndDestino(String origenId, String destinoId) {
        for (Ruta ruta : rutas.values()) {
            if (ruta.conecta(origenId, destinoId)) {
                return Optional.of(ruta);
            }
        }
        return Optional.empty();
    }

    public List<Ruta> findByOrigen(String origenId) {
        List<Ruta> resultado = new ArrayList<>();
        for (Ruta ruta : rutas.values()) {
            if (ruta.getOrigenId().equals(origenId)) {
                resultado.add(ruta);
            }
        }
        return resultado;
    }

    public boolean existeRutaDirecta(String origenId, String destinoId) {
        return findByOrigenAndDestino(origenId, destinoId).isPresent();
    }
}