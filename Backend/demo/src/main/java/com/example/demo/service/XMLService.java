package com.example.demo.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.example.demo.model.Centro;
import com.example.demo.model.Mensajero;
import com.example.demo.model.Paquete;
import com.example.demo.model.Ruta;
import com.example.demo.model.Solicitud;
import com.example.demo.repository.CentroRepository;
import com.example.demo.repository.SolicitudRepository;

@Service
public class XMLService {

    @Autowired
    private CentroRepository centroRepository;
    
    @Autowired
    private RutaRepository rutaRepository;
    
    @Autowired
    private MensajeroRepository mensajeroRepository;
    
    @Autowired
    private PaqueteRepository paqueteRepository;
    
    @Autowired
    private SolicitudRepository solicitudRepository;

    public Map<String, Object> procesarXML(InputStream xmlInputStream) {
        Map<String, Object> resultado = new HashMap<>();
        List<String> errores = new ArrayList<>();
        
        try {
            // Limpiar datos anteriores
            centroRepository.deleteAll();
            rutaRepository.deleteAll();
            mensajeroRepository.deleteAll();
            paqueteRepository.deleteAll();
            solicitudRepository.deleteAll();

            // Parsear XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlInputStream);
            doc.getDocumentElement().normalize();

            // Procesar cada sección
            procesarCentros(doc, errores);
            procesarRutas(doc, errores);
            procesarMensajeros(doc, errores);
            procesarPaquetes(doc, errores);
            procesarSolicitudes(doc, errores);

            // Preparar resultado
            resultado.put("centrosCreados", centroRepository.count());
            resultado.put("rutasCreadas", rutaRepository.count());
            resultado.put("mensajerosCreados", mensajeroRepository.count());
            resultado.put("paquetesCreados", paqueteRepository.count());
            resultado.put("solicitudesCreadas", solicitudRepository.count());
            resultado.put("errores", errores);
            resultado.put("exito", true);

        } catch (Exception e) {
            errores.add("Error general al procesar XML: " + e.getMessage());
            resultado.put("exito", false);
            resultado.put("errores", errores);
        }

        return resultado;
    }

    private void procesarCentros(Document doc, List<String> errores) {
        NodeList centrosList = doc.getElementsByTagName("centro");
        for (int i = 0; i < centrosList.getLength(); i++) {
            try {
                Element centroElement = (Element) centrosList.item(i);
                
                String id = centroElement.getAttribute("id");
                String nombre = getElementValue(centroElement, "nombre");
                String ciudad = getElementValue(centroElement, "ciudad");
                int capacidad = Integer.parseInt(getElementValue(centroElement, "capacidad"));

                if (centroRepository.existsById(id)) {
                    errores.add("Centro duplicado: " + id);
                    continue;
                }

                Centro centro = new Centro(id, nombre, ciudad, capacidad);
                centroRepository.save(centro);

            } catch (Exception e) {
                errores.add("Error procesando centro: " + e.getMessage());
            }
        }
    }

    private void procesarRutas(Document doc, List<String> errores) {
        NodeList rutasList = doc.getElementsByTagName("ruta");
        for (int i = 0; i < rutasList.getLength(); i++) {
            try {
                Element rutaElement = (Element) rutasList.item(i);
                
                String id = rutaElement.getAttribute("id");
                String origen = rutaElement.getAttribute("origen");
                String destino = rutaElement.getAttribute("destino");
                double distancia = Double.parseDouble(rutaElement.getAttribute("distancia"));

                if (rutaRepository.existsById(id)) {
                    errores.add("Ruta duplicada: " + id);
                    continue;
                }

                if (!centroRepository.existsById(origen)) {
                    errores.add("Centro origen no existe: " + origen);
                    continue;
                }

                if (!centroRepository.existsById(destino)) {
                    errores.add("Centro destino no existe: " + destino);
                    continue;
                }

                Ruta ruta = new Ruta(id, origen, destino, distancia);
                rutaRepository.save(ruta);

            } catch (Exception e) {
                errores.add("Error procesando ruta: " + e.getMessage());
            }
        }
    }

