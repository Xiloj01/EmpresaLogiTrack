package com.example.demo.controller;

import com.example.demo.model.Solicitud;
import com.example.demo.repository.PaqueteRepository;
import com.example.demo.repository.SolicitudRepository;
import com.example.demo.service.SolicitudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/solicitudes")
@CrossOrigin(origins = "*")

public class SolicitudController {
    
    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private PaqueteRepository paqueteRepository;

    @Autowired
    private SolicitudService solicitudService;

    // GET /api/solicitudes - Lista la cola ordenada
    @GetMapping
    public ResponseEntity<List<Solicitud>> listarSolicitudes() {
        List<Solicitud> solicitudes = solicitudRepository.findAllPendientesOrdenadas();
        return ResponseEntity.ok(solicitudes);
    }

    // POST /api/solicitudes - Crear solicitud
    @PostMapping
    public ResponseEntity<?> crearSolicitud(@RequestBody Solicitud solicitud) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Validar que no exista
            if (solicitudRepository.existsById(solicitud.getId())) {
                response.put("error", "Ya existe una solicitud con ese ID");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            // Validar que el paquete exista
            if (!paqueteRepository.existsById(solicitud.getPaqueteId())) {
                response.put("error", "Paquete no existe");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Validar prioridad
            if (solicitud.getPrioridad() < 1 || solicitud.getPrioridad() > 10) {
                response.put("error", "La prioridad debe estar entre 1 y 10");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Asignar estado PENDIENTE si no tiene
            if (solicitud.getEstado() == null) {
                solicitud.setEstado(Solicitud.EstadoSolicitud.PENDIENTE);
            }

            Solicitud solicitudCreada = solicitudRepository.save(solicitud);

            response.put("mensaje", "Solicitud creada exitosamente");
            response.put("solicitud", solicitudCreada);
            response.put("posicionEnCola", solicitudRepository.countPendientes());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("error", "Error al crear solicitud: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // DELETE /api/solicitudes/{id} - Eliminar solicitud
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarSolicitud(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        if (!solicitudRepository.existsById(id)) {
            response.put("error", "Solicitud no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        try {
            solicitudRepository.deleteById(id);
            response.put("mensaje", "Solicitud eliminada exitosamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Error al eliminar solicitud: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // POST /api/solicitudes/procesar - Procesar la de mayor prioridad
    @PostMapping("/procesar")
    public ResponseEntity<?> procesarSolicitud() {
        Map<String, Object> resultado = solicitudService.procesarSiguienteSolicitud();
        
        if (!(Boolean) resultado.get("exito")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
        }

        return ResponseEntity.ok(resultado);
    }

    // POST /api/solicitudes/procesar/{n} - Procesar las n más prioritarias
    @PostMapping("/procesar/{n}")
    public ResponseEntity<?> procesarVariasSolicitudes(@PathVariable int n) {
        Map<String, Object> response = new HashMap<>();

        if (n <= 0) {
            response.put("error", "El número debe ser mayor a 0");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        List<Map<String, Object>> resultados = solicitudService.procesarVariasSolicitudes(n);

        response.put("totalProcesadas", resultados.size());
        response.put("resultados", resultados);
        return ResponseEntity.ok(response);
    }

    // GET /api/solicitudes/pendientes - Contar pendientes
    @GetMapping("/pendientes")
    public ResponseEntity<Map<String, Object>> contarPendientes() {
        Map<String, Object> response = new HashMap<>();
        response.put("cantidad", solicitudRepository.countPendientes());
        return ResponseEntity.ok(response);
    }
}
