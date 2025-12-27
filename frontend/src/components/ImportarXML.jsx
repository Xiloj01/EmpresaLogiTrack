// src/components/ImportarXML.jsx
import { useState } from 'react';
import api from '../services/api';

function ImportarXML({ onImportExitoso }) {
  const [archivo, setArchivo] = useState(null);
  const [cargando, setCargando] = useState(false);
  const [resultado, setResultado] = useState(null);
  const [error, setError] = useState(null);

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file && file.name.endsWith('.xml')) {
      setArchivo(file);
      setError(null);
      setResultado(null);
    } else {
      setError('Por favor selecciona un archivo XML válido');
      setArchivo(null);
    }
  };

  const handleImportar = async () => {
    if (!archivo) {
      setError('Debes seleccionar un archivo XML');
      return;
    }

    setCargando(true);
    setError(null);
    setResultado(null);

    try {
      const response = await api.importarXML(archivo);
      
      if (response.exito) {
        setResultado(response);
        if (onImportExitoso) {
          onImportExitoso();
        }
      } else {
        setError('Error al importar: ' + (response.mensaje || 'Error desconocido'));
      }
    } catch (err) {
      setError('Error de conexión con el servidor: ' + err.message);
    } finally {
      setCargando(false);
    }
  };

  return (
    <div className="card">
      <h2>📤 Importar Archivo XML</h2>
      <p>Selecciona el archivo XML con la configuración inicial del sistema LogiTrack.</p>

      <div className="import-section">
        <div className="file-input-wrapper">
          <input
            type="file"
            accept=".xml"
            onChange={handleFileChange}
            id="fileInput"
            disabled={cargando}
          />
          <label htmlFor="fileInput" className="file-label">
            {archivo ? `📄 ${archivo.name}` : '📁 Seleccionar archivo XML'}
          </label>
        </div>

        <button
          onClick={handleImportar}
          disabled={!archivo || cargando}
          className="btn-primary"
        >
          {cargando ? '⏳ Importando...' : '⬆️ Importar XML'}
        </button>
      </div>

      {error && (
        <div className="alert alert-error">
          <strong>❌ Error</strong>
          <p>{error}</p>
        </div>
      )}

      {resultado && (
        <div className="alert alert-success">
          <h3>✅ Importación Exitosa</h3>
          <p>El archivo XML se ha cargado correctamente en el sistema.</p>
          
          <div className="resultado-grid">
            <div className="resultado-item">
              <span className="label">Centros</span>
              <span className="value">{resultado.centrosCreados}</span>
            </div>
            <div className="resultado-item">
              <span className="label">Rutas</span>
              <span className="value">{resultado.rutasCreadas}</span>
            </div>
            <div className="resultado-item">
              <span className="label">Mensajeros</span>
              <span className="value">{resultado.mensajerosCreados}</span>
            </div>
            <div className="resultado-item">
              <span className="label">Paquetes</span>
              <span className="value">{resultado.paquetesCreados}</span>
            </div>
            <div className="resultado-item">
              <span className="label">Solicitudes</span>
              <span className="value">{resultado.solicitudesCreadas}</span>
            </div>
          </div>

          {resultado.errores && resultado.errores.length > 0 && (
            <div className="errores-section">
              <h4>⚠️ Advertencias durante la importación:</h4>
              <ul>
                {resultado.errores.map((err, idx) => (
                  <li key={idx}>{err}</li>
                ))}
              </ul>
            </div>
          )}

          <div style={{ marginTop: '20px', textAlign: 'center' }}>
            <p style={{ color: '#10b981', fontWeight: '600' }}>
              🎉 ¡Sistema inicializado! Ahora puedes navegar por las demás secciones.
            </p>
          </div>
        </div>
      )}

      <div className="info-section">
        <h3>📋 Formato del XML</h3>
        <p>El archivo XML debe contener la configuración completa del sistema:</p>
        <ul>
          <li><strong>Centros:</strong> Centros de distribución con su capacidad de almacenamiento</li>
          <li><strong>Rutas:</strong> Conexiones entre centros con distancias en kilómetros</li>
          <li><strong>Mensajeros:</strong> Personal disponible asignado a cada centro</li>
          <li><strong>Paquetes:</strong> Paquetes pendientes de envío con su información</li>
          <li><strong>Solicitudes:</strong> Solicitudes de envío con niveles de prioridad (1-10)</li>
        </ul>

        <div style={{ marginTop: '20px', padding: '15px', background: '#fef3c7', borderRadius: '8px', borderLeft: '4px solid #f59e0b' }}>
          <strong>💡 Importante:</strong>
          <p style={{ margin: '5px 0 0 0' }}>
            La carga de un nuevo archivo XML reemplazará completamente los datos actuales del sistema.
          </p>
        </div>
      </div>

      <div className="info-section" style={{ marginTop: '20px' }}>
        <h3>🔍 Ejemplo de Estructura XML</h3>
        <pre style={{ background: '#f3f4f6', padding: '15px', borderRadius: '8px', overflow: 'auto', fontSize: '13px' }}>
{`<?xml version="1.0" encoding="UTF-8"?>
<logitrack>
  <configuracion>
    <centros>
      <centro id="C001">
        <nombre>Centro Norte</nombre>
        <ciudad>Guatemala City</ciudad>
        <capacidad>300</capacidad>
      </centro>
    </centros>
    <rutas>
      <ruta id="R001" origen="C001" destino="C002" distancia="205"/>
    </rutas>
    <mensajeros>
      <mensajero id="M001" nombre="Carlos" capacidad="10" centro="C001"/>
    </mensajeros>
    <paquetes>
      <paquete id="P001" cliente="Empresa X" peso="15" 
               destino="C002" estado="PENDIENTE" centroActual="C001"/>
    </paquetes>
    <solicitudes>
      <solicitud id="S001" tipo="EnvioNormal" paquete="P001" prioridad="7"/>
    </solicitudes>
  </configuracion>
</logitrack>`}
        </pre>
      </div>
    </div>
  );
}

export default ImportarXML;