    private void procesarMensajeros(Document doc, List<String> errores) {
        NodeList mensajerosList = doc.getElementsByTagName("mensajero");
        for (int i = 0; i < mensajerosList.getLength(); i++) {
            try {
                Element mensajeroElement = (Element) mensajerosList.item(i);
                
                String id = mensajeroElement.getAttribute("id");
                String nombre = mensajeroElement.getAttribute("nombre");
                int capacidad = Integer.parseInt(mensajeroElement.getAttribute("capacidad"));
                String centroId = mensajeroElement.getAttribute("centro");

                if (mensajeroRepository.existsById(id)) {
                    errores.add("Mensajero duplicado: " + id);
                    continue;
                }

                if (!centroRepository.existsById(centroId)) {
                    errores.add("Centro no existe para mensajero: " + centroId);
                    continue;
                }

                Mensajero mensajero = new Mensajero(id, nombre, capacidad, centroId);
                mensajeroRepository.save(mensajero);

                // Agregar mensajero al centro
                centroRepository.findById(centroId).ifPresent(centro -> {
                    centro.agregarMensajero(mensajero);
                });

            } catch (Exception e) {
                errores.add("Error procesando mensajero: " + e.getMessage());
            }
        }
    }

    private void procesarPaquetes(Document doc, List<String> errores) {
        NodeList paquetesList = doc.getElementsByTagName("paquete");
        for (int i = 0; i < paquetesList.getLength(); i++) {
            try {
                Element paqueteElement = (Element) paquetesList.item(i);
                
                String id = paqueteElement.getAttribute("id");
                String cliente = paqueteElement.getAttribute("cliente");
                double peso = Double.parseDouble(paqueteElement.getAttribute("peso"));
                String destino = paqueteElement.getAttribute("destino");
                String estado = paqueteElement.getAttribute("estado");
                String centroActual = paqueteElement.getAttribute("centroActual");

                if (paqueteRepository.existsById(id)) {
                    errores.add("Paquete duplicado: " + id);
                    continue;
                }

                Paquete paquete = new Paquete(id, cliente, peso, destino, centroActual);
                paquete.setEstado(Paquete.EstadoPaquete.valueOf(estado));
                paqueteRepository.save(paquete);

                // Agregar paquete al centro
                if (paquete.getEstado() == Paquete.EstadoPaquete.PENDIENTE) {
                    centroRepository.findById(centroActual).ifPresent(centro -> {
                        try {
                            centro.agregarPaquete(paquete);
                        } catch (Exception e) {
                            errores.add("Centro sin capacidad: " + centroActual);
                        }
                    });
                }

            } catch (Exception e) {
                errores.add("Error procesando paquete: " + e.getMessage());
            }
        }
    }

    private void procesarSolicitudes(Document doc, List<String> errores) {
        NodeList solicitudesList = doc.getElementsByTagName("solicitud");
        for (int i = 0; i < solicitudesList.getLength(); i++) {
            try {
                Element solicitudElement = (Element) solicitudesList.item(i);
                
                String id = solicitudElement.getAttribute("id");
                String tipo = solicitudElement.getAttribute("tipo");
                String paqueteId = solicitudElement.getAttribute("paquete");
                int prioridad = Integer.parseInt(solicitudElement.getAttribute("prioridad"));

                if (solicitudRepository.existsById(id)) {
                    errores.add("Solicitud duplicada: " + id);
                    continue;
                }

                if (!paqueteRepository.existsById(paqueteId)) {
                    errores.add("Paquete no existe para solicitud: " + paqueteId);
                    continue;
                }

                Solicitud solicitud = new Solicitud(id, tipo, paqueteId, prioridad);
                solicitudRepository.save(solicitud);

            } catch (Exception e) {
                errores.add("Error procesando solicitud: " + e.getMessage());
            }
        }
    }

    private String getElementValue(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }
}