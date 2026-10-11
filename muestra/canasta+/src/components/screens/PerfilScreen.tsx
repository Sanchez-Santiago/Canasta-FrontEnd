import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { RolUsuarioGrupo } from '../../types/database';
import {
  User,
  Users,
  Shield,
  ShieldAlert,
  MapPin,
  Bell,
  LogOut,
  RotateCcw,
  UserPlus,
  Check,
  X,
  Mail,
  Home,
  ChevronRight,
  Sparkles,
  Edit3,
} from 'lucide-react';
import { NotificationsModal } from '../modals/NotificationsModal';
import { InviteGroupModal } from '../modals/InviteGroupModal';

export const PerfilScreen: React.FC = () => {
  const {
    currentUsuario,
    usuarios,
    switchUsuario,
    actualizarUsuario,
    activeGrupo,
    modoGrupo,
    usuarioGrupos,
    invitaciones,
    invitarUsuarioAGrupo,
    responderInvitacion,
    cambiarRolMiembro,
    unreadNotifsCount,
    userLocation,
    setUserLocation,
    resetDatabaseToDefaults,
  } = useApp();

  const [showNotifsModal, setShowNotifsModal] = useState(false);
  const [showEditProfile, setShowEditProfile] = useState(false);
  const [showInviteModal, setShowInviteModal] = useState(false);

  // Edit profile state
  const [nombre, setNombre] = useState(currentUsuario.nombre);
  const [email, setEmail] = useState(currentUsuario.email);
  const [localidad, setLocalidad] = useState(currentUsuario.localidad || 'Córdoba Capital');
  const [provincia, setProvincia] = useState(currentUsuario.provincia || 'Córdoba');
  const [domicilio, setDomicilio] = useState(currentUsuario.domicilio || '');

  // Current group relation for logged in user
  const currentUserRel = usuarioGrupos.find(
    (ug) => ug.id_grupo === activeGrupo.id_grupo && ug.id_usuario === currentUsuario.id_usuario
  );
  const isAdmin = currentUserRel?.rol === 'administrador';

  // Group members
  const groupMembers = usuarioGrupos
    .filter((ug) => ug.id_grupo === activeGrupo.id_grupo)
    .map((ug) => {
      const userObj = usuarios.find((u) => u.id_usuario === ug.id_usuario);
      return {
        ...ug,
        usuario: userObj,
      };
    });

  // Pending invitations for current group or current user
  const pendingInvitations = invitaciones.filter(
    (i) => i.id_grupo === activeGrupo.id_grupo && i.estado === 'pendiente'
  );

  const handleSaveProfile = (e: React.FormEvent) => {
    e.preventDefault();
    actualizarUsuario({
      nombre: nombre.trim(),
      email: email.trim(),
      localidad: localidad.trim(),
      provincia: provincia.trim(),
      domicilio: domicilio.trim() || undefined,
    });
    setUserLocation({
      localidad: localidad.trim(),
      provincia: provincia.trim(),
    });
    setShowEditProfile(false);
  };

  return (
    <div className="space-y-3.5 pb-14">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-base font-bold text-white tracking-tight">Mi Perfil y Ajustes</h2>
          <p className="text-xs text-slate-400">Administración de cuenta y grupo</p>
        </div>
        <button
          onClick={() => setShowEditProfile(true)}
          className="p-2 rounded-2xl bg-black/40 border border-white/10 text-emerald-400 hover:text-emerald-300 text-xs font-semibold flex items-center gap-1.5 transition-colors"
        >
          <Edit3 className="w-3.5 h-3.5" />
          <span>Editar</span>
        </button>
      </div>

      {/* Profile Card (Bento style) */}
      <div className="p-4 rounded-3xl bento-card border border-white/10 shadow-xl relative overflow-hidden">
        <div className="flex items-start gap-3.5">
          <div className="w-13 h-13 rounded-2xl bg-emerald-950/80 border border-emerald-500/30 text-emerald-300 font-extrabold text-lg flex items-center justify-center shadow-lg shrink-0">
            {currentUsuario.nombre.substring(0, 2).toUpperCase()}
          </div>
          <div className="min-w-0 flex-1">
            <div className="flex items-center gap-2">
              <h3 className="text-sm font-bold text-white truncate">{currentUsuario.nombre}</h3>
            </div>
            <p className="text-xs text-slate-400 truncate flex items-center gap-1 mt-0.5">
              <Mail className="w-3 h-3 text-slate-500" />
              {currentUsuario.email}
            </p>
            <div className="flex items-center gap-2 text-[11px] text-slate-400 mt-2">
              <span className="flex items-center gap-1">
                <MapPin className="w-3 h-3 text-emerald-400" />
                {currentUsuario.localidad}, {currentUsuario.provincia}
              </span>
              {currentUsuario.domicilio && (
                <>
                  <span aria-hidden="true">·</span>
                  <span className="truncate max-w-[120px]">{currentUsuario.domicilio}</span>
                </>
              )}
            </div>
          </div>
        </div>

        {/* Switch demo users banner */}
        <div className="mt-3.5 pt-3 border-t border-white/10">
          <label className="block text-[11px] font-semibold text-slate-400 mb-1.5">
            Cambiar usuario activo (Simulación de roles):
          </label>
          <div className="flex items-center gap-1.5 overflow-x-auto pb-1">
            {usuarios.map((u) => (
              <button
                key={u.id_usuario}
                onClick={() => switchUsuario(u.id_usuario)}
                className={`py-1.5 px-2.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-all border ${
                  u.id_usuario === currentUsuario.id_usuario
                    ? 'bg-emerald-900/80 text-emerald-300 border-emerald-500/40 font-bold shadow-sm'
                    : 'bg-black/40 text-slate-400 border-white/5 hover:text-white'
                }`}
              >
                {u.nombre.split(' ')[0]}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* Group Members & Permissions Management */}
      <div className="p-4 rounded-3xl bento-card border border-white/10 shadow-lg">
        <div className="flex items-center justify-between mb-3">
          <div className="flex items-center gap-2">
            <div className="p-1.5 rounded-xl bg-black/40 border border-white/10 text-emerald-400">
              <Users className="w-4 h-4" />
            </div>
            <div>
              <h3 className="text-xs font-bold text-white">Integrantes del Grupo Activo</h3>
              <p className="text-[10px] text-slate-400">
                {activeGrupo.nombre} ({activeGrupo.tipo})
              </p>
            </div>
          </div>

          {activeGrupo.tipo === 'grupal' && (
            <button
              onClick={() => setShowInviteModal(true)}
              className="text-xs font-bold text-emerald-300 hover:text-emerald-200 py-1.5 px-3 rounded-2xl bg-emerald-950/70 border border-emerald-500/30 flex items-center gap-1 shadow-sm active:scale-95 transition-all"
            >
              <UserPlus className="w-3.5 h-3.5" />
              <span>Invitar</span>
            </button>
          )}
        </div>

        {/* Members List with Roles */}
        <div className="space-y-2">
          {groupMembers.map((m) => {
            const isMe = m.id_usuario === currentUsuario.id_usuario;

            return (
              <div
                key={m.id_usuario_grupo}
                className="p-2.5 rounded-2xl bg-black/40 border border-white/5 flex items-center justify-between gap-2"
              >
                <div className="flex items-center gap-2.5 min-w-0">
                  <div className="w-7 h-7 rounded-xl bg-slate-800 border border-white/10 flex items-center justify-center text-xs font-bold text-slate-300 shrink-0">
                    {m.usuario?.nombre.substring(0, 2).toUpperCase() || 'US'}
                  </div>
                  <div className="min-w-0">
                    <div className="flex items-center gap-1.5">
                      <h4 className="text-xs font-bold text-white truncate">
                        {m.usuario?.nombre}
                      </h4>
                      {isMe && (
                        <span className="text-[9px] font-bold text-emerald-400 bg-emerald-950/60 border border-emerald-500/20 px-1 rounded">
                          Tú
                        </span>
                      )}
                    </div>
                    <p className="text-[10px] text-slate-400 truncate">{m.usuario?.email}</p>
                  </div>
                </div>

                {/* Role indicator / selector */}
                <div className="shrink-0 flex items-center gap-1.5">
                  {isAdmin && !isMe ? (
                    <select
                      value={m.rol}
                      onChange={(e) =>
                        cambiarRolMiembro(
                          m.id_usuario,
                          activeGrupo.id_grupo,
                          e.target.value as RolUsuarioGrupo
                        )
                      }
                      className="text-[11px] py-1 px-2 rounded-xl bg-black/60 border border-white/10 text-emerald-300 font-semibold"
                    >
                      <option value="administrador" className="bg-[#0e1612]">Admin</option>
                      <option value="miembro" className="bg-[#0e1612]">Miembro</option>
                    </select>
                  ) : (
                    <span
                      className={`text-[10px] font-semibold px-2 py-0.5 rounded-xl border flex items-center gap-1 ${
                        m.rol === 'administrador'
                          ? 'bg-emerald-950/80 border-emerald-500/30 text-emerald-300'
                          : 'bg-black/40 border-white/10 text-slate-400'
                      }`}
                    >
                      <Shield className="w-2.5 h-2.5" />
                      {m.rol === 'administrador' ? 'Admin' : 'Miembro'}
                    </span>
                  )}
                </div>
              </div>
            );
          })}
        </div>

        {/* Pending Invitations list */}
        {pendingInvitations.length > 0 && (
          <div className="mt-3 pt-3 border-t border-white/10">
            <span className="text-[11px] font-semibold text-slate-400 block mb-1.5">
              Invitaciones Pendientes:
            </span>
            <div className="space-y-1.5">
              {pendingInvitations.map((inv) => {
                const target = usuarios.find((u) => u.id_usuario === inv.id_usuario_destino);
                const isForMe = inv.id_usuario_destino === currentUsuario.id_usuario;

                return (
                  <div
                    key={inv.id_invitacion}
                    className="p-2.5 rounded-2xl bg-amber-950/20 border border-amber-500/20 flex items-center justify-between text-xs"
                  >
                    <div>
                      <span className="text-white font-medium block">
                        {target?.nombre || 'Usuario'}
                      </span>
                      <span className="text-[10px] text-amber-300/80">Esperando respuesta</span>
                    </div>

                    {isForMe && (
                      <div className="flex items-center gap-1.5">
                        <button
                          onClick={() => responderInvitacion(inv.id_invitacion, 'aceptada')}
                          className="py-1 px-2.5 rounded-xl bg-emerald-600 text-white font-bold text-xs flex items-center gap-1"
                        >
                          <Check className="w-3 h-3 stroke-[3]" />
                          Aceptar
                        </button>
                        <button
                          onClick={() => responderInvitacion(inv.id_invitacion, 'rechazada')}
                          className="py-1 px-2 rounded-xl bg-black/40 text-rose-300 border border-white/10 text-xs"
                        >
                          <X className="w-3 h-3" />
                        </button>
                      </div>
                    )}
                  </div>
                );
              })}
            </div>
          </div>
        )}
      </div>

      {/* Notifications & System Settings */}
      <div className="p-4 rounded-3xl bento-card border border-white/10 space-y-2.5">
        <h3 className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">
          Ajustes del Sistema
        </h3>

        {/* Notificaciones button */}
        <button
          onClick={() => setShowNotifsModal(true)}
          className="w-full p-3 rounded-2xl bg-black/40 border border-white/5 hover:border-emerald-500/20 flex items-center justify-between transition-colors"
        >
          <div className="flex items-center gap-3">
            <div className="p-2 rounded-xl bg-emerald-950/60 border border-emerald-500/20 text-emerald-400">
              <Bell className="w-4 h-4" />
            </div>
            <div className="text-left">
              <span className="text-xs font-bold text-white block">Centro de Notificaciones</span>
              <span className="text-[10px] text-slate-400">
                {unreadNotifsCount > 0
                  ? `${unreadNotifsCount} alertas pendientes`
                  : 'Todas al día'}
              </span>
            </div>
          </div>
          <ChevronRight className="w-4 h-4 text-slate-500" />
        </button>

        {/* Ubicación para ofertas */}
        <div className="p-3 rounded-2xl bg-black/40 border border-white/5 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="p-2 rounded-xl bg-black/60 border border-white/10 text-teal-400">
              <MapPin className="w-4 h-4" />
            </div>
            <div>
              <span className="text-xs font-bold text-white block">Zona de Ofertas</span>
              <span className="text-[10px] text-slate-400">
                {userLocation.localidad}, {userLocation.provincia}
              </span>
            </div>
          </div>
          <button
            onClick={() => {
              setUserLocation({
                localidad: 'Córdoba Capital',
                provincia: 'Córdoba',
              });
              alert('Ubicación actualizada con éxito.');
            }}
            className="text-[11px] text-emerald-400 hover:text-emerald-300 font-semibold py-1 px-2.5 rounded-xl bg-emerald-950/60 border border-emerald-500/20"
          >
            Actualizar
          </button>
        </div>

        {/* Reset Database demo */}
        <button
          onClick={() => {
            if (
              confirm(
                '¿Deseas restaurar la base de datos a los valores iniciales de demostración?'
              )
            ) {
              resetDatabaseToDefaults();
            }
          }}
          className="w-full p-3 rounded-2xl bg-black/40 border border-white/5 hover:border-amber-500/30 flex items-center justify-between transition-colors text-slate-300 hover:text-amber-300"
        >
          <div className="flex items-center gap-3">
            <div className="p-2 rounded-xl bg-amber-950/40 border border-amber-500/20 text-amber-400">
              <RotateCcw className="w-4 h-4" />
            </div>
            <div className="text-left">
              <span className="text-xs font-bold text-white block">Restaurar Datos Iniciales</span>
              <span className="text-[10px] text-slate-400">Reiniciar listas y movimientos</span>
            </div>
          </div>
          <ChevronRight className="w-4 h-4 text-slate-500" />
        </button>

        {/* Logout */}
        <button
          onClick={() => {
            alert('Has cerrado sesión en Canasta+ de manera segura.');
          }}
          className="w-full p-3 rounded-2xl bg-rose-950/20 border border-rose-500/20 hover:border-rose-500/30 flex items-center justify-between transition-colors text-rose-300"
        >
          <div className="flex items-center gap-3">
            <div className="p-2 rounded-xl bg-rose-950/60 border border-rose-500/20 text-rose-400">
              <LogOut className="w-4 h-4" />
            </div>
            <div className="text-left">
              <span className="text-xs font-bold text-rose-200 block">Cerrar Sesión</span>
              <span className="text-[10px] text-rose-300/70">Finalizar sesión en este dispositivo</span>
            </div>
          </div>
        </button>
      </div>

      {/* Edit Profile Modal */}
      {showEditProfile && (
        <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/80 backdrop-blur-md">
          <div className="w-full max-w-md bg-[#0c120e] border-t sm:border border-white/10 rounded-t-3xl sm:rounded-3xl p-5 shadow-2xl max-h-[88vh] overflow-y-auto">
            <div className="w-10 h-1 bg-slate-700/60 rounded-full mx-auto mb-3 sm:hidden" />
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-base font-bold text-white">Editar Perfil</h3>
              <button
                onClick={() => setShowEditProfile(false)}
                className="p-1.5 rounded-xl text-slate-400 hover:text-white"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveProfile} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Nombre</label>
                <input
                  type="text"
                  required
                  value={nombre}
                  onChange={(e) => setNombre(e.target.value)}
                  className="w-full py-2 px-3 rounded-2xl glass-input text-xs"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Email</label>
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="w-full py-2 px-3 rounded-2xl glass-input text-xs"
                />
              </div>

              <div className="grid grid-cols-2 gap-2.5">
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">
                    Localidad
                  </label>
                  <input
                    type="text"
                    value={localidad}
                    onChange={(e) => setLocalidad(e.target.value)}
                    className="w-full py-2 px-3 rounded-2xl glass-input text-xs"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">
                    Provincia
                  </label>
                  <input
                    type="text"
                    value={provincia}
                    onChange={(e) => setProvincia(e.target.value)}
                    className="w-full py-2 px-3 rounded-2xl glass-input text-xs"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">
                  Domicilio
                </label>
                <input
                  type="text"
                  value={domicilio}
                  onChange={(e) => setDomicilio(e.target.value)}
                  className="w-full py-2 px-3 rounded-2xl glass-input text-xs"
                />
              </div>

              <div className="flex gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setShowEditProfile(false)}
                  className="flex-1 py-2 px-4 rounded-2xl border border-white/10 text-slate-300 text-xs"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2 px-4 rounded-2xl bg-emerald-600 text-white font-bold text-xs shadow-md"
                >
                  Guardar Cambios
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Invite Member Modal */}
      {showInviteModal && <InviteGroupModal onClose={() => setShowInviteModal(false)} />}

      {/* Notifications Modal */}
      {showNotifsModal && (
        <NotificationsModal onClose={() => setShowNotifsModal(false)} />
      )}
    </div>
  );
};

