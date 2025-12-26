package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.service.XMLService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Permitir CORS para React
public class ImportarController {

    @Autowired
    private XMLService xmlService;

    @PostMapping("/importar")
    public ResponseEntity<Map<String, Object>> importarXML(
            @RequestParam("archivo") MultipartFile archivo) {
        
        Map<String, Object> respuesta = new HashMap<>();

        try {
            // Validar que el archivo no esté vacío
            if (archivo.isEmpty()) {
                respuesta.put("exito", false);
                respuesta.put("mensaje", "El archivo está vacío");
                return ResponseEntity.badRequest().body(respuesta);
            }

            // Validar extensión XML
            String nombreArchivo = archivo.getOriginalFilename();
            if (nombreArchivo == null || !nombreArchivo.toLowerCase().endsWith(".xml")) {
                respuesta.put("exito", false);
                respuesta.put("mensaje", "El archivo debe ser XML");
                return ResponseEntity.badRequest().body(respuesta);
            }

            // Procesar el XML
            Map<String, Object> resultado = xmlService.procesarXML(archivo.getInputStream());
            
            if ((Boolean) resultado.get("exito")) {
                return ResponseEntity.ok(resultado);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultado);
            }

        } catch (Exception e) {
            respuesta.put("exito", false);
            respuesta.put("mensaje", "Error al procesar el archivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }

    @GetMapping("/estado")
    public ResponseEntity<Map<String, Object>> obtenerEstado() {
        Map<String, Object> estado = new HashMap<>();
        estado.put("mensaje", "Sistema LogiTrack activo");
        estado.put("version", "1.0");
        return ResponseEntity.ok(estado);
    }
}