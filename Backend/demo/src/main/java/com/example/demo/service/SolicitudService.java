package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SolicitudService {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private PaqueteRepository paqueteRepository;

    @Autowired
    private MensajeroRepository mensajeroRepository;

    @Autowired
    private CentroRepository centroRepository;

    @Autowired
    private RutaRepository rutaRepository;

    public Map<String, Object> procesarSiguienteSolicitud() {
        Map<String, Object> resultado = new HashMap<>();

        // Verificar si hay solicitudes pendientes
        if (!solicitudRepository.hayPendientes()) {
            resultado.put("exito", false);
            resultado.put("mensaje", "No hay solicitudes pendientes");
            return resultado;
        }

        // Obtener la solicitud de mayor prioridad
        Solicitud solicitud = solicitudRepository.peek();
        
        if (solicitud == null) {
            resultado.put("exito", false);
            resultado.put("mensaje", "No hay solicitudes disponibles");
            return resultado;
        }

        // Procesar la solicitud
        return procesarSolicitudIndividual(solicitud);
    }

    public List<Map<String, Object>> procesarVariasSolicitudes(int n) {
        List<Map<String, Object>> resultados = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (!solicitudRepository.hayPendientes()) {
                break;
            }

            Map<String, Object> resultado = procesarSiguienteSolicitud();
            resultados.add(resultado);
        }

        return resultados;
    }

    private Map<String, Object> procesarSolicitudIndividual(Solicitud solicitud) {
        Map<String, Object> resultado = new HashMap<>();

        try {
            // Validar que el paquete exista
            Optional<Paquete> paqueteOpt = paqueteRepository.findById(solicitud.getPaqueteId());
            if (paqueteOpt.isEmpty()) {
                solicitudRepository.poll(); // Remover de la cola
                solicitud.marcarRechazada();
                resultado.put("exito", false);
                resultado.put("solicitudId", solicitud.getId());
                resultado.put("motivo", "Paquete no encontrado");
                return resultado;
            }

            Paquete paquete = paqueteOpt.get();

            // Validar que el paquete esté PENDIENTE
            if (!paquete.puedeSerEnviado()) {
                solicitudRepository.poll();
                solicitud.marcarRechazada();
                resultado.put("exito", false);
                resultado.put("solicitudId", solicitud.getId());
                resultado.put("motivo", "Paquete no está en estado PENDIENTE");
                return resultado;
            }

            // Obtener centro origen (donde está el paquete)
            Optional<Centro> centroOrigenOpt = centroRepository.findById(paquete.getCentroActualId());
            if (centroOrigenOpt.isEmpty()) {
                solicitudRepository.poll();
                solicitud.marcarRechazada();
                resultado.put("exito", false);
                resultado.put("solicitudId", solicitud.getId());
                resultado.put("motivo", "Centro origen no encontrado");
                return resultado;
            }

            Centro centroOrigen = centroOrigenOpt.get();

            // Validar que exista ruta directa
            if (!rutaRepository.existeRutaDirecta(paquete.getCentroActualId(), paquete.getDestinoId())) {
                solicitudRepository.poll();
                solicitud.marcarRechazada();
                resultado.put("exito", false);
                resultado.put("solicitudId", solicitud.getId());
                resultado.put("motivo", "No existe ruta directa entre origen y destino");
                return resultado;
            }

            // Buscar mensajero disponible en el centro origen
            List<Mensajero> mensajerosDisponibles = mensajeroRepository.findDisponiblesByCentroId(paquete.getCentroActualId());
            
            if (mensajerosDisponibles.isEmpty()) {
                solicitudRepository.poll();
                solicitud.marcarRechazada();
                resultado.put("exito", false);
                resultado.put("solicitudId", solicitud.getId());
                resultado.put("motivo", "No hay mensajeros disponibles en el centro");
                return resultado;
            }

            // Tomar el primer mensajero disponible
            Mensajero mensajero = mensajerosDisponibles.get(0);

            // Validar capacidad del mensajero
            if (!mensajero.puedeCargarPaquete()) {
                solicitudRepository.poll();
                solicitud.marcarRechazada();
                resultado.put("exito", false);
                resultado.put("solicitudId", solicitud.getId());
                resultado.put("motivo", "Mensajero sin capacidad disponible");
                return resultado;
            }

            // Buscar paquetes adicionales con la misma ruta
            List<Paquete> paquetesAdicionales = buscarPaquetesConMismaRuta(
                paquete.getCentroActualId(), 
                paquete.getDestinoId(),
                mensajero.getCapacidadDisponible() - 1 // Ya contamos el paquete actual
            );

            // ===== PROCESAR ENVÍO =====

            // Asignar paquete principal al mensajero
            mensajero.asignarPaquete(paquete);
            paquete.marcarEnTransito();
            centroOrigen.removerPaquete(paquete);
            paqueteRepository.save(paquete);

            // Asignar paquetes adicionales
            for (Paquete paqueteAdicional : paquetesAdicionales) {
                mensajero.asignarPaquete(paqueteAdicional);
                paqueteAdicional.marcarEnTransito();
                centroOrigen.removerPaquete(paqueteAdicional);
                paqueteRepository.save(paqueteAdicional);
            }

            // Cambiar estado del mensajero a EN_TRANSITO
            mensajero.marcarEnTransito();
            mensajeroRepository.save(mensajero);

            // Marcar solicitud como ATENDIDA y remover de la cola
            solicitudRepository.poll();
            solicitud.marcarAtendida();

            // Preparar resultado exitoso
            resultado.put("exito", true);
            resultado.put("solicitudId", solicitud.getId());
            resultado.put("paqueteId", paquete.getId());
            resultado.put("mensajeroId", mensajero.getId());
            resultado.put("paquetesAsignados", 1 + paquetesAdicionales.size());
            resultado.put("origen", paquete.getCentroActualId());
            resultado.put("destino", paquete.getDestinoId());
            resultado.put("mensaje", "Solicitud procesada exitosamente");

            return resultado;

        } catch (Exception e) {
            solicitudRepository.poll();
            if (solicitud != null) {
                solicitud.marcarRechazada();
            }
            resultado.put("exito", false);
            resultado.put("solicitudId", solicitud != null ? solicitud.getId() : "N/A");
            resultado.put("motivo", "Error al procesar: " + e.getMessage());
            return resultado;
        }
    }

    private List<Paquete> buscarPaquetesConMismaRuta(String origenId, String destinoId, int capacidadDisponible) {
        List<Paquete> resultado = new ArrayList<>();

        if (capacidadDisponible <= 0) {
            return resultado;
        }

        List<Paquete> paquetesPendientes = paqueteRepository.findByEstado(Paquete.EstadoPaquete.PENDIENTE);

        for (Paquete paquete : paquetesPendientes) {
            if (resultado.size() >= capacidadDisponible) {
                break;
            }

            if (paquete.getCentroActualId().equals(origenId) && 
                paquete.getDestinoId().equals(destinoId)) {
                resultado.add(paquete);
            }
        }

        return resultado;
    }
}