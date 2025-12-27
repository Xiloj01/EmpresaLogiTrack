import { useState, useEffect } from 'react';
import api from '../services/api';

function Rutas() {
  const [rutas, setRutas] = useState([]);
  const [centros, setCentros] = useState([]);
  const [mostrarForm, setMostrarForm] = useState(false);
  const [nuevaRuta, setNuevaRuta] = useState({
    id: '', origenId: '', destinoId: '', distancia: ''
  });

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    try {
      const [rutasData, centrosData] = await Promise.all([
        api.obtenerRutas(),
        api.obtenerCentros()
      ]);
      setRutas(rutasData);
      setCentros(centrosData);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const handleCrear = async (e) => {
    e.preventDefault();
    try {
      await api.crearRuta(nuevaRuta);
      alert('Ruta creada');
      setMostrarForm(false);
      setNuevaRuta({ id: '', origenId: '', destinoId: '', distancia: '' });
      cargarDatos();
    } catch (error) {
      alert('Error al crear');
    }
  };

  const handleEliminar = async (id) => {
    if (!confirm('¿Eliminar ruta?')) return;
    try {
      await api.eliminarRuta(id);
      alert('Eliminada');
      cargarDatos();
    } catch (error) {
      alert('Error al eliminar');
    }
  };

  return (
    <div className="card">
      <div className="header-section">
        <h2>🛣️ Gestión de Rutas</h2>
        <button onClick={() => setMostrarForm(!mostrarForm)} className="btn-primary">
          ➕ Nueva Ruta
        </button>
      </div>

      {mostrarForm && (
        <form onSubmit={handleCrear} className="form-card">
          <h3>Nueva Ruta</h3>
          <input placeholder="ID (ej: R001)" value={nuevaRuta.id} 
            onChange={(e) => setNuevaRuta({...nuevaRuta, id: e.target.value})} required />
          
          <select value={nuevaRuta.origenId} 
            onChange={(e) => setNuevaRuta({...nuevaRuta, origenId: e.target.value})} required>
            <option value="">Centro Origen</option>
            {centros.map(c => <option key={c.id} value={c.id}>{c.nombre} ({c.id})</option>)}
          </select>

          <select value={nuevaRuta.destinoId} 
            onChange={(e) => setNuevaRuta({...nuevaRuta, destinoId: e.target.value})} required>
            <option value="">Centro Destino</option>
            {centros.map(c => <option key={c.id} value={c.id}>{c.nombre} ({c.id})</option>)}
          </select>

          <input type="number" placeholder="Distancia (km)" value={nuevaRuta.distancia} 
            onChange={(e) => setNuevaRuta({...nuevaRuta, distancia: e.target.value})} required />

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
            <th>Origen</th>
            <th>Destino</th>
            <th>Distancia (km)</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {rutas.map(r => (
            <tr key={r.id}>
              <td><strong>{r.id}</strong></td>
              <td>{r.origenId}</td>
              <td>{r.destinoId}</td>
              <td>{r.distancia} km</td>
              <td>
                <button onClick={() => handleEliminar(r.id)} className="btn-danger btn-sm">🗑️</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default Rutas;