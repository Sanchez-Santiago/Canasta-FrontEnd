import React, { useState, useMemo } from 'react';
import { useApp } from '../../context/AppContext';
import {
  Receipt,
  Plus,
  ArrowUpRight,
  ArrowDownLeft,
  Filter,
  Calendar,
  Search,
  Trash2,
  Image,
  ChevronDown,
} from 'lucide-react';
import { AddMovementModal } from '../modals/AddMovementModal';

export const HistorialScreen: React.FC = () => {
  const { activeMovimientos, usuarios, eliminarMovimiento, activeGrupo } = useApp();

  const [showAddModal, setShowAddModal] = useState(false);
  const [filtroTipo, setFiltroTipo] = useState<'todos' | 'gasto' | 'ingreso'>('todos');
  const [filtroFecha, setFiltroFecha] = useState<'mes' | '30dias' | 'anio' | 'todos'>('mes');
  const [filtroCategoria, setFiltroCategoria] = useState<string>('todas');
  const [searchQuery, setSearchQuery] = useState('');

  // Extract all categories present
  const availableCategories = useMemo(() => {
    const cats = new Set<string>();
    activeMovimientos.forEach((m) => {
      if (m.categoria) cats.add(m.categoria);
    });
    return Array.from(cats);
  }, [activeMovimientos]);

  // Filter logic
  const movimientosFiltrados = useMemo(() => {
    const now = new Date();
    const currentYear = now.getFullYear();
    const currentMonth = now.getMonth();

    return activeMovimientos.filter((m) => {
      // Filtro tipo
      if (filtroTipo !== 'todos' && m.tipo !== filtroTipo) return false;

      // Filtro categoría
      if (filtroCategoria !== 'todas' && m.categoria !== filtroCategoria) return false;

      // Filtro búsqueda
      if (searchQuery.trim()) {
        const query = searchQuery.toLowerCase();
        const matchName = m.nombre.toLowerCase().includes(query);
        const matchDesc = m.descripcion?.toLowerCase().includes(query);
        const matchCat = m.categoria?.toLowerCase().includes(query);
        if (!matchName && !matchDesc && !matchCat) return false;
      }

      // Filtro fecha
      const mDate = new Date(m.fecha_movimiento);
      if (filtroFecha === 'mes') {
        if (mDate.getFullYear() !== currentYear || mDate.getMonth() !== currentMonth) {
          return false;
        }
      } else if (filtroFecha === '30dias') {
        const diffDays = (now.getTime() - mDate.getTime()) / (1000 * 3600 * 24);
        if (diffDays > 30) return false;
      } else if (filtroFecha === 'anio') {
        if (mDate.getFullYear() !== currentYear) return false;
      }

      return true;
    });
  }, [activeMovimientos, filtroTipo, filtroCategoria, filtroFecha, searchQuery]);

  // Balance metrics for filtered items
  const totalIngresos = useMemo(() => {
    return movimientosFiltrados
      .filter((m) => m.tipo === 'ingreso')
      .reduce((sum, m) => sum + m.monto, 0);
  }, [movimientosFiltrados]);

  const totalGastos = useMemo(() => {
    return movimientosFiltrados
      .filter((m) => m.tipo === 'gasto')
      .reduce((sum, m) => sum + m.monto, 0);
  }, [movimientosFiltrados]);

  const balanceNeto = totalIngresos - totalGastos;

  return (
    <div className="space-y-4 pb-20 relative min-h-[calc(100vh-14rem)]">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-extrabold text-white tracking-tight flex items-center gap-2">
            <span>Historial Financiero</span>
            <span className="text-xs px-2 py-0.5 rounded-full bg-emerald-500/20 text-emerald-300 font-mono">
              {movimientosFiltrados.length}
            </span>
          </h2>
          <p className="text-xs text-slate-400">
            {activeGrupo.nombre} · Registro de ingresos y egresos
          </p>
        </div>

        <button
          onClick={() => setShowAddModal(true)}
          className="py-2 px-3 rounded-xl bg-gradient-to-r from-emerald-500 to-teal-600 text-black text-xs font-bold flex items-center gap-1.5 shadow-md shadow-emerald-950/60 hover:from-emerald-400 hover:to-teal-500 transition-all active:scale-95"
        >
          <Plus className="w-4 h-4 stroke-[2.5]" />
          <span>Registrar</span>
        </button>
      </div>

      {/* Filter Balance KPI Card */}
      <div className="p-4 rounded-3xl bento-card border border-white/10 shadow-lg">
        <div className="flex items-center justify-between mb-3 text-xs">
          <span className="text-slate-400 font-medium">Balance del Período</span>
          <span
            className={`font-mono font-extrabold text-sm tabular-nums ${
              balanceNeto >= 0 ? 'text-emerald-300' : 'text-rose-400'
            }`}
          >
            {balanceNeto >= 0 ? '+' : '-'}${Math.abs(balanceNeto).toLocaleString()}
          </span>
        </div>

        <div className="grid grid-cols-2 gap-2.5">
          <div className="p-2.5 rounded-2xl bg-black/40 border border-white/5 flex items-center gap-2.5">
            <div className="p-1.5 rounded-xl bg-emerald-950/80 border border-emerald-500/20 text-emerald-400 shrink-0">
              <ArrowDownLeft className="w-4 h-4" />
            </div>
            <div className="min-w-0">
              <span className="text-[10px] text-slate-400 block truncate">Ingresos</span>
              <span className="text-xs font-mono font-bold text-emerald-300 tabular-nums truncate block">
                +${totalIngresos.toLocaleString()}
              </span>
            </div>
          </div>

          <div className="p-2.5 rounded-2xl bg-black/40 border border-white/5 flex items-center gap-2.5">
            <div className="p-1.5 rounded-xl bg-rose-950/60 border border-rose-500/20 text-rose-400 shrink-0">
              <ArrowUpRight className="w-4 h-4" />
            </div>
            <div className="min-w-0">
              <span className="text-[10px] text-slate-400 block truncate">Gastos</span>
              <span className="text-xs font-mono font-bold text-rose-300 tabular-nums truncate block">
                -${totalGastos.toLocaleString()}
              </span>
            </div>
          </div>
        </div>
      </div>

      {/* Filter Bar */}
      <div className="space-y-2">
        {/* Type Filter Buttons */}
        <div className="flex items-center p-1 bg-black/50 rounded-xl border border-white/10 text-xs">
          <button
            onClick={() => setFiltroTipo('todos')}
            className={`flex-1 py-1.5 rounded-lg font-medium transition-all ${
              filtroTipo === 'todos'
                ? 'bg-emerald-950/80 border border-emerald-500/40 text-emerald-300 font-semibold'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            Todos
          </button>
          <button
            onClick={() => setFiltroTipo('gasto')}
            className={`flex-1 py-1.5 rounded-lg font-medium transition-all ${
              filtroTipo === 'gasto'
                ? 'bg-rose-950/80 border border-rose-500/40 text-rose-300 font-semibold'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            Solo Gastos
          </button>
          <button
            onClick={() => setFiltroTipo('ingreso')}
            className={`flex-1 py-1.5 rounded-lg font-medium transition-all ${
              filtroTipo === 'ingreso'
                ? 'bg-emerald-950/80 border border-emerald-500/40 text-emerald-300 font-semibold'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            Solo Ingresos
          </button>
        </div>

        {/* Date Filter Bar */}
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 text-xs">
          {[
            { id: 'mes', label: 'Este Mes' },
            { id: '30dias', label: 'Últimos 30 días' },
            { id: 'anio', label: 'Este Año' },
            { id: 'todos', label: 'Todo el Historial' },
          ].map((f) => (
            <button
              key={f.id}
              onClick={() => setFiltroFecha(f.id as any)}
              className={`py-1.5 px-3 rounded-xl border text-[11px] whitespace-nowrap transition-colors ${
                filtroFecha === f.id
                  ? 'bg-emerald-950/70 border-emerald-500/50 text-emerald-300 font-semibold'
                  : 'bg-black/40 border-white/10 text-slate-400 hover:text-slate-200'
              }`}
            >
              {f.label}
            </button>
          ))}
        </div>

        {/* Search & Category Filter */}
        <div className="grid grid-cols-2 gap-2">
          <div className="relative">
            <Search className="w-3.5 h-3.5 text-slate-500 absolute left-2.5 top-2.5" />
            <input
              type="text"
              placeholder="Buscar concepto..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full py-1.5 pl-8 pr-2.5 rounded-xl glass-input text-xs"
            />
          </div>

          <select
            value={filtroCategoria}
            onChange={(e) => setFiltroCategoria(e.target.value)}
            className="w-full py-1.5 px-2.5 rounded-xl glass-input text-xs"
          >
            <option value="todas" className="bg-[#0e1612]">Todas las Categorías</option>
            {availableCategories.map((c) => (
              <option key={c} value={c} className="bg-[#0e1612]">
                {c}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Movements List */}
      <div className="space-y-2 mt-2">
        {movimientosFiltrados.length === 0 ? (
          <div className="p-8 text-center rounded-3xl glass-card border border-white/5 my-4">
            <Receipt className="w-8 h-8 text-slate-600 mx-auto mb-2" />
            <h4 className="text-xs font-bold text-white mb-1">Sin movimientos encontrados</h4>
            <p className="text-[11px] text-slate-400">
              Prueba cambiando los filtros o registra un nuevo ingreso o gasto.
            </p>
          </div>
        ) : (
          movimientosFiltrados.map((mov) => {
            const isIngreso = mov.tipo === 'ingreso';
            const creador = usuarios.find((u) => u.id_usuario === mov.id_usuario_creador);

            return (
              <div
                key={mov.id_movimiento}
                className="p-3.5 rounded-2xl glass-card border border-white/5 hover:border-emerald-500/30 transition-all flex items-start justify-between gap-3"
              >
                <div className="flex items-start gap-3 min-w-0 flex-1">
                  <div
                    className={`p-2 rounded-xl shrink-0 mt-0.5 ${
                      isIngreso
                        ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30'
                        : 'bg-rose-500/20 text-rose-400 border border-rose-500/30'
                    }`}
                  >
                    {isIngreso ? (
                      <ArrowDownLeft className="w-4 h-4" />
                    ) : (
                      <ArrowUpRight className="w-4 h-4" />
                    )}
                  </div>

                  <div className="min-w-0 flex-1">
                    <div className="flex items-center gap-2">
                      <h4 className="text-xs font-bold text-white truncate">{mov.nombre}</h4>
                    </div>

                    {mov.descripcion && (
                      <p className="text-[11px] text-slate-300/90 line-clamp-1 mt-0.5">
                        {mov.descripcion}
                      </p>
                    )}

                    <div className="flex flex-wrap items-center gap-1.5 text-[10px] text-slate-400 mt-1.5">
                      <span className="font-semibold text-slate-300">
                        {creador?.nombre.split(' ')[0] || 'Usuario'}
                      </span>
                      <span aria-hidden="true">·</span>
                      <span>{mov.categoria || 'General'}</span>
                      <span aria-hidden="true">·</span>
                      <span className="font-mono">{mov.fecha_movimiento}</span>
                      {mov.foto_url && (
                        <>
                          <span aria-hidden="true">·</span>
                          <span className="text-emerald-400 flex items-center gap-0.5">
                            <Image className="w-3 h-3" />
                            Comprobante
                          </span>
                        </>
                      )}
                    </div>
                  </div>
                </div>

                <div className="text-right shrink-0">
                  <span
                    className={`text-sm font-mono font-bold tabular-nums block ${
                      isIngreso ? 'text-emerald-400' : 'text-slate-100'
                    }`}
                  >
                    {isIngreso ? '+' : '-'}${mov.monto.toLocaleString()}
                  </span>
                  <button
                    onClick={() => {
                      if (confirm(`¿Eliminar movimiento "${mov.nombre}"?`)) {
                        eliminarMovimiento(mov.id_movimiento);
                      }
                    }}
                    className="text-slate-600 hover:text-rose-400 p-1 transition-colors mt-1"
                    title="Eliminar movimiento"
                  >
                    <Trash2 className="w-3 h-3" />
                  </button>
                </div>
              </div>
            );
          })
        )}
      </div>

      {/* Botón flotante a la derecha para agregar un movimiento (Requerido por el usuario) */}
      <div className="fixed bottom-24 right-5 z-40 max-w-md mx-auto pointer-events-auto">
        <button
          onClick={() => setShowAddModal(true)}
          className="w-14 h-14 rounded-2xl bg-gradient-to-tr from-emerald-500 via-emerald-400 to-teal-300 text-black shadow-[0_4px_25px_rgba(16,185,129,0.5)] border border-emerald-200/50 flex items-center justify-center transition-all hover:scale-105 active:scale-95 group"
          title="Agregar nuevo movimiento financiero"
        >
          <Plus className="w-7 h-7 stroke-[2.7] group-hover:rotate-90 transition-transform duration-200" />
        </button>
      </div>

      {/* Modal */}
      {showAddModal && <AddMovementModal onClose={() => setShowAddModal(false)} />}
    </div>
  );
};
