import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { Users, User, ChevronDown, Bell, Plus, Check, Shield, UserPlus } from 'lucide-react';
import { GroupSelectorModal } from '../modals/GroupSelectorModal';
import { NotificationsModal } from '../modals/NotificationsModal';
import { InviteGroupModal } from '../modals/InviteGroupModal';

export const TopHeader: React.FC = () => {
  const {
    modoGrupo,
    setModoGrupo,
    activeGrupo,
    userGrupalGrupos,
    unreadNotifsCount,
    currentUsuario,
  } = useApp();

  const [showGroupModal, setShowGroupModal] = useState(false);
  const [showNotifsModal, setShowNotifsModal] = useState(false);
  const [showInviteModal, setShowInviteModal] = useState(false);

  return (
    <>
      <header className="sticky top-0 z-30 w-full glass-panel border-b border-white/[0.08] backdrop-blur-2xl px-4 py-2.5 transition-all">
        {/* Brand Row & Notification Action */}
        <div className="flex items-center justify-between mb-2">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-xl bg-gradient-to-br from-emerald-500/80 via-emerald-700/80 to-teal-900 flex items-center justify-center shadow-lg shadow-black/60 border border-emerald-400/25">
              <span className="text-white font-extrabold text-sm tracking-tighter">C+</span>
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="text-sm font-bold tracking-tight text-white">Canasta+</span>
                <span className="text-[10px] font-semibold text-emerald-400 bg-emerald-950/70 border border-emerald-500/20 px-1.5 py-0.2 rounded-md">
                  {modoGrupo === 'personal' ? 'Personal' : 'Grupal'}
                </span>
              </div>
              <p className="text-[10px] text-slate-400 truncate max-w-[170px]">
                {activeGrupo.nombre}
              </p>
            </div>
          </div>

          {/* Right Action: Notifications & Avatar */}
          <div className="flex items-center gap-2">
            <button
              onClick={() => setShowNotifsModal(true)}
              aria-label="Ver notificaciones"
              className="relative p-2 rounded-xl bg-black/40 border border-white/10 hover:border-emerald-500/30 text-slate-300 hover:text-emerald-300 transition-colors"
            >
              <Bell className="w-4 h-4" />
              {unreadNotifsCount > 0 && (
                <span className="absolute -top-1 -right-1 w-4 h-4 rounded-full bg-emerald-500 text-black text-[9px] font-bold flex items-center justify-center animate-pulse">
                  {unreadNotifsCount}
                </span>
              )}
            </button>

            <div
              className="w-8 h-8 rounded-xl bg-emerald-950/70 border border-emerald-500/25 flex items-center justify-center text-xs font-bold text-emerald-300"
              title={currentUsuario.nombre}
            >
              {currentUsuario.nombre.substring(0, 2).toUpperCase()}
            </div>
          </div>
        </div>

        {/* Persistent Switch Row: Individual vs Grupal + Extra Group Selector + Botón Invitar */}
        <div className="flex items-center gap-1.5">
          {/* Segmented Switch: Individual vs Grupal */}
          <div className="flex-1 p-0.5 bg-black/50 rounded-xl border border-white/10 flex items-center shadow-inner">
            <button
              onClick={() => setModoGrupo('personal')}
              className={`flex-1 flex items-center justify-center gap-1 py-1.5 px-2 rounded-lg text-xs font-semibold transition-all ${
                modoGrupo === 'personal'
                  ? 'bg-gradient-to-r from-emerald-700/80 to-teal-800/80 text-white shadow-sm border border-emerald-400/20'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <User className="w-3.5 h-3.5" />
              <span>Individual</span>
            </button>

            <button
              onClick={() => setModoGrupo('grupal')}
              className={`flex-1 flex items-center justify-center gap-1 py-1.5 px-2 rounded-lg text-xs font-semibold transition-all ${
                modoGrupo === 'grupal'
                  ? 'bg-gradient-to-r from-emerald-700/80 to-teal-800/80 text-white shadow-sm border border-emerald-400/20'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <Users className="w-3.5 h-3.5" />
              <span>Grupal</span>
            </button>
          </div>

          {/* Botones extras al estar en modo grupal: Selector de Grupo + Botón Invitar */}
          {modoGrupo === 'grupal' && (
            <>
              <button
                onClick={() => setShowGroupModal(true)}
                className="flex items-center gap-1 py-1.5 px-2.5 rounded-xl bg-black/40 border border-white/10 text-slate-200 text-xs font-semibold hover:border-emerald-500/30 transition-all shadow-sm shrink-0"
                title="Cambiar grupo"
              >
                <span className="max-w-[70px] truncate">{activeGrupo.nombre}</span>
                <ChevronDown className="w-3.5 h-3.5 text-slate-400" />
              </button>

              <button
                onClick={() => setShowInviteModal(true)}
                className="flex items-center gap-1 py-1.5 px-2.5 rounded-xl bg-emerald-950/70 border border-emerald-500/40 text-emerald-300 text-xs font-bold hover:bg-emerald-900/80 hover:border-emerald-400 transition-all shadow-sm shrink-0"
                title="Enviar invitación al grupo"
              >
                <UserPlus className="w-3.5 h-3.5" />
                <span>Invitar</span>
              </button>
            </>
          )}
        </div>
      </header>

      {/* Modals */}
      {showGroupModal && (
        <GroupSelectorModal onClose={() => setShowGroupModal(false)} />
      )}
      {showNotifsModal && (
        <NotificationsModal onClose={() => setShowNotifsModal(false)} />
      )}
      {showInviteModal && (
        <InviteGroupModal onClose={() => setShowInviteModal(false)} />
      )}
    </>
  );
};
