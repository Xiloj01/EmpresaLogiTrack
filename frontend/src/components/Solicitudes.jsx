// src/components/Solicitudes.jsx
import { useState, useEffect } from 'react';
import api from '../services/api';

function Solicitudes() {
  const [solicitudes, setSolicitudes] = useState([]);
  const [paquetes, setPaquetes] = useState([]);
  const [mostrarFormulario, setMostrarFormulario] = useState(false);
  const [cargando, setCargando] = useState(false);
  const [resultado, setResultado] = useState(null);

  const [nuevaSolicitud, setNuevaSolicitud] = useState({
    id: '',
    tipo: 'EnvioNormal',
    paqueteId: '',
    prioridad: 5
  });

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    try {
      const [solicitudesData, paquetesData] = await Promise.all([
        api.obtenerSolicitudes(),
        api.obtenerPaquetes()
      ]);
      setSolicitudes(solicitudesData);
      setPaquetes(paquetesData.filter(p => p.estado === 'PENDIENTE'));
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const handleCrear = async (e) => {
    e.preventDefault();
    setCargando(true);
    try {
      await api.crearSolicitud(nuevaSolicitud);
      alert('Solicitud creada exitosamente');
      setMostrarFormulario(false);
      setNuevaSolicitud({ id: '', tipo: 'EnvioNormal', paqueteId: '', prioridad: 5 });
      cargarDatos();
    } catch (error) {
      alert('Error al crear solicitud');
    } finally {
      setCargando(false);
    }
  };

  const handleProcesarSiguiente = async () => {
    setCargando(true);
    setResultado(null);
    try {
      const result = await api.procesarSiguienteSolicitud();
      setResultado(result);
      cargarDatos();
    } catch (error) {
      alert('Error al procesar solicitud');
    } finally {
      setCargando(false);
    }
  };

  const handleProcesarVarias = async () => {
    const n = prompt('¿Cuántas solicitudes deseas procesar?', '3');
    if (!n) return;

    setCargando(true);
    try {
      const result = await api.procesarVariasSolicitudes(parseInt(n));
      alert(`Procesadas ${result.totalProcesadas} solicitudes`);
      cargarDatos();
    } catch (error) {
      alert('Error al procesar solicitudes');
    } finally {
      setCargando(false);
    }
  };

  const handleEliminar = async (id) => {
    if (!confirm('¿Eliminar esta solicitud?')) return;
    
    try {
      await api.eliminarSolicitud(id);
      alert('Solicitud eliminada');
      cargarDatos();
    } catch (error) {
      alert('Error al eliminar');
    }
  };

  return (
    <div className="card">
      <div className="header-section">
        <h2>📋 Gestión de Solicitudes</h2>
        <div className="button-group">
          <button 
            onClick={() => setMostrarFormulario(!mostrarFormulario)}
            className="btn-primary"
          >
            ➕ Nueva Solicitud
          </button>
          <button 
            onClick={handleProcesarSiguiente}
            disabled={cargando || solicitudes.length === 0}
            className="btn-success"
          >
            ▶️ Procesar Siguiente
          </button>
          <button 
            onClick={handleProcesarVarias}
            disabled={cargando || solicitudes.length === 0}
            className="btn-success"
          >
            ⏩ Procesar Varias
          </button>
        </div>
      </div>

      {mostrarFormulario && (
        <form onSubmit={handleCrear} className="form-card">
          <h3>Nueva Solicitud</h3>
          
          <div className="form-group">
            <label>ID de Solicitud:</label>
            <input
              type="text"
              value={nuevaSolicitud.id}
              onChange={(e) => setNuevaSolicitud({...nuevaSolicitud, id: e.target.value})}
              placeholder="S001"
              required
            />
          </div>

          <div className="form-group">
            <label>Tipo:</label>
            <select
              value={nuevaSolicitud.tipo}
              onChange={(e) => setNuevaSolicitud({...nuevaSolicitud, tipo: e.target.value})}
            >
              <option value="EnvioNormal">Envío Normal</option>
              <option value="EnvioExpress">Envío Express</option>
              <option value="EnvioUrgente">Envío Urgente</option>
            </select>
          </div>

          <div className="form-group">
            <label>Paquete:</label>
            <select
              value={nuevaSolicitud.paqueteId}
              onChange={(e) => setNuevaSolicitud({...nuevaSolicitud, paqueteId: e.target.value})}
              required
            >
              <option value="">Selecciona un paquete</option>
              {paquetes.map(p => (
                <option key={p.id} value={p.id}>
                  {p.id} - {p.cliente} ({p.peso}kg)
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>Prioridad (1-10):</label>
            <input
              type="number"
              min="1"
              max="10"
              value={nuevaSolicitud.prioridad}
              onChange={(e) => setNuevaSolicitud({...nuevaSolicitud, prioridad: parseInt(e.target.value)})}
              required
            />
          </div>

          <div className="button-group">
            <button type="submit" disabled={cargando} className="btn-primary">
              Crear Solicitud
            </button>
            <button type="button" onClick={() => setMostrarFormulario(false)} className="btn-secondary">
              Cancelar
            </button>
          </div>
        </form>
      )}

      {resultado && (
        <div className={`alert ${resultado.exito ? 'alert-success' : 'alert-error'}`}>
          <h3>{resultado.exito ? '✅ Solicitud Procesada' : '❌ Error'}</h3>
          {resultado.exito ? (
            <div>
              <p>Solicitud: {resultado.solicitudId}</p>
              <p>Paquete: {resultado.paqueteId}</p>
              <p>Mensajero: {resultado.mensajeroId}</p>
              <p>Paquetes asignados: {resultado.paquetesAsignados}</p>
              <p>Ruta: {resultado.origen} → {resultado.destino}</p>
            </div>
          ) : (
            <p>{resultado.motivo || resultado.mensaje}</p>
          )}
        </div>
      )}

      <div className="table-container">
        <h3>Cola de Solicitudes (Ordenadas por Prioridad)</h3>
        {solicitudes.length === 0 ? (
          <p className="empty-message">No hay solicitudes pendientes</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Tipo</th>
                <th>Paquete</th>
                <th>Prioridad</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {solicitudes.map(solicitud => (
                <tr key={solicitud.id}>
                  <td><strong>{solicitud.id}</strong></td>
                  <td>{solicitud.tipo}</td>
                  <td>{solicitud.paqueteId}</td>
                  <td>
                    <span className="priority-badge">{solicitud.prioridad}</span>
                  </td>
                  <td>
                    <span className={`status-badge badge-${solicitud.estado.toLowerCase()}`}>
                      {solicitud.estado}
                    </span>
                  </td>
                  <td>
                    {solicitud.estado === 'PENDIENTE' && (
                      <button 
                        onClick={() => handleEliminar(solicitud.id)}
                        className="btn-danger btn-sm"
                      >
                        🗑️
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}

export default Solicitudes;