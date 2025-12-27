// src/components/Dashboard.jsx
import { useState, useEffect } from 'react';
import api from '../services/api';

function Dashboard() {
  const [centros, setCentros] = useState([]);
  const [paquetes, setPaquetes] = useState([]);
  const [mensajeros, setMensajeros] = useState([]);
  const [solicitudes, setSolicitudes] = useState([]);
  const [cargando, setCargando] = useState(true);

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    try {
      const [centrosData, paquetesData, mensajerosData, solicitudesData] = await Promise.all([
        api.obtenerCentros(),
        api.obtenerPaquetes(),
        api.obtenerMensajeros(),
        api.obtenerSolicitudes()
      ]);

      setCentros(centrosData);
      setPaquetes(paquetesData);
      setMensajeros(mensajerosData);
      setSolicitudes(solicitudesData);
    } catch (error) {
      console.error('Error al cargar datos:', error);
    } finally {
      setCargando(false);
    }
  };

  const handleExportar = async () => {
    try {
      await api.exportarXML();
      alert('XML exportado exitosamente');
    } catch (error) {
      alert('Error al exportar XML: ' + error.message);
    }
  };

  if (cargando) {
    return <div className="loading">Cargando datos...</div>;
  }

  const paquetesPendientes = paquetes.filter(p => p.estado === 'PENDIENTE').length;
  const paquetesEnTransito = paquetes.filter(p => p.estado === 'EN_TRANSITO').length;
  const paquetesEntregados = paquetes.filter(p => p.estado === 'ENTREGADO').length;
  const mensajerosDisponibles = mensajeros.filter(m => m.estado === 'DISPONIBLE').length;

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h2>📊 Panel de Control</h2>
        <button onClick={handleExportar} className="btn-export">
          💾 Exportar XML
        </button>
      </div>

      {/* Estadísticas principales */}
      <div className="stats-grid">
        <div className="stat-card stat-centros">
          <div className="stat-icon">🏢</div>
          <div className="stat-info">
            <h3>{centros.length}</h3>
            <p>Centros de Distribución</p>
          </div>
        </div>

        <div className="stat-card stat-paquetes">
          <div className="stat-icon">📦</div>
          <div className="stat-info">
            <h3>{paquetes.length}</h3>
            <p>Paquetes Totales</p>
          </div>
        </div>

        <div className="stat-card stat-mensajeros">
          <div className="stat-icon">👤</div>
          <div className="stat-info">
            <h3>{mensajeros.length}</h3>
            <p>Mensajeros</p>
          </div>
        </div>

        <div className="stat-card stat-solicitudes">
          <div className="stat-icon">📋</div>
          <div className="stat-info">
            <h3>{solicitudes.length}</h3>
            <p>Solicitudes</p>
          </div>
        </div>
      </div>

      {/* Detalles de paquetes */}
      <div className="details-section">
        <h3>Estado de Paquetes</h3>
        <div className="paquetes-grid">
          <div className="paquete-status">
            <span className="status-badge badge-pendiente">Pendientes</span>
            <span className="status-count">{paquetesPendientes}</span>
          </div>
          <div className="paquete-status">
            <span className="status-badge badge-transito">En Tránsito</span>
            <span className="status-count">{paquetesEnTransito}</span>
          </div>
          <div className="paquete-status">
            <span className="status-badge badge-entregado">Entregados</span>
            <span className="status-count">{paquetesEntregados}</span>
          </div>
        </div>
      </div>

      {/* Mensajeros disponibles */}
      <div className="details-section">
        <h3>Mensajeros Disponibles</h3>
        <p className="mensajeros-disponibles">
          {mensajerosDisponibles} de {mensajeros.length} mensajeros están disponibles
        </p>
        <div className="progress-bar">
          <div 
            className="progress-fill" 
            style={{ width: `${mensajeros.length > 0 ? (mensajerosDisponibles / mensajeros.length) * 100 : 0}%` }}
          />
        </div>
      </div>

      {/* Centros */}
      <div className="details-section">
        <h3>Centros de Distribución</h3>
        {centros.length === 0 ? (
          <p className="empty-message">No hay centros cargados. Importa un archivo XML primero.</p>
        ) : (
          <div className="centros-list">
            {centros.map(centro => (
              <div key={centro.id} className="centro-item">
                <div className="centro-header">
                  <strong>{centro.nombre}</strong>
                  <span className="centro-id">{centro.id}</span>
                </div>
                <div className="centro-info">
                  <span>📍 {centro.ciudad}</span>
                  <span>📦 {centro.paquetesAlmacenados?.length || 0} paquetes</span>
                  <span>👤 {centro.mensajerosAsignados?.length || 0} mensajeros</span>
                  <span>💼 Capacidad: {centro.capacidad}</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Solicitudes Pendientes */}
      {solicitudes.length > 0 && (
        <div className="details-section">
          <h3>Solicitudes Pendientes</h3>
          <p>Hay {solicitudes.length} solicitudes en cola esperando ser procesadas.</p>
        </div>
      )}
    </div>
  );
}

export default Dashboard;