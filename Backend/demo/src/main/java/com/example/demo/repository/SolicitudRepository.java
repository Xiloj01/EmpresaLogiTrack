package com.example.demo.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;

import org.springframework.stereotype.Repository;

import com.example.demo.model.Solicitud;

@Repository
public class SolicitudRepository {
    private Map<String, Solicitud> solicitudes;
    private PriorityQueue<Solicitud> colaPrioridad;

    public SolicitudRepository() {
        this.solicitudes = new HashMap<>();
        this.colaPrioridad = new PriorityQueue<>();
    }

    public Solicitud save(Solicitud solicitud) {
        solicitudes.put(solicitud.getId(), solicitud);
        if (solicitud.estaPendiente()) {
            colaPrioridad.offer(solicitud);
        }
        return solicitud;
    }

    public Optional<Solicitud> findById(String id) {
        return Optional.ofNullable(solicitudes.get(id));
    }

    public List<Solicitud> findAll() {
        return new ArrayList<>(solicitudes.values());
    }

    // Obtener todas las solicitudes pendientes ordenadas por prioridad
    public List<Solicitud> findAllPendientesOrdenadas() {
        List<Solicitud> pendientes = new ArrayList<>();
        for (Solicitud solicitud : solicitudes.values()) {
            if (solicitud.estaPendiente()) {
                pendientes.add(solicitud);
            }
        }
        pendientes.sort(Solicitud::compareTo);
        return pendientes;
    }

    public boolean existsById(String id) {
        return solicitudes.containsKey(id);
    }

    public void deleteById(String id) {
        Solicitud solicitud = solicitudes.remove(id);
        if (solicitud != null) {
            colaPrioridad.remove(solicitud);
        }
    }

    public void deleteAll() {
        solicitudes.clear();
        colaPrioridad.clear();
    }

    public int count() {
        return solicitudes.size();
    }

    // Métodos específicos para cola de prioridad
    public Solicitud poll() {
        Solicitud solicitud = colaPrioridad.poll();
        return solicitud;
    }

    public Solicitud peek() {
        return colaPrioridad.peek();
    }

    public boolean hayPendientes() {
        return !colaPrioridad.isEmpty();
    }

    public int countPendientes() {
        return colaPrioridad.size();
    }

    // Actualizar el estado de una solicitud y reorganizar la cola
    public void actualizarEstado(String id, Solicitud.EstadoSolicitud nuevoEstado) {
        Solicitud solicitud = solicitudes.get(id);
        if (solicitud != null) {
            Solicitud.EstadoSolicitud estadoAnterior = solicitud.getEstado();
            solicitud.setEstado(nuevoEstado);
            
            // Si cambia de PENDIENTE a otro estado, remover de la cola
            if (estadoAnterior == Solicitud.EstadoSolicitud.PENDIENTE && 
                nuevoEstado != Solicitud.EstadoSolicitud.PENDIENTE) {
                colaPrioridad.remove(solicitud);
            }
            
            // Si cambia a PENDIENTE, agregar a la cola
            if (estadoAnterior != Solicitud.EstadoSolicitud.PENDIENTE && 
                nuevoEstado == Solicitud.EstadoSolicitud.PENDIENTE) {
                colaPrioridad.offer(solicitud);
            }
        }
    }

    public int countAtendidas() {
        int count = 0;
        for (Solicitud solicitud : solicitudes.values()) {
            if (solicitud.getEstado() == Solicitud.EstadoSolicitud.ATENDIDA) {
                count++;
            }
        }
        return count;
    }
}