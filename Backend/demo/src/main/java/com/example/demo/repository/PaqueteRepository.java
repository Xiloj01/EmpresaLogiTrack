package com.example.demo.repository;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.demo.model.Paquete;

@Repository
public class PaqueteRepository {
    private Map<String, Paquete> paquetes;

    public PaqueteRepository() {
        this.paquetes = new HashMap<>();
    }

    public Paquete save(Paquete paquete) {
        paquetes.put(paquete.getId(), paquete);
        return paquete;
    }

    public Optional<Paquete> findById(String id) {
        return Optional.ofNullable(paquetes.get(id));
    }

    public List<Paquete> findAll() {
        return new ArrayList<>(paquetes.values());
    }

    public boolean existsById(String id) {
        return paquetes.containsKey(id);
    }

    public void deleteById(String id) {
        paquetes.remove(id);
    }

    public void deleteAll() {
        paquetes.clear();
    }

    public int count() {
        return paquetes.size();
    }

    // Búsquedas específicas
    public List<Paquete> findByCentroActualId(String centroId) {
        List<Paquete> resultado = new ArrayList<>();
        for (Paquete paquete : paquetes.values()) {
            if (paquete.getCentroActualId().equals(centroId)) {
                resultado.add(paquete);
            }
        }
        return resultado;
    }

    public List<Paquete> findByEstado(Paquete.EstadoPaquete estado) {
        List<Paquete> resultado = new ArrayList<>();
        for (Paquete paquete : paquetes.values()) {
            if (paquete.getEstado() == estado) {
                resultado.add(paquete);
            }
        }
        return resultado;
    }
}