import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { RolUsuarioGrupo } from '../../types/database';
import {
  UserPlus,
  Copy,
  Check,
  X,
  Mail,
  Shield,
  Share2,
  Users,
  Sparkles,
  Link2,
} from 'lucide-react';

interface InviteGroupModalProps {
  onClose: () => void;
}

export const InviteGroupModal: React.FC<InviteGroupModalProps> = ({ onClose }) => {
  const {
    activeGrupo,
    currentUsuario,
    usuarios,
    usuarioGrupos,
    invitarUsuarioAGrupo,
  } = useApp();

  const [method, setMethod] = useState<'usuario' | 'enlace'>('usuario');
  const [selectedUserId, setSelectedUserId] = useState<number>(() => {
    // Buscar un usuario que no esté todavía en el grupo
    const existingIds = new Set(
      usuarioGrupos.filter((ug) => ug.id_grupo === activeGrupo.id_grupo).map((ug) => ug.id_usuario)
    );
    const candidate = usuarios.find((u) => !existingIds.has(u.id_usuario));
    return candidate ? candidate.id_usuario : 2;
  });
  const [rol, setRol] = useState<RolUsuarioGrupo>('miembro');
  const [customEmail, setCustomEmail] = useState('');
  const [copiedLink, setCopiedLink] = useState(false);
  const [sentSuccess, setSentSuccess] = useState(false);

  const inviteCode = `CAN-${activeGrupo.nombre.substring(0, 3).toUpperCase()}-${activeGrupo.id_grupo * 147}`;
  const inviteLink = `https://canasta.app/join/${activeGrupo.id_grupo}?code=${inviteCode}`;

  const handleCopyLink = () => {
    navigator.clipboard?.writeText(inviteLink);
    setCopiedLink(true);
    setTimeout(() => setCopiedLink(false), 2200);
  };

  const handleSendInvite = (e: React.FormEvent) => {
    e.preventDefault();
    invitarUsuarioAGrupo(selectedUserId, activeGrupo.id_grupo);
    setSentSuccess(true);
    setTimeout(() => {
      onClose();
    }, 1500);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/80 backdrop-blur-md animate-in fade-in duration-200">
      <div className="w-full max-w-md bg-[#0c120e] border-t sm:border border-white/10 rounded-t-3xl sm:rounded-3xl p-5 shadow-2xl max-h-[90vh] overflow-y-auto">
        {/* Drag handle */}
        <div className="w-10 h-1 bg-slate-700/60 rounded-full mx-auto mb-3 sm:hidden" />

        {/* Modal Header */}
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2.5">
            <div className="w-9 h-9 rounded-2xl bg-emerald-950/80 border border-emerald-500/30 text-emerald-400 flex items-center justify-center shadow-inner">
              <UserPlus className="w-4 h-4" />
            </div>
            <div>
              <h3 className="text-base font-bold text-white tracking-tight">Invitar al Grupo</h3>
              <p className="text-xs text-slate-400 truncate max-w-[200px]">{activeGrupo.nombre}</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-xl text-slate-400 hover:text-white hover:bg-white/10 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Method Selector Tabs */}
        <div className="flex items-center p-1 bg-black/50 rounded-2xl border border-white/10 mb-4">
          <button
            type="button"
            onClick={() => setMethod('usuario')}
            className={`flex-1 py-1.5 text-xs font-semibold rounded-xl transition-all ${
              method === 'usuario'
                ? 'bg-emerald-950/90 text-emerald-300 border border-emerald-500/40 shadow-sm'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            Invitar Usuario
          </button>
          <button
            type="button"
            onClick={() => setMethod('enlace')}
            className={`flex-1 py-1.5 text-xs font-semibold rounded-xl transition-all ${
              method === 'enlace'
                ? 'bg-emerald-950/90 text-emerald-300 border border-emerald-500/40 shadow-sm'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            Enlace y Código
          </button>
        </div>

        {sentSuccess ? (
          <div className="p-6 text-center rounded-2xl bg-emerald-950/40 border border-emerald-500/40 my-3">
            <div className="w-12 h-12 rounded-full bg-emerald-500/20 text-emerald-400 flex items-center justify-center mx-auto mb-2 border border-emerald-500/30">
              <Check className="w-6 h-6 stroke-[3]" />
            </div>
            <h4 className="text-sm font-bold text-white mb-1">¡Invitación Enviada con Éxito!</h4>
            <p className="text-xs text-slate-300">
              El integrante recibirá la notificación para unirse a {activeGrupo.nombre}.
            </p>
          </div>
        ) : method === 'usuario' ? (
          <form onSubmit={handleSendInvite} className="space-y-3.5">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">
                Seleccionar integrante *
              </label>
              <select
                value={selectedUserId}
                onChange={(e) => setSelectedUserId(Number(e.target.value))}
                className="w-full py-2.5 px-3 rounded-2xl glass-input text-xs font-medium"
              >
                {usuarios
                  .filter((u) => u.id_usuario !== currentUsuario.id_usuario)
                  .map((u) => (
                    <option key={u.id_usuario} value={u.id_usuario} className="bg-[#0c120e]">
                      {u.nombre} ({u.email})
                    </option>
                  ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">
                Rol en el grupo
              </label>
              <div className="grid grid-cols-2 gap-2">
                <button
                  type="button"
                  onClick={() => setRol('miembro')}
                  className={`p-2.5 rounded-2xl text-left border transition-all ${
                    rol === 'miembro'
                      ? 'bg-emerald-950/80 border-emerald-500/50 text-white shadow-sm'
                      : 'bg-black/40 border-white/5 text-slate-400 hover:text-white'
                  }`}
                >
                  <span className="text-xs font-bold block mb-0.5">Miembro</span>
                  <span className="text-[10px] text-slate-400 block leading-tight">
                    Carga productos y gastos del día a día
                  </span>
                </button>

                <button
                  type="button"
                  onClick={() => setRol('administrador')}
                  className={`p-2.5 rounded-2xl text-left border transition-all ${
                    rol === 'administrador'
                      ? 'bg-emerald-950/80 border-emerald-500/50 text-white shadow-sm'
                      : 'bg-black/40 border-white/5 text-slate-400 hover:text-white'
                  }`}
                >
                  <span className="text-xs font-bold block mb-0.5">Administrador</span>
                  <span className="text-[10px] text-slate-400 block leading-tight">
                    Control total de miembros y presupuestos
                  </span>
                </button>
              </div>
            </div>

            <div className="p-3 rounded-2xl bg-black/40 border border-white/5 text-xs text-slate-400">
              <span className="text-slate-300 font-semibold block mb-0.5">
                Permisos compartidos:
              </span>
              Los miembros de {activeGrupo.nombre} podrán ver el presupuesto común, editar listas
              y cargar compras simultáneamente.
            </div>

            <div className="flex gap-2.5 pt-2">
              <button
                type="button"
                onClick={onClose}
                className="flex-1 py-2.5 px-4 rounded-2xl border border-white/10 text-slate-300 text-xs font-medium hover:bg-white/5"
              >
                Cancelar
              </button>
              <button
                type="submit"
                className="flex-1 py-2.5 px-4 rounded-2xl bg-gradient-to-r from-emerald-600 to-teal-700 text-white font-bold text-xs hover:from-emerald-500 hover:to-teal-600 shadow-lg shadow-emerald-950/60"
              >
                Enviar Invitación
              </button>
            </div>
          </form>
        ) : (
          <div className="space-y-3.5">
            {/* Copyable Link */}
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">
                Enlace Directo de Invitación
              </label>
              <div className="flex items-center gap-1.5">
                <div className="flex-1 py-2 px-3 rounded-2xl glass-input text-xs font-mono truncate text-slate-300">
                  {inviteLink}
                </div>
                <button
                  type="button"
                  onClick={handleCopyLink}
                  className={`py-2 px-3 rounded-2xl font-bold text-xs flex items-center gap-1.5 transition-all ${
                    copiedLink
                      ? 'bg-emerald-500 text-black'
                      : 'bg-emerald-950/80 border border-emerald-500/40 text-emerald-300 hover:bg-emerald-900/80'
                  }`}
                >
                  {copiedLink ? <Check className="w-3.5 h-3.5" /> : <Copy className="w-3.5 h-3.5" />}
                  <span>{copiedLink ? 'Copiado' : 'Copiar'}</span>
                </button>
              </div>
            </div>

            {/* Code */}
            <div className="p-3.5 rounded-2xl bg-black/50 border border-white/5 flex items-center justify-between">
              <div>
                <span className="text-[10px] text-slate-400 block">Código del Grupo:</span>
                <span className="text-sm font-mono font-bold text-emerald-400 tracking-wider">
                  {inviteCode}
                </span>
              </div>
              <button
                onClick={() => {
                  navigator.clipboard?.writeText(inviteCode);
                  setCopiedLink(true);
                  setTimeout(() => setCopiedLink(false), 2000);
                }}
                className="text-xs text-slate-300 hover:text-white p-2 rounded-xl bg-white/5"
              >
                Copiar Código
              </button>
            </div>

            <p className="text-[11px] text-slate-400 leading-relaxed">
              Cualquier persona con este enlace o código podrá solicitar unirse a {activeGrupo.nombre}.
            </p>

            <button
              onClick={onClose}
              className="w-full py-2.5 px-4 rounded-2xl border border-white/10 text-slate-300 text-xs font-medium hover:bg-white/5"
            >
              Listo
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
