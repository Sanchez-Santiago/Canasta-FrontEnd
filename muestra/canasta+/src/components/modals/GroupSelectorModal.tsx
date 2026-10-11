import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { Users, Plus, Check, X, Shield, Sparkles } from 'lucide-react';

interface GroupSelectorModalProps {
  onClose: () => void;
}

export const GroupSelectorModal: React.FC<GroupSelectorModalProps> = ({ onClose }) => {
  const {
    userGrupalGrupos,
    activeGrupoId,
    setActiveGrupoId,
    usuarioGrupos,
    currentUsuarioId,
    crearGrupo,
  } = useApp();

  const [isCreating, setIsCreating] = useState(false);
  const [nombre, setNombre] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [categoria, setCategoria] = useState('Hogar');
  const [presupuesto, setPresupuesto] = useState('350000');

  const handleCreate = (e: React.FormEvent) => {
    e.preventDefault();
    if (!nombre.trim()) return;
    const parsedBudget = parseInt(presupuesto.replace(/\D/g, ''), 10) || 300000;
    crearGrupo(nombre.trim(), descripcion.trim(), categoria, parsedBudget);
    setIsCreating(false);
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/70 backdrop-blur-sm animate-in fade-in duration-200">
      <div className="w-full max-w-md bg-[#0e1612] border-t sm:border border-emerald-500/30 rounded-t-3xl sm:rounded-2xl p-5 shadow-2xl max-h-[85vh] overflow-y-auto">
        {/* Drag handle */}
        <div className="w-10 h-1 bg-slate-700/60 rounded-full mx-auto mb-4" />

        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2">
            <div className="p-2 rounded-xl bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
              <Users className="w-5 h-5" />
            </div>
            <div>
              <h3 className="text-base font-bold text-white">Seleccionar Grupo Grupal</h3>
              <p className="text-xs text-slate-400">Elige con qué equipo o familia trabajar</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-white/10"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {!isCreating ? (
          <>
            <div className="space-y-2.5 mb-5">
              {userGrupalGrupos.map((grp) => {
                const isSelected = grp.id_grupo === activeGrupoId;
                const userRel = usuarioGrupos.find(
                  (ug) => ug.id_grupo === grp.id_grupo && ug.id_usuario === currentUsuarioId
                );
                const memberCount = usuarioGrupos.filter((ug) => ug.id_grupo === grp.id_grupo).length;

                return (
                  <button
                    key={grp.id_grupo}
                    onClick={() => {
                      setActiveGrupoId(grp.id_grupo);
                      onClose();
                    }}
                    className={`w-full text-left p-3.5 rounded-2xl border transition-all flex items-center justify-between ${
                      isSelected
                        ? 'bg-emerald-950/70 border-emerald-500/60 shadow-lg shadow-emerald-950/60'
                        : 'bg-black/40 border-white/10 hover:border-emerald-500/30 hover:bg-emerald-950/20'
                    }`}
                  >
                    <div className="flex-1 pr-3">
                      <div className="flex items-center gap-2 mb-1">
                        <span className="font-semibold text-sm text-white">{grp.nombre}</span>
                        {userRel?.rol === 'administrador' && (
                          <span className="inline-flex items-center gap-1 text-[10px] font-medium text-emerald-300 bg-emerald-900/60 border border-emerald-500/40 px-1.5 py-0.2 rounded">
                            <Shield className="w-2.5 h-2.5" />
                            Admin
                          </span>
                        )}
                      </div>
                      <p className="text-xs text-slate-400 line-clamp-1 mb-1.5">{grp.descripcion}</p>
                      <div className="flex items-center gap-2 text-[11px] text-slate-400">
                        <span>{grp.categoria || 'Grupo'}</span>
                        <span aria-hidden="true">·</span>
                        <span>{memberCount} integrantes</span>
                        <span aria-hidden="true">·</span>
                        <span className="tabular-nums font-mono text-emerald-400/90">
                          ${(grp.presupuesto_mensual || 0).toLocaleString()} ppto
                        </span>
                      </div>
                    </div>

                    <div className="shrink-0">
                      {isSelected ? (
                        <div className="w-6 h-6 rounded-full bg-emerald-500 text-black flex items-center justify-center shadow-md">
                          <Check className="w-4 h-4 stroke-[3]" />
                        </div>
                      ) : (
                        <div className="w-6 h-6 rounded-full border border-slate-700" />
                      )}
                    </div>
                  </button>
                );
              })}
            </div>

            <button
              onClick={() => setIsCreating(true)}
              className="w-full py-3 px-4 rounded-xl border border-dashed border-emerald-500/40 bg-emerald-950/30 hover:bg-emerald-950/60 text-emerald-300 text-sm font-semibold flex items-center justify-center gap-2 transition-all shadow-sm"
            >
              <Plus className="w-4 h-4" />
              <span>Crear Nuevo Grupo</span>
            </button>
          </>
        ) : (
          <form onSubmit={handleCreate} className="space-y-3.5">
            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">
                Nombre del Grupo *
              </label>
              <input
                type="text"
                required
                placeholder="Ej. Casa & Familia, Viaje Vacaciones"
                value={nombre}
                onChange={(e) => setNombre(e.target.value)}
                className="w-full py-2.5 px-3 rounded-xl glass-input text-sm"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">
                Descripción
              </label>
              <input
                type="text"
                placeholder="Ej. Gastos compartidos de convivencia"
                value={descripcion}
                onChange={(e) => setDescripcion(e.target.value)}
                className="w-full py-2.5 px-3 rounded-xl glass-input text-sm"
              />
            </div>

            <div className="grid grid-cols-2 gap-2.5">
              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1">
                  Categoría
                </label>
                <select
                  value={categoria}
                  onChange={(e) => setCategoria(e.target.value)}
                  className="w-full py-2.5 px-2.5 rounded-xl glass-input text-sm"
                >
                  <option value="Hogar" className="bg-[#0e1612]">Hogar</option>
                  <option value="Convivencia" className="bg-[#0e1612]">Convivencia</option>
                  <option value="Social" className="bg-[#0e1612]">Social / Eventos</option>
                  <option value="Viajes" className="bg-[#0e1612]">Viajes</option>
                  <option value="Trabajo" className="bg-[#0e1612]">Trabajo</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1">
                  Presupuesto Mensual ($)
                </label>
                <input
                  type="number"
                  min="0"
                  step="1000"
                  value={presupuesto}
                  onChange={(e) => setPresupuesto(e.target.value)}
                  className="w-full py-2.5 px-3 rounded-xl glass-input text-sm tabular-nums"
                />
              </div>
            </div>

            <div className="flex gap-2.5 pt-2">
              <button
                type="button"
                onClick={() => setIsCreating(false)}
                className="flex-1 py-2.5 px-4 rounded-xl border border-white/10 text-slate-300 text-sm font-medium hover:bg-white/5"
              >
                Volver
              </button>
              <button
                type="submit"
                className="flex-1 py-2.5 px-4 rounded-xl bg-gradient-to-r from-emerald-500 to-teal-600 text-black font-semibold text-sm hover:from-emerald-400 hover:to-teal-500 shadow-lg shadow-emerald-950/60"
              >
                Guardar Grupo
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};
