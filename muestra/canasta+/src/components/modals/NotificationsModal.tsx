import React from 'react';
import { useApp } from '../../context/AppContext';
import { Bell, CheckCheck, Tag, ShoppingCart, Receipt, Users, AlertCircle, X } from 'lucide-react';

interface NotificationsModalProps {
  onClose: () => void;
}

export const NotificationsModal: React.FC<NotificationsModalProps> = ({ onClose }) => {
  const { notificaciones, marcarNotificacionesLeidas, activeGrupo } = useApp();

  const getIcon = (tipo: string) => {
    switch (tipo) {
      case 'oferta':
        return <Tag className="w-4 h-4 text-emerald-400" />;
      case 'lista':
        return <ShoppingCart className="w-4 h-4 text-teal-400" />;
      case 'movimiento':
        return <Receipt className="w-4 h-4 text-cyan-400" />;
      case 'invitacion':
        return <Users className="w-4 h-4 text-amber-400" />;
      default:
        return <AlertCircle className="w-4 h-4 text-emerald-400" />;
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/75 backdrop-blur-sm animate-in fade-in duration-200">
      <div className="w-full max-w-md bg-[#0e1612] border-t sm:border border-emerald-500/30 rounded-t-3xl sm:rounded-2xl p-5 shadow-2xl max-h-[85vh] flex flex-col">
        {/* Drag handle */}
        <div className="w-10 h-1 bg-slate-700/60 rounded-full mx-auto mb-3" />

        <div className="flex items-center justify-between mb-3 shrink-0">
          <div className="flex items-center gap-2">
            <div className="p-2 rounded-xl bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
              <Bell className="w-5 h-5" />
            </div>
            <div>
              <h3 className="text-base font-bold text-white">Notificaciones</h3>
              <p className="text-xs text-slate-400">Actividad de {activeGrupo.nombre}</p>
            </div>
          </div>

          <div className="flex items-center gap-1.5">
            <button
              onClick={marcarNotificacionesLeidas}
              className="p-1.5 rounded-lg text-emerald-400 hover:text-emerald-300 hover:bg-emerald-950/40 text-xs font-medium flex items-center gap-1"
              title="Marcar todo como leído"
            >
              <CheckCheck className="w-4 h-4" />
              <span className="text-[11px]">Leídas</span>
            </button>
            <button
              onClick={onClose}
              className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-white/10"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        <div className="flex-1 overflow-y-auto space-y-2.5 pr-0.5 py-1">
          {notificaciones.length === 0 ? (
            <div className="text-center py-10 text-slate-500 text-xs">
              No tienes notificaciones pendientes.
            </div>
          ) : (
            notificaciones.map((notif) => (
              <div
                key={notif.id_notificacion}
                className={`p-3 rounded-2xl border transition-all ${
                  notif.leida
                    ? 'bg-black/30 border-white/5 text-slate-400'
                    : 'bg-emerald-950/30 border-emerald-500/40 text-slate-200 shadow-sm'
                }`}
              >
                <div className="flex items-start gap-2.5">
                  <div className="p-2 rounded-xl bg-slate-900 border border-white/10 shrink-0 mt-0.5">
                    {getIcon(notif.tipo)}
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center justify-between gap-2 mb-0.5">
                      <h4 className="text-xs font-semibold text-white truncate">
                        {notif.titulo}
                      </h4>
                      {!notif.leida && (
                        <span className="w-2 h-2 rounded-full bg-emerald-400 shrink-0 shadow-[0_0_6px_rgba(52,211,153,0.8)]" />
                      )}
                    </div>
                    {notif.descripcion && (
                      <p className="text-xs text-slate-300/90 leading-relaxed mb-1">
                        {notif.descripcion}
                      </p>
                    )}
                    <span className="text-[10px] text-slate-400 font-mono">
                      {notif.fecha_creacion}
                    </span>
                  </div>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};
