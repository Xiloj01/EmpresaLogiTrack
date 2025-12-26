package com.example.demo.controller;

import com.example.demo.model.Centro;
import com.example.demo.model.Paquete;
import com.example.demo.repository.CentroRepository;
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
@RequestMapping("/api/paquetes")
@CrossOrigin(origins = "*")

public class PaqueteController {
    
    @Autowired
    private PaqueteRepository paqueteRepository;

    @Autowired
    private CentroRepository centroRepository;

    @GetMapping
    public ResponseEntity<List<Paquete>> listarPaquetes() {
        List<Paquete> paquetes = paqueteRepository.findAll();
        return ResponseEntity.ok(paquetes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPaquete(@PathVariable String id) {
        Optional<Paquete> paqueteOpt = paqueteRepository.findById(id);
        
        if (paqueteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Paquete no encontrado"));
        }

        return ResponseEntity.ok(paqueteOpt.get());
    }

    @PostMapping
    public ResponseEntity<?> crearPaquete(@RequestBody Paquete paquete) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (paqueteRepository.existsById(paquete.getId())) {
                response.put("error", "Ya existe un paquete con ese ID");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            if (!paquete.validarPeso()) {
                response.put("error", "El peso debe ser mayor a 0");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (!centroRepository.existsById(paquete.getDestinoId())) {
                response.put("error", "Centro destino no existe");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (!centroRepository.existsById(paquete.getCentroActualId())) {
                response.put("error", "Centro actual no existe");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (paquete.getEstado() == null) {
                paquete.setEstado(Paquete.EstadoPaquete.PENDIENTE);
            }

            Paquete paqueteCreado = paqueteRepository.save(paquete);

            if (paquete.getEstado() == Paquete.EstadoPaquete.PENDIENTE) {
                Optional<Centro> centroOpt = centroRepository.findById(paquete.getCentroActualId());
                if (centroOpt.isPresent()) {
                    try {
                        centroOpt.get().agregarPaquete(paqueteCreado);
                    } catch (IllegalStateException e) {
                        response.put("error", "Centro sin capacidad disponible");
                        paqueteRepository.deleteById(paquete.getId());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }
                }
            }

            response.put("mensaje", "Paquete creado exitosamente");
            response.put("paquete", paqueteCreado);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("error", "Error al crear paquete: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPaquete(@PathVariable String id, @RequestBody Paquete paqueteActualizado) {
        Map<String, Object> response = new HashMap<>();

        Optional<Paquete> paqueteOpt = paqueteRepository.findById(id);
        
        if (paqueteOpt.isEmpty()) {
            response.put("error", "Paquete no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        try {
            Paquete paquete = paqueteOpt.get();

            if (paquete.getEstado() != Paquete.EstadoPaquete.PENDIENTE) {
                response.put("error", "Solo se pueden modificar paquetes PENDIENTES");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (paqueteActualizado.getCliente() != null) {
                paquete.setCliente(paqueteActualizado.getCliente());
            }

            if (paqueteActualizado.getPeso() > 0) {
                paquete.setPeso(paqueteActualizado.getPeso());
            }

            paqueteRepository.save(paquete);
            response.put("mensaje", "Paquete actualizado exitosamente");
            response.put("paquete", paquete);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Error al actualizar paquete: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPaquete(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Paquete> paqueteOpt = paqueteRepository.findById(id);
        
        if (paqueteOpt.isEmpty()) {
            response.put("error", "Paquete no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        try {
            Paquete paquete = paqueteOpt.get();

            if (paquete.getEstado() == Paquete.EstadoPaquete.ENTREGADO || 
                paquete.getEstado() == Paquete.EstadoPaquete.EN_TRANSITO) {
                response.put("error", "No se puede eliminar un paquete en tránsito o entregado");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (paquete.getEstado() == Paquete.EstadoPaquete.PENDIENTE) {
                Optional<Centro> centroOpt = centroRepository.findById(paquete.getCentroActualId());
                if (centroOpt.isPresent()) {
                    centroOpt.get().removerPaquete(paquete);
                }
            }

            paqueteRepository.deleteById(id);
            response.put("mensaje", "Paquete eliminado exitosamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Error al eliminar paquete: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
