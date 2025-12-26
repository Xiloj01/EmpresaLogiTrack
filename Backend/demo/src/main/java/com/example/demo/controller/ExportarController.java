package com.example.demo.controller;

import com.example.demo.service.XMLOutputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/exportar")
@CrossOrigin(origins = "*")
public class ExportarController {

    @Autowired
    private XMLOutputService xmlOutputService;

    // GET /api/exportar - Generar y descargar XML de salida
    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> exportarXML() {
        try {
            String xmlContent = xmlOutputService.generarXMLSalida();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.setContentDispositionFormData("attachment", "resultado_logitrack.xml");

            return new ResponseEntity<>(xmlContent, headers, HttpStatus.OK);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al generar XML: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // GET /api/exportar/preview - Ver el XML sin descargar (para testing)
    @GetMapping("/preview")
    public ResponseEntity<?> previewXML() {
        try {
            String xmlContent = xmlOutputService.generarXMLSalida();
            
            Map<String, Object> response = new HashMap<>();
            response.put("xml", xmlContent);
            response.put("mensaje", "XML generado exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al generar XML: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}