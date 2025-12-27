import { useState, useEffect } from 'react';
import api from '../services/api';

function Paquetes() {
  const [paquetes, setPaquetes] = useState([]);
  const [centros, setCentros] = useState([]);
  const [mostrarForm, setMostrarForm] = useState(false);
  const [nuevoPaquete, setNuevoPaquete] = useState({
    id: '', cliente: '', peso: '', destinoId: '', centroActualId: ''
  });

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    try {
      const [paquetesData, centrosData] = await Promise.all([
        api.obtenerPaquetes(),
        api.obtenerCentros()
      ]);
      setPaquetes(paquetesData);
      setCentros(centrosData);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const handleCrear = async (e) => {
    e.preventDefault();
    try {
      await api.crearPaquete(nuevoPaquete);
      alert('Paquete creado');
      setMostrarForm(false);
      setNuevoPaquete({ id: '', cliente: '', peso: '', destinoId: '', centroActualId: '' });
      cargarDatos();
    } catch (error) {
      alert('Error al crear');
    }
  };

  const handleEliminar = async (id) => {
    if (!confirm('¿Eliminar paquete?')) return;
    try {
      await api.eliminarPaquete(id);
      alert('Eliminado');
      cargarDatos();
    } catch (error) {
      alert('Error al eliminar');
    }
  };

  return (
    <div className="card">
      <div className="header-section">
        <h2>📦 Gestión de Paquetes</h2>
        <button onClick={() => setMostrarForm(!mostrarForm)} className="btn-primary">
          ➕ Nuevo Paquete
        </button>
      </div>

      {mostrarForm && (
        <form onSubmit={handleCrear} className="form-card">
          <h3>Nuevo Paquete</h3>
          <input placeholder="ID" value={nuevoPaquete.id} 
            onChange={(e) => setNuevoPaquete({...nuevoPaquete, id: e.target.value})} required />
          <input placeholder="Cliente" value={nuevoPaquete.cliente} 
            onChange={(e) => setNuevoPaquete({...nuevoPaquete, cliente: e.target.value})} required />
          <input type="number" placeholder="Peso (kg)" value={nuevoPaquete.peso} 
            onChange={(e) => setNuevoPaquete({...nuevoPaquete, peso: e.target.value})} required />
          
          <select value={nuevoPaquete.centroActualId} 
            onChange={(e) => setNuevoPaquete({...nuevoPaquete, centroActualId: e.target.value})} required>
            <option value="">Centro Actual</option>
            {centros.map(c => <option key={c.id} value={c.id}>{c.nombre}</option>)}
          </select>

          <select value={nuevoPaquete.destinoId} 
            onChange={(e) => setNuevoPaquete({...nuevoPaquete, destinoId: e.target.value})} required>
            <option value="">Destino</option>
            {centros.map(c => <option key={c.id} value={c.id}>{c.nombre}</option>)}
          </select>

          <div className="button-group">
            <button type="submit" className="btn-primary">Crear</button>
            <button type="button" onClick={() => setMostrarForm(false)} className="btn-secondary">Cancelar</button>
          </div>
        </form>
      )}

      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Cliente</th>
            <th>Peso</th>
            <th>Estado</th>
            <th>Centro Actual</th>
            <th>Destino</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {paquetes.map(p => (
            <tr key={p.id}>
              <td><strong>{p.id}</strong></td>
              <td>{p.cliente}</td>
              <td>{p.peso} kg</td>
              <td><span className={`status-badge badge-${p.estado.toLowerCase()}`}>{p.estado}</span></td>
              <td>{p.centroActualId}</td>
              <td>{p.destinoId}</td>
              <td>
                {p.estado === 'PENDIENTE' && (
                  <button onClick={() => handleEliminar(p.id)} className="btn-danger btn-sm">🗑️</button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default Paquetes;