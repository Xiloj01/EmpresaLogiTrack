package com.example.demo.controller;

import com.example.demo.model.Centro;
import com.example.demo.model.Mensajero;
import com.example.demo.repository.CentroRepository;
import com.example.demo.repository.MensajeroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/mensajeros")
@CrossOrigin(origins = "*")

public class MensajeroController {
    
    @Autowired
    private MensajeroRepository mensajeroRepository;

    @Autowired
    private CentroRepository centroRepository;

    @GetMapping
    public ResponseEntity<List<Mensajero>> listarMensajeros() {
        List<Mensajero> mensajeros = mensajeroRepository.findAll();
        return ResponseEntity.ok(mensajeros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerMensajero(@PathVariable String id) {
        Optional<Mensajero> mensajeroOpt = mensajeroRepository.findById(id);
        
        if (mensajeroOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Mensajero no encontrado"));
        }

        return ResponseEntity.ok(mensajeroOpt.get());
    }

    @PostMapping
    public ResponseEntity<?> crearMensajero(@RequestBody Mensajero mensajero) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (mensajeroRepository.existsById(mensajero.getId())) {
                response.put("error", "Ya existe un mensajero con ese ID");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            if (!centroRepository.existsById(mensajero.getCentroId())) {
                response.put("error", "Centro no existe");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (mensajero.getCapacidad() <= 0) {
                response.put("error", "La capacidad debe ser mayor a 0");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Mensajero mensajeroCreado = mensajeroRepository.save(mensajero);

            Optional<Centro> centroOpt = centroRepository.findById(mensajero.getCentroId());
            if (centroOpt.isPresent()) {
                Centro centro = centroOpt.get();
                centro.agregarMensajero(mensajeroCreado);
            }

            response.put("mensaje", "Mensajero creado exitosamente");
            response.put("mensajero", mensajeroCreado);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("error", "Error al crear mensajero: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable String id, @RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();

        Optional<Mensajero> mensajeroOpt = mensajeroRepository.findById(id);
        
        if (mensajeroOpt.isEmpty()) {
            response.put("error", "Mensajero no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        try {
            Mensajero mensajero = mensajeroOpt.get();
            String nuevoEstado = body.get("estado");

            if (nuevoEstado == null) {
                response.put("error", "Estado no proporcionado");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Mensajero.EstadoMensajero estado = Mensajero.EstadoMensajero.valueOf(nuevoEstado);
            mensajero.setEstado(estado);
            mensajeroRepository.save(mensajero);

            response.put("mensaje", "Estado actualizado exitosamente");
            response.put("mensajero", mensajero);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("error", "Estado inválido. Use: DISPONIBLE o EN_TRANSITO");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("error", "Error al cambiar estado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}/centro")
    public ResponseEntity<?> reasignarCentro(@PathVariable String id, @RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();

        Optional<Mensajero> mensajeroOpt = mensajeroRepository.findById(id);
        
        if (mensajeroOpt.isEmpty()) {
            response.put("error", "Mensajero no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        try {
            Mensajero mensajero = mensajeroOpt.get();
            String nuevoCentroId = body.get("centroId");

            if (nuevoCentroId == null) {
                response.put("error", "ID del centro no proporcionado");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (mensajero.getEstado() == Mensajero.EstadoMensajero.EN_TRANSITO) {
                response.put("error", "No se puede reasignar un mensajero en tránsito");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Optional<Centro> nuevoCentroOpt = centroRepository.findById(nuevoCentroId);
            if (nuevoCentroOpt.isEmpty()) {
                response.put("error", "Centro destino no existe");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Optional<Centro> centroAnteriorOpt = centroRepository.findById(mensajero.getCentroId());
            if (centroAnteriorOpt.isPresent()) {
                centroAnteriorOpt.get().removerMensajero(mensajero);
            }

            mensajero.setCentroId(nuevoCentroId);
            mensajeroRepository.save(mensajero);

            nuevoCentroOpt.get().agregarMensajero(mensajero);

            response.put("mensaje", "Centro reasignado exitosamente");
            response.put("mensajero", mensajero);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Error al reasignar centro: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
