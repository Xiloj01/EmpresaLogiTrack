package com.example.demo.controller;

import com.example.demo.model.Mensajero;
import com.example.demo.model.Paquete;
import com.example.demo.repository.MensajeroRepository;
import com.example.demo.repository.PaqueteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/envios")
@CrossOrigin(origins = "*")

public class EnvioController {

    @Autowired
    private PaqueteRepository paqueteRepository;

    @Autowired
    private MensajeroRepository mensajeroRepository;

    // PUT /api/envios/asignar - Asignación directa de mensajero
    @PutMapping("/asignar")
    public ResponseEntity<?> asignarMensajero(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();

        try {
            String paqueteId = body.get("paqueteId");
            String mensajeroId = body.get("mensajeroId");

            if (paqueteId == null || mensajeroId == null) {
                response.put("error", "Se requieren paqueteId y mensajeroId");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Validar que exista el paquete
            Optional<Paquete> paqueteOpt = paqueteRepository.findById(paqueteId);
            if (paqueteOpt.isEmpty()) {
                response.put("error", "Paquete no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Validar que exista el mensajero
            Optional<Mensajero> mensajeroOpt = mensajeroRepository.findById(mensajeroId);
            if (mensajeroOpt.isEmpty()) {
                response.put("error", "Mensajero no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Paquete paquete = paqueteOpt.get();
            Mensajero mensajero = mensajeroOpt.get();

            // Validar que estén en el mismo centro
            if (!paquete.getCentroActualId().equals(mensajero.getCentroId())) {
                response.put("error", "Paquete y mensajero deben estar en el mismo centro");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Validar que el mensajero esté disponible
            if (!mensajero.estaDisponible()) {
                response.put("error", "Mensajero no está disponible");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Validar que el paquete esté pendiente
            if (!paquete.puedeSerEnviado()) {
                response.put("error", "Paquete no está en estado PENDIENTE");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Validar capacidad del mensajero
            if (!mensajero.puedeCargarPaquete()) {
                response.put("error", "Mensajero sin capacidad disponible");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Realizar asignación
            mensajero.asignarPaquete(paquete);
            paquete.marcarEnTransito();
            mensajero.marcarEnTransito();

            paqueteRepository.save(paquete);
            mensajeroRepository.save(mensajero);

            response.put("mensaje", "Asignación exitosa");
            response.put("paqueteId", paquete.getId());
            response.put("mensajeroId", mensajero.getId());
            response.put("estado", "EN_TRANSITO");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Error al asignar: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // PUT /api/envios/{id}/estado - Actualizar estado del envío
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable String id, @RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Paquete> paqueteOpt = paqueteRepository.findById(id);
            
            if (paqueteOpt.isEmpty()) {
                response.put("error", "Paquete no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Paquete paquete = paqueteOpt.get();
            String nuevoEstado = body.get("estado");

            if (nuevoEstado == null) {
                response.put("error", "Estado no proporcionado");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Paquete.EstadoPaquete estadoEnum = Paquete.EstadoPaquete.valueOf(nuevoEstado);

            // Validar transiciones
            if (paquete.getEstado() == Paquete.EstadoPaquete.PENDIENTE && 
                estadoEnum == Paquete.EstadoPaquete.EN_TRANSITO) {
                paquete.marcarEnTransito();
            } else if (paquete.getEstado() == Paquete.EstadoPaquete.EN_TRANSITO && 
                       estadoEnum == Paquete.EstadoPaquete.ENTREGADO) {
                paquete.marcarEntregado();
                
                // Actualizar centro actual al destino
                paquete.setCentroActualId(paquete.getDestinoId());
                
                // Liberar mensajero asociado
                liberarMensajeroDelPaquete(paquete);
            } else {
                response.put("error", "Transición de estado no válida");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            paqueteRepository.save(paquete);

            response.put("mensaje", "Estado actualizado exitosamente");
            response.put("paqueteId", paquete.getId());
            response.put("estadoAnterior", paquete.getEstado());
            response.put("estadoNuevo", estadoEnum);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("error", "Estado inválido. Use: PENDIENTE, EN_TRANSITO, ENTREGADO");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IllegalStateException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("error", "Error al actualizar estado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private void liberarMensajeroDelPaquete(Paquete paquete) {
        // Buscar mensajeros en tránsito que tengan este paquete
        List<Mensajero> mensajeros = mensajeroRepository.findAll();
        
        for (Mensajero mensajero : mensajeros) {
            if (mensajero.getPaquetesAsignados().contains(paquete)) {
                mensajero.removerPaquete(paquete);
                
                // Si no tiene más paquetes, marcar como disponible
                if (mensajero.getPaquetesAsignados().isEmpty()) {
                    mensajero.marcarDisponible();
                    mensajeroRepository.save(mensajero);
                }
                break;
            }
        }
    }
}