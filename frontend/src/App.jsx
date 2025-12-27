// src/App.jsx
import { useState } from 'react';
import './App.css';
import ImportarXML from './components/ImportarXML';
import Centros from './components/Centros';
import Rutas from './components/Rutas';
import Mensajeros from './components/Mensajeros';
import Paquetes from './components/Paquetes';
import Solicitudes from './components/Solicitudes';
import Dashboard from './components/Dashboard';

function App() {
  const [vistaActual, setVistaActual] = useState('dashboard');
  const [sistemaIniciado, setSistemaIniciado] = useState(false);

  const handleImportExitoso = () => {
    setSistemaIniciado(true);
    setVistaActual('dashboard');
  };

  const renderVista = () => {
    switch (vistaActual) {
      case 'dashboard':
        return <Dashboard />;
      case 'importar':
        return <ImportarXML onImportExitoso={handleImportExitoso} />;
      case 'centros':
        return <Centros />;
      case 'rutas':
        return <Rutas />;
      case 'mensajeros':
        return <Mensajeros />;
      case 'paquetes':
        return <Paquetes />;
      case 'solicitudes':
        return <Solicitudes />;
      default:
        return <Dashboard />;
    }
  };

  return (
    <div className="app">
      {/* Header */}
      <header className="app-header">
        <h1>🚚 LogiTrack - Sistema de Gestión Logística</h1>
        {sistemaIniciado && (
          <span className="badge-iniciado">Sistema Iniciado ✓</span>
        )}
      </header>

      {/* Navegación */}
      <nav className="app-nav">
        <button
          className={vistaActual === 'dashboard' ? 'active' : ''}
          onClick={() => setVistaActual('dashboard')}
        >
          📊 Dashboard
        </button>
        <button
          className={vistaActual === 'importar' ? 'active' : ''}
          onClick={() => setVistaActual('importar')}
        >
          📤 Importar XML
        </button>
        <button
          className={vistaActual === 'centros' ? 'active' : ''}
          onClick={() => setVistaActual('centros')}
          disabled={!sistemaIniciado}
        >
          🏢 Centros
        </button>
        <button
          className={vistaActual === 'rutas' ? 'active' : ''}
          onClick={() => setVistaActual('rutas')}
          disabled={!sistemaIniciado}
        >
          🛣️ Rutas
        </button>
        <button
          className={vistaActual === 'mensajeros' ? 'active' : ''}
          onClick={() => setVistaActual('mensajeros')}
          disabled={!sistemaIniciado}
        >
          👤 Mensajeros
        </button>
        <button
          className={vistaActual === 'paquetes' ? 'active' : ''}
          onClick={() => setVistaActual('paquetes')}
          disabled={!sistemaIniciado}
        >
          📦 Paquetes
        </button>
        <button
          className={vistaActual === 'solicitudes' ? 'active' : ''}
          onClick={() => setVistaActual('solicitudes')}
          disabled={!sistemaIniciado}
        >
          📋 Solicitudes
        </button>
      </nav>

      {/* Contenido Principal */}
      <main className="app-main">
        {renderVista()}
      </main>

      {/* Footer */}
      <footer className="app-footer">
        <p>LogiTrack v1.0 - IPC2 Proyecto 2 - 2024</p>
      </footer>
    </div>
  );
}

export default App;
