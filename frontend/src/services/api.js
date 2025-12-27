// src/services/api.js
// Configuración base para conectar con el backend Spring Boot

const API_BASE_URL = 'http://localhost:8080/api';

class ApiService {
  
  // ==================== SISTEMA ====================
  
  async verificarEstado() {
    const response = await fetch(`${API_BASE_URL}/estado`);
    return await response.json();
  }

  async importarXML(archivo) {
    const formData = new FormData();
    formData.append('archivo', archivo);
    
    const response = await fetch(`${API_BASE_URL}/importar`, {
      method: 'POST',
      body: formData
    });
    return await response.json();
  }

  async exportarXML() {
    const response = await fetch(`${API_BASE_URL}/exportar`);
    const blob = await response.blob();
    
    // Descargar archivo
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'resultado_logitrack.xml';
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
  }

  async previewXML() {
    const response = await fetch(`${API_BASE_URL}/exportar/preview`);
    return await response.json();
  }

  // ==================== CENTROS ====================
  
  async obtenerCentros() {
    const response = await fetch(`${API_BASE_URL}/centros`);
    return await response.json();
  }

  async obtenerCentro(id) {
    const response = await fetch(`${API_BASE_URL}/centros/${id}`);
    return await response.json();
  }

  async obtenerPaquetesCentro(id) {
    const response = await fetch(`${API_BASE_URL}/centros/${id}/paquetes`);
    return await response.json();
  }

  async obtenerMensajerosCentro(id) {
    const response = await fetch(`${API_BASE_URL}/centros/${id}/mensajeros`);
    return await response.json();
  }

  // ==================== RUTAS ====================
  
  async obtenerRutas() {
    const response = await fetch(`${API_BASE_URL}/rutas`);
    return await response.json();
  }

  async obtenerRuta(id) {
    const response = await fetch(`${API_BASE_URL}/rutas/${id}`);
    return await response.json();
  }

  async crearRuta(ruta) {
    const response = await fetch(`${API_BASE_URL}/rutas`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(ruta)
    });
    return await response.json();
  }

  async actualizarRuta(id, ruta) {
    const response = await fetch(`${API_BASE_URL}/rutas/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(ruta)
    });
    return await response.json();
  }

  async eliminarRuta(id) {
    const response = await fetch(`${API_BASE_URL}/rutas/${id}`, {
      method: 'DELETE'
    });
    return await response.json();
  }

  // ==================== MENSAJEROS ====================
  
  async obtenerMensajeros() {
    const response = await fetch(`${API_BASE_URL}/mensajeros`);
    return await response.json();
  }

  async obtenerMensajero(id) {
    const response = await fetch(`${API_BASE_URL}/mensajeros/${id}`);
    return await response.json();
  }

  async crearMensajero(mensajero) {
    const response = await fetch(`${API_BASE_URL}/mensajeros`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(mensajero)
    });
    return await response.json();
  }

  async cambiarEstadoMensajero(id, estado) {
    const response = await fetch(`${API_BASE_URL}/mensajeros/${id}/estado`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ estado })
    });
    return await response.json();
  }

  async reasignarCentroMensajero(id, centroId) {
    const response = await fetch(`${API_BASE_URL}/mensajeros/${id}/centro`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ centroId })
    });
    return await response.json();
  }

  // ==================== PAQUETES ====================
  
  async obtenerPaquetes() {
    const response = await fetch(`${API_BASE_URL}/paquetes`);
    return await response.json();
  }

  async obtenerPaquete(id) {
    const response = await fetch(`${API_BASE_URL}/paquetes/${id}`);
    return await response.json();
  }

  async crearPaquete(paquete) {
    const response = await fetch(`${API_BASE_URL}/paquetes`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(paquete)
    });
    return await response.json();
  }

  async actualizarPaquete(id, paquete) {
    const response = await fetch(`${API_BASE_URL}/paquetes/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(paquete)
    });
    return await response.json();
  }

  async eliminarPaquete(id) {
    const response = await fetch(`${API_BASE_URL}/paquetes/${id}`, {
      method: 'DELETE'
    });
    return await response.json();
  }

  // ==================== SOLICITUDES ====================
  
  async obtenerSolicitudes() {
    const response = await fetch(`${API_BASE_URL}/solicitudes`);
    return await response.json();
  }

  async crearSolicitud(solicitud) {
    const response = await fetch(`${API_BASE_URL}/solicitudes`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(solicitud)
    });
    return await response.json();
  }

  async eliminarSolicitud(id) {
    const response = await fetch(`${API_BASE_URL}/solicitudes/${id}`, {
      method: 'DELETE'
    });
    return await response.json();
  }

  async procesarSiguienteSolicitud() {
    const response = await fetch(`${API_BASE_URL}/solicitudes/procesar`, {
      method: 'POST'
    });
    return await response.json();
  }

  async procesarVariasSolicitudes(n) {
    const response = await fetch(`${API_BASE_URL}/solicitudes/procesar/${n}`, {
      method: 'POST'
    });
    return await response.json();
  }

  async contarSolicitudesPendientes() {
    const response = await fetch(`${API_BASE_URL}/solicitudes/pendientes`);
    return await response.json();
  }

  // ==================== ENVÍOS ====================
  
  async asignarMensajero(paqueteId, mensajeroId) {
    const response = await fetch(`${API_BASE_URL}/envios/asignar`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ paqueteId, mensajeroId })
    });
    return await response.json();
  }

  async actualizarEstadoEnvio(id, estado) {
    const response = await fetch(`${API_BASE_URL}/envios/${id}/estado`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ estado })
    });
    return await response.json();
  }
}

export default new ApiService();