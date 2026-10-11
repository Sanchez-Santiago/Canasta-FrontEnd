import React, { createContext, useContext, useState, useEffect, useMemo, ReactNode } from 'react';
import {
  Usuario,
  Grupo,
  UsuarioGrupo,
  Lista,
  ProductoLista,
  Movimiento,
  Oferta,
  InvitacionGrupo,
  Notificacion,
  TipoGrupo,
  PantallaActiva,
  EstadoProducto,
  EstadoLista,
  RolUsuarioGrupo,
  PrioridadProducto,
  TipoMovimiento,
} from '../types/database';
import {
  INITIAL_USUARIOS,
  INITIAL_GRUPOS,
  INITIAL_USUARIO_GRUPO,
  INITIAL_LISTAS,
  INITIAL_PRODUCTOS,
  INITIAL_MOVIMIENTOS,
  INITIAL_OFERTAS,
  INITIAL_INVITACIONES,
  INITIAL_NOTIFICACIONES,
} from '../data/initialData';

interface AppContextType {
  usuarios: Usuario[];
  currentUsuarioId: number;
  currentUsuario: Usuario;
  grupos: Grupo[];
  usuarioGrupos: UsuarioGrupo[];
  modoGrupo: TipoGrupo;
  activeGrupoId: number;
  activeGrupo: Grupo;
  userGrupalGrupos: Grupo[];
  listas: Lista[];
  activeListas: Lista[];
  productos: ProductoLista[];
  movimientos: Movimiento[];
  activeMovimientos: Movimiento[];
  ofertas: Oferta[];
  invitaciones: InvitacionGrupo[];
  notificaciones: Notificacion[];
  unreadNotifsCount: number;
  pantallaActiva: PantallaActiva;
  userLocation: { localidad: string; provincia: string };

  // Actions
  setModoGrupo: (modo: TipoGrupo) => void;
  setActiveGrupoId: (id: number) => void;
  setPantallaActiva: (pantalla: PantallaActiva) => void;
  switchUsuario: (id: number) => void;
  actualizarUsuario: (data: Partial<Usuario>) => void;
  setUserLocation: (loc: { localidad: string; provincia: string }) => void;

  // Listas
  crearLista: (nombre: string, descripcion?: string) => Lista;
  actualizarEstadoLista: (id_lista: number, estado: EstadoLista) => void;
  eliminarLista: (id_lista: number) => void;

  // Productos
  crearProducto: (producto: {
    id_lista: number;
    nombre: string;
    descripcion?: string;
    prioridad: PrioridadProducto;
    cantidad: number;
    precio_estimado_unitario: number;
    codigo_producto?: string;
  }) => ProductoLista;
  actualizarEstadoProducto: (id_producto: number, estado: EstadoProducto) => void;
  editarProducto: (id_producto: number, data: Partial<ProductoLista>) => void;
  eliminarProducto: (id_producto: number) => void;

  // Movimientos
  crearMovimiento: (mov: {
    nombre: string;
    monto: number;
    tipo: TipoMovimiento;
    categoria?: string;
    descripcion?: string;
    fecha_movimiento: string;
    foto_url?: string;
  }) => Movimiento;
  eliminarMovimiento: (id_movimiento: number) => void;

  // Grupos e Invitaciones
  crearGrupo: (nombre: string, descripcion?: string, categoria?: string, presupuesto?: number) => Grupo;
  invitarUsuarioAGrupo: (id_usuario_destino: number, id_grupo: number) => void;
  responderInvitacion: (id_invitacion: number, respuesta: 'aceptada' | 'rechazada') => void;
  cambiarRolMiembro: (id_usuario: number, id_grupo: number, rol: RolUsuarioGrupo) => void;

  // Ofertas & Notificaciones
  crearOferta: (oferta: Omit<Oferta, 'id_oferta' | 'fecha_creacion'>) => void;
  marcarNotificacionesLeidas: () => void;
  resetDatabaseToDefaults: () => void;
}

const AppContext = createContext<AppContextType | undefined>(undefined);

