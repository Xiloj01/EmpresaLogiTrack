package com.example.demo.repository;


import com.example.demo.model.Mensajero;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MensajeroRepository {
    private Map<String, Mensajero> mensajeros;

    public MensajeroRepository() {
        this.mensajeros = new HashMap<>();
    }

    public Mensajero save(Mensajero mensajero) {
        mensajeros.put(mensajero.getId(), mensajero);
        return mensajero;
    }

    public Optional<Mensajero> findById(String id) {
        return Optional.ofNullable(mensajeros.get(id));
    }

    public List<Mensajero> findAll() {
        return new ArrayList<>(mensajeros.values());
    }

    public boolean existsById(String id) {
        return mensajeros.containsKey(id);
    }

    public void deleteById(String id) {
        mensajeros.remove(id);
    }

    public void deleteAll() {
        mensajeros.clear();
    }

    public int count() {
        return mensajeros.size();
    }

    // Búsquedas específicas
    public List<Mensajero> findByCentroId(String centroId) {
        List<Mensajero> resultado = new ArrayList<>();
        for (Mensajero mensajero : mensajeros.values()) {
            if (mensajero.getCentroId().equals(centroId)) {
                resultado.add(mensajero);
            }
        }
        return resultado;
    }

    public List<Mensajero> findDisponiblesByCentroId(String centroId) {
        List<Mensajero> resultado = new ArrayList<>();
        for (Mensajero mensajero : mensajeros.values()) {
            if (mensajero.getCentroId().equals(centroId) && 
                mensajero.getEstado() == Mensajero.EstadoMensajero.DISPONIBLE) {
                resultado.add(mensajero);
            }
        }
        return resultado;
    }

    public int countActivos() {
        int count = 0;
        for (Mensajero mensajero : mensajeros.values()) {
            if (mensajero.getEstado() != null) {
                count++;
            }
        }
        return count;
    }
}
