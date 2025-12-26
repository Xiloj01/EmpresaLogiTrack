package com.example.demo.controller;


import com.example.demo.model.Centro;
import com.example.demo.model.Mensajero;
import com.example.demo.model.Paquete;
import com.example.demo.repository.CentroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/centros")
@CrossOrigin(origins = "*")

public class CentroController {
    
    @Autowired
    private CentroRepository centroRepository;

    // GET /api/centros - Lista todos los centros
    @GetMapping
    public ResponseEntity<List<Centro>> listarCentros() {
        List<Centro> centros = centroRepository.findAll();
        return ResponseEntity.ok(centros);
    }

    // GET /api/centros/{id} - Obtener un centro específico
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerCentro(@PathVariable String id) {
        Optional<Centro> centroOpt = centroRepository.findById(id);
        
        if (centroOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Centro no encontrado"));
        }

        Centro centro = centroOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("id", centro.getId());
        response.put("nombre", centro.getNombre());
        response.put("ciudad", centro.getCiudad());
        response.put("capacidad", centro.getCapacidad());
        response.put("cargaActual", centro.getCargaActual());
        response.put("porcentajeUso", centro.getPorcentajeUso());
        response.put("paquetesAlmacenados", centro.getPaquetesAlmacenados().size());
        response.put("mensajerosAsignados", centro.getMensajerosAsignados().size());

        return ResponseEntity.ok(response);
    }

    // GET /api/centros/{id}/paquetes - Lista paquetes del centro
    @GetMapping("/{id}/paquetes")
    public ResponseEntity<?> listarPaquetesCentro(@PathVariable String id) {
        Optional<Centro> centroOpt = centroRepository.findById(id);
        
        if (centroOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Centro no encontrado"));
        }

        Centro centro = centroOpt.get();
        List<Paquete> paquetes = centro.getPaquetesAlmacenados();
        
        return ResponseEntity.ok(paquetes);
    }

    // GET /api/centros/{id}/mensajeros - Lista mensajeros del centro
    @GetMapping("/{id}/mensajeros")
    public ResponseEntity<?> listarMensajerosCentro(@PathVariable String id) {
        Optional<Centro> centroOpt = centroRepository.findById(id);
        
        if (centroOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Centro no encontrado"));
        }

        Centro centro = centroOpt.get();
        List<Mensajero> mensajeros = centro.getMensajerosAsignados();
        
        return ResponseEntity.ok(mensajeros);
    }
}
