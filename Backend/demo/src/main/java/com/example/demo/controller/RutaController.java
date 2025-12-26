package com.example.demo.controller;

import com.example.demo.model.Ruta;
import com.example.demo.repository.CentroRepository;
import com.example.demo.repository.RutaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/rutas")
@CrossOrigin(origins = "*")

public class RutaController {
    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private CentroRepository centroRepository;

    @GetMapping
    public ResponseEntity<List<Ruta>> listarRutas() {
        List<Ruta> rutas = rutaRepository.findAll();
        return ResponseEntity.ok(rutas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerRuta(@PathVariable String id) {
        Optional<Ruta> rutaOpt = rutaRepository.findById(id);
        
        if (rutaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Ruta no encontrada"));
        }

        return ResponseEntity.ok(rutaOpt.get());
    }

    @PostMapping
    public ResponseEntity<?> crearRuta(@RequestBody Ruta ruta) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (rutaRepository.existsById(ruta.getId())) {
                response.put("error", "Ya existe una ruta con ese ID");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            if (!centroRepository.existsById(ruta.getOrigenId())) {
                response.put("error", "Centro origen no existe");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (!centroRepository.existsById(ruta.getDestinoId())) {
                response.put("error", "Centro destino no existe");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (rutaRepository.existeRutaDirecta(ruta.getOrigenId(), ruta.getDestinoId())) {
                response.put("error", "Ya existe una ruta entre esos centros");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            if (!ruta.validarDistancia()) {
                response.put("error", "La distancia debe ser mayor a 0");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Ruta rutaCreada = rutaRepository.save(ruta);
            response.put("mensaje", "Ruta creada exitosamente");
            response.put("ruta", rutaCreada);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("error", "Error al crear ruta: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarRuta(@PathVariable String id, @RequestBody Ruta rutaActualizada) {
        Map<String, Object> response = new HashMap<>();

        Optional<Ruta> rutaOpt = rutaRepository.findById(id);
        
        if (rutaOpt.isEmpty()) {
            response.put("error", "Ruta no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        try {
            Ruta ruta = rutaOpt.get();
            
            if (rutaActualizada.getDistancia() > 0) {
                ruta.setDistancia(rutaActualizada.getDistancia());
            }

            rutaRepository.save(ruta);
            response.put("mensaje", "Ruta actualizada exitosamente");
            response.put("ruta", ruta);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Error al actualizar ruta: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRuta(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        if (!rutaRepository.existsById(id)) {
            response.put("error", "Ruta no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        try {
            rutaRepository.deleteById(id);
            response.put("mensaje", "Ruta eliminada exitosamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Error al eliminar ruta: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
