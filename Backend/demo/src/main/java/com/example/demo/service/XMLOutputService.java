package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import java.io.StringWriter;
import java.util.List;

@Service
public class XMLOutputService {

    @Autowired
    private CentroRepository centroRepository;

    @Autowired
    private MensajeroRepository mensajeroRepository;

    @Autowired
    private PaqueteRepository paqueteRepository;

    @Autowired
    private SolicitudRepository solicitudRepository;

    public String generarXMLSalida() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        // Elemento raíz
        Element root = doc.createElement("resultadoLogitrack");
        doc.appendChild(root);

        // Estadísticas
        Element estadisticas = doc.createElement("estadisticas");
        root.appendChild(estadisticas);

        int paquetesProcesados = contarPaquetesProcesados();
        int solicitudesAtendidas = solicitudRepository.countAtendidas();
        int mensajerosActivos = mensajeroRepository.countActivos();

        crearElementoConTexto(doc, estadisticas, "paquetesProcesados", String.valueOf(paquetesProcesados));
        crearElementoConTexto(doc, estadisticas, "solicitudesAtendidas", String.valueOf(solicitudesAtendidas));
        crearElementoConTexto(doc, estadisticas, "mensajerosActivos", String.valueOf(mensajerosActivos));

        // Centros
        Element centros = doc.createElement("centros");
        root.appendChild(centros);

        List<Centro> listaCentros = centroRepository.findAll();
        for (Centro centro : listaCentros) {
            Element centroElement = doc.createElement("centro");
            centroElement.setAttribute("id", centro.getId());

            int mensajerosDisponibles = contarMensajerosDisponibles(centro.getId());
            
            crearElementoConTexto(doc, centroElement, "paquetesActuales", String.valueOf(centro.getCargaActual()));
            crearElementoConTexto(doc, centroElement, "mensajerosDisponibles", String.valueOf(mensajerosDisponibles));

            centros.appendChild(centroElement);
        }

        // Mensajeros
        Element mensajeros = doc.createElement("mensajeros");
        root.appendChild(mensajeros);

        List<Mensajero> listaMensajeros = mensajeroRepository.findAll();
        for (Mensajero mensajero : listaMensajeros) {
            Element mensajeroElement = doc.createElement("mensajero");
            mensajeroElement.setAttribute("id", mensajero.getId());
            mensajeroElement.setAttribute("estado", mensajero.getEstado().toString());
            mensajeros.appendChild(mensajeroElement);
        }

        // Paquetes
        Element paquetes = doc.createElement("paquetes");
        root.appendChild(paquetes);

        List<Paquete> listaPaquetes = paqueteRepository.findAll();
        for (Paquete paquete : listaPaquetes) {
            Element paqueteElement = doc.createElement("paquete");
            paqueteElement.setAttribute("id", paquete.getId());
            paqueteElement.setAttribute("estado", paquete.getEstado().toString());
            paqueteElement.setAttribute("centroActual", paquete.getCentroActualId());
            paquetes.appendChild(paqueteElement);
        }

        // Solicitudes
        Element solicitudes = doc.createElement("solicitudes");
        root.appendChild(solicitudes);

        List<Solicitud> listaSolicitudes = solicitudRepository.findAll();
        for (Solicitud solicitud : listaSolicitudes) {
            Element solicitudElement = doc.createElement("solicitud");
            solicitudElement.setAttribute("id", solicitud.getId());
            solicitudElement.setAttribute("estado", solicitud.getEstado().toString());
            solicitudElement.setAttribute("paquete", solicitud.getPaqueteId());
            solicitudes.appendChild(solicitudElement);
        }

        // Convertir Document a String
        return documentToString(doc);
    }

    private void crearElementoConTexto(Document doc, Element padre, String nombre, String texto) {
        Element elemento = doc.createElement(nombre);
        elemento.setTextContent(texto);
        padre.appendChild(elemento);
    }

    private int contarPaquetesProcesados() {
        int count = 0;
        List<Paquete> paquetes = paqueteRepository.findAll();
        for (Paquete paquete : paquetes) {
            if (paquete.getEstado() == Paquete.EstadoPaquete.ENTREGADO || 
                paquete.getEstado() == Paquete.EstadoPaquete.EN_TRANSITO) {
                count++;
            }
        }
        return count;
    }

    private int contarMensajerosDisponibles(String centroId) {
        List<Mensajero> mensajeros = mensajeroRepository.findDisponiblesByCentroId(centroId);
        return mensajeros.size();
    }

    private String documentToString(Document doc) throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "no");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.getBuffer().toString();
    }
}