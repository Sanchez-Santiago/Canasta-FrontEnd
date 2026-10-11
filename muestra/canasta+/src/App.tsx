/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';
import { AppProvider, useApp } from './context/AppContext';
import { MobileShell } from './components/layout/MobileShell';
import { ResumenScreen } from './components/screens/ResumenScreen';
import { ListasScreen } from './components/screens/ListasScreen';
import { OfertasScreen } from './components/screens/OfertasScreen';
import { HistorialScreen } from './components/screens/HistorialScreen';
import { PerfilScreen } from './components/screens/PerfilScreen';

const ScreenRenderer: React.FC = () => {
  const { pantallaActiva } = useApp();

  switch (pantallaActiva) {
    case 'resumen':
      return <ResumenScreen />;
    case 'listas':
      return <ListasScreen />;
    case 'ofertas':
      return <OfertasScreen />;
    case 'historial':
      return <HistorialScreen />;
    case 'perfil':
      return <PerfilScreen />;
    default:
      return <ResumenScreen />;
  }
};

export default function App() {
  return (
    <AppProvider>
      <MobileShell>
        <ScreenRenderer />
      </MobileShell>
    </AppProvider>
  );
}