const STORAGE_PREFIX = 'canasta_plus_v1_';

function loadFromStorage<T>(key: string, fallback: T): T {
  try {
    const raw = localStorage.getItem(STORAGE_PREFIX + key);
    if (raw) return JSON.parse(raw);
  } catch (err) {
    console.error(`Error loading ${key} from storage:`, err);
  }
  return fallback;
}

function saveToStorage<T>(key: string, data: T) {
  try {
    localStorage.setItem(STORAGE_PREFIX + key, JSON.stringify(data));
  } catch (err) {
    console.error(`Error saving ${key} to storage:`, err);
  }
}

export const AppProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [usuarios, setUsuarios] = useState<Usuario[]>(() => loadFromStorage('usuarios', INITIAL_USUARIOS));
  const [currentUsuarioId, setCurrentUsuarioId] = useState<number>(() => loadFromStorage('currentUserId', 1));
  const [grupos, setGrupos] = useState<Grupo[]>(() => loadFromStorage('grupos', INITIAL_GRUPOS));
  const [usuarioGrupos, setUsuarioGrupos] = useState<UsuarioGrupo[]>(() => loadFromStorage('usuarioGrupos', INITIAL_USUARIO_GRUPO));
  const [listas, setListas] = useState<Lista[]>(() => loadFromStorage('listas', INITIAL_LISTAS));
  const [productos, setProductos] = useState<ProductoLista[]>(() => loadFromStorage('productos', INITIAL_PRODUCTOS));
  const [movimientos, setMovimientos] = useState<Movimiento[]>(() => loadFromStorage('movimientos', INITIAL_MOVIMIENTOS));
  const [ofertas, setOfertas] = useState<Oferta[]>(() => loadFromStorage('ofertas', INITIAL_OFERTAS));
  const [invitaciones, setInvitaciones] = useState<InvitacionGrupo[]>(() => loadFromStorage('invitaciones', INITIAL_INVITACIONES));
  const [notificaciones, setNotificaciones] = useState<Notificacion[]>(() => loadFromStorage('notificaciones', INITIAL_NOTIFICACIONES));

  const [modoGrupo, setModoGrupoState] = useState<TipoGrupo>(() => loadFromStorage('modoGrupo', 'grupal'));
  const [activeGrupalId, setActiveGrupalId] = useState<number>(() => loadFromStorage('activeGrupalId', 2));
  const [pantallaActiva, setPantallaActiva] = useState<PantallaActiva>('resumen');
  const [userLocation, setUserLocation] = useState<{ localidad: string; provincia: string }>(() =>
    loadFromStorage('userLocation', { localidad: 'Córdoba Capital', provincia: 'Córdoba' })
  );

  // Sync to localStorage
  useEffect(() => saveToStorage('usuarios', usuarios), [usuarios]);
  useEffect(() => saveToStorage('currentUserId', currentUsuarioId), [currentUsuarioId]);
  useEffect(() => saveToStorage('grupos', grupos), [grupos]);
  useEffect(() => saveToStorage('usuarioGrupos', usuarioGrupos), [usuarioGrupos]);
  useEffect(() => saveToStorage('listas', listas), [listas]);
  useEffect(() => saveToStorage('productos', productos), [productos]);
  useEffect(() => saveToStorage('movimientos', movimientos), [movimientos]);
  useEffect(() => saveToStorage('ofertas', ofertas), [ofertas]);
  useEffect(() => saveToStorage('invitaciones', invitaciones), [invitaciones]);
  useEffect(() => saveToStorage('notificaciones', notificaciones), [notificaciones]);
  useEffect(() => saveToStorage('modoGrupo', modoGrupo), [modoGrupo]);
  useEffect(() => saveToStorage('activeGrupalId', activeGrupalId), [activeGrupalId]);
  useEffect(() => saveToStorage('userLocation', userLocation), [userLocation]);

  const currentUsuario = useMemo(() => {
    return usuarios.find((u) => u.id_usuario === currentUsuarioId) || usuarios[0];
  }, [usuarios, currentUsuarioId]);

  // Grupos grupales en los que participa el usuario actual
  const userGrupalGrupos = useMemo(() => {
    const userGroupIds = new Set(
      usuarioGrupos.filter((ug) => ug.id_usuario === currentUsuarioId).map((ug) => ug.id_grupo)
    );
    return grupos.filter((g) => g.tipo === 'grupal' && userGroupIds.has(g.id_grupo));
  }, [grupos, usuarioGrupos, currentUsuarioId]);

  // Grupo personal del usuario
  const personalGrupo = useMemo(() => {
    // Buscar grupo personal vinculado al usuario o crear/tomar el primero tipo 'personal'
    const link = usuarioGrupos.find(
      (ug) => ug.id_usuario === currentUsuarioId && grupos.find((g) => g.id_grupo === ug.id_grupo && g.tipo === 'personal')
    );
    if (link) {
      return grupos.find((g) => g.id_grupo === link.id_grupo) || grupos[0];
    }
    const defaultPersonal = grupos.find((g) => g.tipo === 'personal');
    return defaultPersonal || grupos[0];
  }, [grupos, usuarioGrupos, currentUsuarioId]);

  // ID del grupo activo según el modo
  const activeGrupoId = useMemo(() => {
    if (modoGrupo === 'personal') {
      return personalGrupo ? personalGrupo.id_grupo : 1;
    }
    return activeGrupalId;
  }, [modoGrupo, personalGrupo, activeGrupalId]);

  const activeGrupo = useMemo(() => {
    return grupos.find((g) => g.id_grupo === activeGrupoId) || personalGrupo || grupos[0];
  }, [grupos, activeGrupoId, personalGrupo]);

  // Listas del grupo activo
  const activeListas = useMemo(() => {
    return listas.filter((l) => l.id_grupo === activeGrupoId);
  }, [listas, activeGrupoId]);

  // Movimientos del grupo activo
  const activeMovimientos = useMemo(() => {
    return movimientos
      .filter((m) => m.id_grupo === activeGrupoId)
      .sort((a, b) => new Date(b.fecha_movimiento).getTime() - new Date(a.fecha_movimiento).getTime());
  }, [movimientos, activeGrupoId]);

  // Conteo de notificaciones no leídas para el grupo activo o generales
  const unreadNotifsCount = useMemo(() => {
    return notificaciones.filter((n) => !n.leida && (n.id_grupo === activeGrupoId || n.id_grupo === 0)).length;
  }, [notificaciones, activeGrupoId]);

  const setModoGrupo = (modo: TipoGrupo) => {
    setModoGrupoState(modo);
  };

  const setActiveGrupoId = (id: number) => {
    setActiveGrupalId(id);
  };

  const switchUsuario = (id: number) => {
    setCurrentUsuarioId(id);
  };

  const actualizarUsuario = (data: Partial<Usuario>) => {
    setUsuarios((prev) =>
      prev.map((u) => (u.id_usuario === currentUsuarioId ? { ...u, ...data } : u))
    );
  };

  // Crear Lista
  const crearLista = (nombre: string, descripcion?: string) => {
    const now = new Date().toISOString().replace('T', ' ').substring(0, 19);
    const newId = Math.max(0, ...listas.map((l) => l.id_lista)) + 1;
    const newLista: Lista = {
      id_lista: newId,
      id_grupo: activeGrupoId,
      id_usuario_creador: currentUsuarioId,
      id_usuario_administrador: currentUsuarioId,
      nombre,
      descripcion,
      estado: 'activa',
      fecha_creacion: now,
      fecha_actualizacion: now,
    };
    setListas((prev) => [newLista, ...prev]);

    // Crear notificación
    const newNotifId = Math.max(0, ...notificaciones.map((n) => n.id_notificacion)) + 1;
    setNotificaciones((prev) => [
      {
        id_notificacion: newNotifId,
        id_grupo: activeGrupoId,
        tipo: 'lista',
        titulo: `Nueva lista: ${nombre}`,
        descripcion: `${currentUsuario.nombre} creó una lista en ${activeGrupo.nombre}`,
        leida: false,
        fecha_creacion: now,
      },
      ...prev,
    ]);

    return newLista;
  };

  const actualizarEstadoLista = (id_lista: number, estado: EstadoLista) => {
    const now = new Date().toISOString().replace('T', ' ').substring(0, 19);
    setListas((prev) =>
      prev.map((l) => (l.id_lista === id_lista ? { ...l, estado, fecha_actualizacion: now } : l))
    );
  };

  const eliminarLista = (id_lista: number) => {
    setListas((prev) => prev.filter((l) => l.id_lista !== id_lista));
    setProductos((prev) => prev.filter((p) => p.id_lista !== id_lista));
  };

  // Crear Producto
  const crearProducto = (prodData: {
    id_lista: number;
    nombre: string;
    descripcion?: string;
    prioridad: PrioridadProducto;
    cantidad: number;
    precio_estimado_unitario: number;
    codigo_producto?: string;
  }) => {
    const now = new Date().toISOString().replace('T', ' ').substring(0, 19);
    const newId = Math.max(0, ...productos.map((p) => p.id_producto_lista)) + 1;
    const newProducto: ProductoLista = {
      id_producto_lista: newId,
      id_lista: prodData.id_lista,
      id_usuario_creador: currentUsuarioId,
      nombre: prodData.nombre,
      descripcion: prodData.descripcion,
      prioridad: prodData.prioridad,
      cantidad: prodData.cantidad,
      precio_estimado_unitario: prodData.precio_estimado_unitario,
      codigo_producto: prodData.codigo_producto || `PRD-${Math.floor(1000 + Math.random() * 9000)}`,
      estado: 'pendiente',
      fecha_creacion: now,
    };
    setProductos((prev) => [...prev, newProducto]);

    // Actualizar fecha_actualizacion de la lista
    setListas((prev) =>
      prev.map((l) => (l.id_lista === prodData.id_lista ? { ...l, fecha_actualizacion: now } : l))
    );

    return newProducto;
  };

  const actualizarEstadoProducto = (id_producto: number, estado: EstadoProducto) => {
    setProductos((prev) =>
      prev.map((p) => (p.id_producto_lista === id_producto ? { ...p, estado } : p))
    );
  };

  const editarProducto = (id_producto: number, data: Partial<ProductoLista>) => {
    setProductos((prev) =>
      prev.map((p) => (p.id_producto_lista === id_producto ? { ...p, ...data } : p))
    );
  };

  const eliminarProducto = (id_producto: number) => {
    setProductos((prev) => prev.filter((p) => p.id_producto_lista !== id_producto));
    setOfertas((prev) => prev.filter((o) => o.id_producto_lista !== id_producto));
  };

  // Crear Movimiento
  const crearMovimiento = (movData: {
    nombre: string;
    monto: number;
    tipo: TipoMovimiento;
    categoria?: string;
    descripcion?: string;
    fecha_movimiento: string;
    foto_url?: string;
  }) => {
    const now = new Date().toISOString().replace('T', ' ').substring(0, 19);
    const newId = Math.max(0, ...movimientos.map((m) => m.id_movimiento)) + 1;
    const newMov: Movimiento = {
      id_movimiento: newId,
      id_usuario_creador: currentUsuarioId,
      id_grupo: activeGrupoId,
      nombre: movData.nombre,
      monto: movData.monto,
      tipo: movData.tipo,
      categoria: movData.categoria || (movData.tipo === 'ingreso' ? 'General' : 'Compras'),
      descripcion: movData.descripcion,
      fecha_movimiento: movData.fecha_movimiento,
      foto_url: movData.foto_url,
      fecha_creacion: now,
    };

    setMovimientos((prev) => [newMov, ...prev]);

    // Notificación
    const newNotifId = Math.max(0, ...notificaciones.map((n) => n.id_notificacion)) + 1;
    setNotificaciones((prev) => [
      {
        id_notificacion: newNotifId,
        id_grupo: activeGrupoId,
        tipo: 'movimiento',
        titulo: `${movData.tipo === 'ingreso' ? 'Ingreso' : 'Gasto'} registrado: $${movData.monto.toLocaleString()}`,
        descripcion: `${currentUsuario.nombre} cargó "${movData.nombre}" en ${activeGrupo.nombre}`,
        leida: false,
        fecha_creacion: now,
      },
      ...prev,
    ]);

    return newMov;
  };

  const eliminarMovimiento = (id_movimiento: number) => {
    setMovimientos((prev) => prev.filter((m) => m.id_movimiento !== id_movimiento));
  };

  // Grupos e Invitaciones
  const crearGrupo = (
    nombre: string,
    descripcion?: string,
    categoria: string = 'General',
    presupuesto: number = 300000
  ) => {
    const now = new Date().toISOString().replace('T', ' ').substring(0, 19);
    const newGroupId = Math.max(0, ...grupos.map((g) => g.id_grupo)) + 1;
    const newGroup: Grupo = {
      id_grupo: newGroupId,
      nombre,
      descripcion,
      categoria,
      tipo: 'grupal',
      presupuesto_mensual: presupuesto,
      fecha_creacion: now,
    };

    setGrupos((prev) => [...prev, newGroup]);

    // Agregar relación usuario-grupo como administrador
    const newUgId = Math.max(0, ...usuarioGrupos.map((ug) => ug.id_usuario_grupo)) + 1;
    setUsuarioGrupos((prev) => [
      ...prev,
      {
        id_usuario_grupo: newUgId,
        id_usuario: currentUsuarioId,
        id_grupo: newGroupId,
        rol: 'administrador',
        fecha_ingreso: now,
      },
    ]);

    // Seleccionar como grupo activo
    setActiveGrupalId(newGroupId);
    setModoGrupoState('grupal');

    return newGroup;
  };

  const invitarUsuarioAGrupo = (id_usuario_destino: number, id_grupo: number) => {
    const now = new Date().toISOString().replace('T', ' ').substring(0, 19);
    const newInvId = Math.max(0, ...invitaciones.map((i) => i.id_invitacion)) + 1;
    const targetUser = usuarios.find((u) => u.id_usuario === id_usuario_destino);
    const targetGroup = grupos.find((g) => g.id_grupo === id_grupo);

    const newInv: InvitacionGrupo = {
      id_invitacion: newInvId,
      id_usuario_origen: currentUsuarioId,
      id_usuario_destino,
      id_grupo,
      estado: 'pendiente',
      fecha_creacion: now,
      fecha_respuesta: null,
    };

    setInvitaciones((prev) => [...prev, newInv]);

    // Notificación
    const newNotifId = Math.max(0, ...notificaciones.map((n) => n.id_notificacion)) + 1;
    setNotificaciones((prev) => [
      {
        id_notificacion: newNotifId,
        id_grupo,
        tipo: 'invitacion',
        titulo: `Invitación enviada a ${targetUser?.nombre || 'usuario'}`,
        descripcion: `Se invitó a formar parte del grupo ${targetGroup?.nombre || ''}`,
        leida: false,
        fecha_creacion: now,
      },
      ...prev,
    ]);
  };

  const responderInvitacion = (id_invitacion: number, respuesta: 'aceptada' | 'rechazada') => {
    const now = new Date().toISOString().replace('T', ' ').substring(0, 19);
    const targetInv = invitaciones.find((i) => i.id_invitacion === id_invitacion);
    if (!targetInv) return;

    setInvitaciones((prev) =>
      prev.map((i) => (i.id_invitacion === id_invitacion ? { ...i, estado: respuesta, fecha_respuesta: now } : i))
    );

    if (respuesta === 'aceptada') {
      const newUgId = Math.max(0, ...usuarioGrupos.map((ug) => ug.id_usuario_grupo)) + 1;
      setUsuarioGrupos((prev) => [
        ...prev,
        {
          id_usuario_grupo: newUgId,
          id_usuario: targetInv.id_usuario_destino,
          id_grupo: targetInv.id_grupo,
          rol: 'miembro',
          fecha_ingreso: now,
        },
      ]);
    }
  };

  const cambiarRolMiembro = (id_usuario: number, id_grupo: number, rol: RolUsuarioGrupo) => {
    setUsuarioGrupos((prev) =>
      prev.map((ug) => (ug.id_usuario === id_usuario && ug.id_grupo === id_grupo ? { ...ug, rol } : ug))
    );
  };

  const crearOferta = (ofertaData: Omit<Oferta, 'id_oferta' | 'fecha_creacion'>) => {
    const now = new Date().toISOString().replace('T', ' ').substring(0, 19);
    const newId = Math.max(0, ...ofertas.map((o) => o.id_oferta)) + 1;
    const newOferta: Oferta = {
      ...ofertaData,
      id_oferta: newId,
      fecha_creacion: now,
    };
    setOfertas((prev) => [...prev, newOferta]);

    const targetProd = productos.find((p) => p.id_producto_lista === ofertaData.id_producto_lista);
    const targetList = targetProd ? listas.find((l) => l.id_lista === targetProd.id_lista) : null;

    if (targetList) {
      const newNotifId = Math.max(0, ...notificaciones.map((n) => n.id_notificacion)) + 1;
      setNotificaciones((prev) => [
        {
          id_notificacion: newNotifId,
          id_grupo: targetList.id_grupo,
          tipo: 'oferta',
          titulo: `¡Nueva Oferta: ${ofertaData.nombre_comercio}!`,
          descripcion: `${ofertaData.porcentaje_descuento ? `${ofertaData.porcentaje_descuento}% OFF en ` : ''}${targetProd?.nombre}`,
          leida: false,
          fecha_creacion: now,
        },
        ...prev,
      ]);
    }
  };

  const marcarNotificacionesLeidas = () => {
    setNotificaciones((prev) => prev.map((n) => ({ ...n, leida: true })));
  };

  const resetDatabaseToDefaults = () => {
    setUsuarios(INITIAL_USUARIOS);
    setCurrentUsuarioId(1);
    setGrupos(INITIAL_GRUPOS);
    setUsuarioGrupos(INITIAL_USUARIO_GRUPO);
    setListas(INITIAL_LISTAS);
    setProductos(INITIAL_PRODUCTOS);
    setMovimientos(INITIAL_MOVIMIENTOS);
    setOfertas(INITIAL_OFERTAS);
    setInvitaciones(INITIAL_INVITACIONES);
    setNotificaciones(INITIAL_NOTIFICACIONES);
    setModoGrupoState('grupal');
    setActiveGrupalId(2);
    setPantallaActiva('resumen');
  };

  return (
    <AppContext.Provider
      value={{
        usuarios,
        currentUsuarioId,
        currentUsuario,
        grupos,
        usuarioGrupos,
        modoGrupo,
        activeGrupoId,
        activeGrupo,
        userGrupalGrupos,
        listas,
        activeListas,
        productos,
        movimientos,
        activeMovimientos,
        ofertas,
        invitaciones,
        notificaciones,
        unreadNotifsCount,
        pantallaActiva,
        userLocation,
        setModoGrupo,
        setActiveGrupoId,
        setPantallaActiva,
        switchUsuario,
        actualizarUsuario,
        setUserLocation,
        crearLista,
        actualizarEstadoLista,
        eliminarLista,
        crearProducto,
        actualizarEstadoProducto,
        editarProducto,
        eliminarProducto,
        crearMovimiento,
        eliminarMovimiento,
        crearGrupo,
        invitarUsuarioAGrupo,
        responderInvitacion,
        cambiarRolMiembro,
        crearOferta,
        marcarNotificacionesLeidas,
        resetDatabaseToDefaults,
      }}
    >
      {children}
    </AppContext.Provider>
  );
};

export const useApp = () => {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useApp must be used within an AppProvider');
  }
  return context;
};
