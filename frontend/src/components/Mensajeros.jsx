import { useState, useEffect } from 'react';
import api from '../services/api';

function Mensajeros() {
  const [mensajeros, setMensajeros] = useState([]);
  const [centros, setCentros] = useState([]);
  const [mostrarForm, setMostrarForm] = useState(false);
  const [nuevoMensajero, setNuevoMensajero] = useState({
    id: '', nombre: '', capacidad: '', centroId: ''
  });

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    try {
      const [mensajerosData, centrosData] = await Promise.all([
        api.obtenerMensajeros(),
        api.obtenerCentros()
      ]);
      setMensajeros(mensajerosData);
      setCentros(centrosData);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const handleCrear = async (e) => {
    e.preventDefault();
    try {
      await api.crearMensajero(nuevoMensajero);
      alert('Mensajero creado');
      setMostrarForm(false);
      setNuevoMensajero({ id: '', nombre: '', capacidad: '', centroId: '' });
      cargarDatos();
    } catch (error) {
      alert('Error al crear');
    }
  };

  const cambiarEstado = async (id, estadoActual) => {
    const nuevoEstado = estadoActual === 'DISPONIBLE' ? 'EN_TRANSITO' : 'DISPONIBLE';
    try {
      await api.cambiarEstadoMensajero(id, nuevoEstado);
      alert('Estado actualizado');
      cargarDatos();
    } catch (error) {
      alert('Error al cambiar estado');
    }
  };

  return (
    <div className="card">
      <div className="header-section">
        <h2>👤 Gestión de Mensajeros</h2>
        <button onClick={() => setMostrarForm(!mostrarForm)} className="btn-primary">
          ➕ Nuevo Mensajero
        </button>
      </div>

      {mostrarForm && (
        <form onSubmit={handleCrear} className="form-card">
          <h3>Nuevo Mensajero</h3>
          <input placeholder="ID" value={nuevoMensajero.id} 
            onChange={(e) => setNuevoMensajero({...nuevoMensajero, id: e.target.value})} required />
          <input placeholder="Nombre" value={nuevoMensajero.nombre} 
            onChange={(e) => setNuevoMensajero({...nuevoMensajero, nombre: e.target.value})} required />
          <input type="number" placeholder="Capacidad" value={nuevoMensajero.capacidad} 
            onChange={(e) => setNuevoMensajero({...nuevoMensajero, capacidad: e.target.value})} required />
          
          <select value={nuevoMensajero.centroId} 
            onChange={(e) => setNuevoMensajero({...nuevoMensajero, centroId: e.target.value})} required>
            <option value="">Centro Asignado</option>
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
            <th>Nombre</th>
            <th>Capacidad</th>
            <th>Centro</th>
            <th>Estado</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {mensajeros.map(m => (
            <tr key={m.id}>
              <td><strong>{m.id}</strong></td>
              <td>{m.nombre}</td>
              <td>{m.capacidad}</td>
              <td>{m.centroId}</td>
              <td><span className={`status-badge badge-${m.estado.toLowerCase()}`}>{m.estado}</span></td>
              <td>
                <button onClick={() => cambiarEstado(m.id, m.estado)} className="btn-sm btn-primary">
                  Cambiar Estado
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default Mensajeros;