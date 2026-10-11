/**
 * Tipos de datos que reflejan el esquema SQLite oficial de Canasta+
 */

export interface Usuario {
  id_usuario: number;
  nombre: string;
  email: string;
  contrasena?: string;
  localidad: string;
  provincia: string;
  pais: string;
  domicilio?: string;
  avatar_url?: string;
  fecha_creacion: string;
}

export type TipoGrupo = 'personal' | 'grupal';

export interface Grupo {
  id_grupo: number;
  nombre: string;
  descripcion?: string;
  categoria?: string;
  tipo: TipoGrupo;
  presupuesto_mensual?: number;
  fecha_creacion: string;
}

export type RolUsuarioGrupo = 'administrador' | 'miembro';

export interface UsuarioGrupo {
  id_usuario_grupo: number;
  id_usuario: number;
  id_grupo: number;
  rol: RolUsuarioGrupo;
  fecha_ingreso: string;
}

export type EstadoLista = 'activa' | 'completada' | 'archivada';

export interface Lista {
  id_lista: number;
  id_grupo: number;
  id_usuario_creador: number;
  id_usuario_administrador?: number | null;
  nombre: string;
  descripcion?: string;
  estado: EstadoLista;
  fecha_creacion: string;
  fecha_actualizacion: string;
}

export type PrioridadProducto = 'prioritario' | 'esencial' | 'secundario';
export type EstadoProducto = 'pendiente' | 'comprado' | 'omitido';

export interface ProductoLista {
  id_producto_lista: number;
  id_lista: number;
  id_usuario_creador: number;
  nombre: string;
  descripcion?: string;
  prioridad: PrioridadProducto;
  cantidad: number;
  precio_estimado_unitario: number;
  codigo_producto?: string;
  estado: EstadoProducto;
  fecha_creacion: string;
}

export type TipoMovimiento = 'ingreso' | 'gasto';

export interface Movimiento {
  id_movimiento: number;
  id_usuario_creador: number;
  id_grupo: number;
  nombre: string;
  monto: number;
  descripcion?: string;
  tipo: TipoMovimiento;
  categoria?: string;
  foto_url?: string;
  fecha_movimiento: string;
  fecha_creacion: string;
}

export interface Oferta {
  id_oferta: number;
  id_producto_lista: number;
  nombre_comercio: string;
  ubicacion?: string;
  localidad: string;
  provincia: string;
  latitud?: number;
  longitud?: number;
  fecha_inicio: string;
  fecha_fin: string;
  porcentaje_descuento?: number;
  precio_original?: number;
  precio_oferta?: number;
  descripcion?: string;
  fecha_creacion: string;
}

export type EstadoInvitacion = 'pendiente' | 'aceptada' | 'rechazada';

export interface InvitacionGrupo {
  id_invitacion: number;
  id_usuario_origen: number;
  id_usuario_destino: number;
  id_grupo: number;
  estado: EstadoInvitacion;
  fecha_creacion: string;
  fecha_respuesta?: string | null;
}

export interface Notificacion {
  id_notificacion: number;
  id_grupo: number;
  tipo: 'lista' | 'movimiento' | 'oferta' | 'invitacion' | 'alerta';
  titulo: string;
  descripcion?: string;
  leida?: boolean;
  fecha_creacion: string;
}

export type PantallaActiva = 'resumen' | 'listas' | 'ofertas' | 'historial' | 'perfil';
