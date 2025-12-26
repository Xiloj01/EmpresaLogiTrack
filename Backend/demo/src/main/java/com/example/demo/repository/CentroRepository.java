package com.example.demo.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.demo.model.Centro;

@Repository
public class CentroRepository {
    private Map<String, Centro> centros;

    public CentroRepository() {
        this.centros = new HashMap<>();
    }

    // Guardar un centro
    public Centro save(Centro centro) {
        centros.put(centro.getId(), centro);
        return centro;
    }

    // Buscar por ID
    public Optional<Centro> findById(String id) {
        return Optional.ofNullable(centros.get(id));
    }

    // Buscar todos
    public List<Centro> findAll() {
        return new ArrayList<>(centros.values());
    }

    // Verificar si existe
    public boolean existsById(String id) {
        return centros.containsKey(id);
    }

    // Eliminar
    public void deleteById(String id) {
        centros.remove(id);
    }

    // Limpiar todo (útil para carga inicial)
    public void deleteAll() {
        centros.clear();
    }

    // Contar
    public int count() {
        return centros.size();
    }
}