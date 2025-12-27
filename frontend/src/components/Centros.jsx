import { useState, useEffect } from 'react';
import api from '../services/api';

function Centros() {
  const [centros, setCentros] = useState([]);
  const [centroSeleccionado, setCentroSeleccionado] = useState(null);
  const [detalles, setDetalles] = useState(null);

  useEffect(() => {
    cargarCentros();
  }, []);

  const cargarCentros = async () => {
    try {
      const data = await api.obtenerCentros();
      setCentros(data);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const verDetalles = async (id) => {
    try {
      const data = await api.obtenerCentro(id);
      setDetalles(data);
      setCentroSeleccionado(id);
    } catch (error) {
      alert('Error al cargar detalles');
    }
  };

  return (
    <div className="card">
      <h2>🏢 Centros de Distribución</h2>
      
      <div className="centros-grid">
        {centros.map(centro => (
          <div key={centro.id} className="centro-card" onClick={() => verDetalles(centro.id)}>
            <h3>{centro.nombre}</h3>
            <p className="centro-id">{centro.id}</p>
            <p>📍 {centro.ciudad}</p>
            <p>📦 Capacidad: {centro.capacidad}</p>
            <p>📊 Paquetes: {centro.paquetesAlmacenados?.length || 0}</p>
            <p>👤 Mensajeros: {centro.mensajerosAsignados?.length || 0}</p>
          </div>
        ))}
      </div>

      {detalles && (
        <div className="detalles-modal">
          <div className="modal-content">
            <button onClick={() => setDetalles(null)} className="btn-close">✖️</button>
            <h3>Detalles de {detalles.nombre}</h3>
            <div className="detalles-info">
              <p><strong>ID:</strong> {detalles.id}</p>
              <p><strong>Ciudad:</strong> {detalles.ciudad}</p>
              <p><strong>Capacidad:</strong> {detalles.capacidad}</p>
              <p><strong>Carga Actual:</strong> {detalles.cargaActual}</p>
              <p><strong>Uso:</strong> {detalles.porcentajeUso?.toFixed(2)}%</p>
              <p><strong>Paquetes:</strong> {detalles.paquetesAlmacenados}</p>
              <p><strong>Mensajeros:</strong> {detalles.mensajerosAsignados}</p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Centros;