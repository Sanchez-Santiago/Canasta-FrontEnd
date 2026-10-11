import React, { useState, useMemo } from 'react';
import { useApp } from '../../context/AppContext';
import {
  Wallet,
  ArrowUpRight,
  ArrowDownLeft,
  ShoppingCart,
  UserPlus,
  Users,
  ChevronRight,
  Sparkles,
  ReceiptText,
  BadgePercent,
  Layers,
  CircleDollarSign,
  TrendingUp,
} from 'lucide-react';
import { InviteGroupModal } from '../modals/InviteGroupModal';

export const ResumenScreen: React.FC = () => {
  const {
    currentUsuario,
    activeGrupo,
    modoGrupo,
    activeMovimientos,
    activeListas,
    productos,
    usuarios,
    usuarioGrupos,
    setPantallaActiva,
    ofertas,
  } = useApp();

  const [chartMode, setChartMode] = useState<'completo' | 'solo_prioridades'>('completo');
  const [selectedSlice, setSelectedSlice] = useState<
    'todos' | 'gastado' | 'esencial' | 'primario' | 'secundario' | 'disponible'
  >('todos');
  const [showInviteModal, setShowInviteModal] = useState(false);

  // Calcular movimientos de este mes
  const now = new Date();
  const currentMonthPrefix = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;

  const movimientosMes = useMemo(() => {
    return activeMovimientos.filter(
      (m) => m.fecha_movimiento.startsWith(currentMonthPrefix) || true
    );
  }, [activeMovimientos, currentMonthPrefix]);

  const totalIngresos = useMemo(() => {
    return movimientosMes
      .filter((m) => m.tipo === 'ingreso')
      .reduce((sum, m) => sum + m.monto, 0);
  }, [movimientosMes]);

  const totalGastosReal = useMemo(() => {
    return movimientosMes
      .filter((m) => m.tipo === 'gasto')
      .reduce((sum, m) => sum + m.monto, 0);
  }, [movimientosMes]);

  const presupuestoTotal = activeGrupo.presupuesto_mensual || 350000;
  const disponiblePresupuesto = Math.max(0, presupuestoTotal - totalGastosReal);

  // Cálculo de gastos estimados de listas de compras por prioridad
  const activeListIds = useMemo(() => {
    return new Set(activeListas.filter((l) => l.estado === 'activa').map((l) => l.id_lista));
  }, [activeListas]);

  const productosActivos = useMemo(() => {
    return productos.filter((p) => activeListIds.has(p.id_lista) && p.estado === 'pendiente');
  }, [productos, activeListIds]);

  const gastosEstimados = useMemo(() => {
    let esenciales = 0;
    let prioritarios = 0;
    let secundarios = 0;
    let countEsenciales = 0;
    let countPrioritarios = 0;
    let countSecundarios = 0;

    productosActivos.forEach((p) => {
      const costo = p.cantidad * p.precio_estimado_unitario;
      if (p.prioridad === 'esencial') {
        esenciales += costo;
        countEsenciales += 1;
      } else if (p.prioridad === 'prioritario') {
        prioritarios += costo;
        countPrioritarios += 1;
      } else {
        secundarios += costo;
        countSecundarios += 1;
      }
    });

    const totalEstimado = esenciales + prioritarios + secundarios;

    return {
      esenciales,
      prioritarios,
      secundarios,
      totalEstimado,
      countEsenciales,
      countPrioritarios,
      countSecundarios,
    };
  }, [productosActivos]);

  // Miembros del grupo activo
  const groupMembers = useMemo(() => {
    return usuarioGrupos
      .filter((ug) => ug.id_grupo === activeGrupo.id_grupo)
      .map((ug) => usuarios.find((u) => u.id_usuario === ug.id_usuario))
      .filter(Boolean);
  }, [usuarioGrupos, activeGrupo, usuarios]);

  // Segmentos del gráfico SVG
  const radius = 50;
  const circumference = 2 * Math.PI * radius; // ~314.16

  const segmentsData = useMemo(() => {
    if (chartMode === 'solo_prioridades') {
      const sum = Math.max(1, gastosEstimados.totalEstimado);
      const sEsencial = (gastosEstimados.esenciales / sum) * 100;
      const sPrimario = (gastosEstimados.prioritarios / sum) * 100;
      const sSecundario = (gastosEstimados.secundarios / sum) * 100;

      return [
        {
          id: 'esencial',
          name: 'Esenciales',
          amount: gastosEstimados.esenciales,
          percent: sEsencial,
          strokeColor: '#34d399', // Sage/Forest Emerald
        },
        {
          id: 'primario',
          name: 'Primarios',
          amount: gastosEstimados.prioritarios,
          percent: sPrimario,
          strokeColor: '#38bdf8', // Ice Blue
        },
        {
          id: 'secundario',
          name: 'Secundarios',
          amount: gastosEstimados.secundarios,
          percent: sSecundario,
          strokeColor: '#facc15', // Warm Ochre
        },
      ];
    }

    const baseTotal = Math.max(
      presupuestoTotal,
      totalGastosReal + gastosEstimados.totalEstimado
    );
    const pGastado = (totalGastosReal / baseTotal) * 100;
    const pEsencial = (gastosEstimados.esenciales / baseTotal) * 100;
    const pPrimario = (gastosEstimados.prioritarios / baseTotal) * 100;
    const pSecundario = (gastosEstimados.secundarios / baseTotal) * 100;
    const pDisponible = Math.max(0, 100 - (pGastado + pEsencial + pPrimario + pSecundario));

    return [
      {
        id: 'gastado',
        name: 'Gasto Real',
        amount: totalGastosReal,
        percent: pGastado,
        strokeColor: '#fb7185', // Soft Rose
      },
      {
        id: 'esencial',
        name: 'Esenciales',
        amount: gastosEstimados.esenciales,
        percent: pEsencial,
        strokeColor: '#34d399', // Forest Emerald
      },
      {
        id: 'primario',
        name: 'Primarios',
        amount: gastosEstimados.prioritarios,
        percent: pPrimario,
        strokeColor: '#38bdf8', // Ice Blue
      },
      {
        id: 'secundario',
        name: 'Secundarios',
        amount: gastosEstimados.secundarios,
        percent: pSecundario,
        strokeColor: '#facc15', // Warm Ochre
      },
      {
        id: 'disponible',
        name: 'Disponible',
        amount: Math.max(0, baseTotal - (totalGastosReal + gastosEstimados.totalEstimado)),
        percent: pDisponible,
        strokeColor: '#334155', // Smoky Slate
      },
    ];
  }, [chartMode, gastosEstimados, totalGastosReal, presupuestoTotal]);

  let cumulativePercent = 0;
  const arcs = segmentsData.map((seg) => {
    const strokeDasharray = `${(seg.percent / 100) * circumference} ${circumference}`;
    const strokeDashoffset = -((cumulativePercent / 100) * circumference);
    cumulativePercent += seg.percent;
    return {
      ...seg,
      strokeDasharray,
      strokeDashoffset,
    };
  });

  const centerDisplay = useMemo(() => {
    if (selectedSlice !== 'todos') {
      const match = segmentsData.find((s) => s.id === selectedSlice);
      if (match) {
        return {
          title: match.name,
          amount: match.amount,
          percent: Math.round(match.percent),
          color: match.strokeColor,
        };
      }
    }

    if (chartMode === 'solo_prioridades') {
      return {
        title: 'Estimados',
        amount: gastosEstimados.totalEstimado,
        percent: 100,
        color: '#34d399',
      };
    }

    const pct = Math.min(100, Math.round((totalGastosReal / presupuestoTotal) * 100));
    return {
      title: 'Consumido',
      amount: totalGastosReal,
      percent: pct,
      color: '#34d399',
    };
  }, [selectedSlice, segmentsData, chartMode, gastosEstimados, totalGastosReal, presupuestoTotal]);

  return (
    <div className="space-y-3 pb-8">
      {/* Bento Container Grid */}
      <div className="grid grid-cols-2 gap-3">
        {/* ============================================================== */}
        {/* BENTO CARD 1: Hero Financial Pulse & Segmented Donut Chart     */}
        {/* ============================================================== */}
        <div className="col-span-2 bento-card rounded-3xl p-4 relative overflow-hidden">
          {/* Subtle warm specular illumination */}
          <div className="absolute top-0 right-0 w-36 h-36 bg-emerald-900/10 rounded-full blur-3xl pointer-events-none" />

          {/* Header Row */}
          <div className="flex items-center justify-between mb-3">
            <div>
              <span className="text-[10px] font-semibold tracking-wider text-slate-400 uppercase block">
                Presupuesto Mensual
              </span>
              <h2 className="text-base font-bold text-white tracking-tight">
                {activeGrupo.nombre}
              </h2>
            </div>

            {/* Toggle Modes */}
            <div className="flex p-0.5 bg-black/60 rounded-xl border border-white/10">
              <button
                onClick={() => {
                  setChartMode('completo');
                  setSelectedSlice('todos');
                }}
                className={`py-1 px-2 rounded-lg text-[10px] font-bold transition-all ${
                  chartMode === 'completo'
                    ? 'bg-emerald-900/80 text-emerald-300 border border-emerald-500/30'
                    : 'text-slate-400 hover:text-white'
                }`}
              >
                Completo
              </button>
              <button
                onClick={() => {
                  setChartMode('solo_prioridades');
                  setSelectedSlice('todos');
                }}
                className={`py-1 px-2 rounded-lg text-[10px] font-bold transition-all ${
                  chartMode === 'solo_prioridades'
                    ? 'bg-emerald-900/80 text-emerald-300 border border-emerald-500/30'
                    : 'text-slate-400 hover:text-white'
                }`}
              >
                Estimados
              </button>
            </div>
          </div>

          {/* Donut Chart + Stacked Stats Layout */}
          <div className="flex items-center justify-between gap-3 p-3 rounded-2xl bg-black/40 border border-white/[0.05]">
            {/* Donut SVG */}
            <div className="relative w-36 h-36 shrink-0 flex items-center justify-center">
              <svg className="w-full h-full -rotate-90 transform" viewBox="0 0 120 120">
                <circle
                  cx="60"
                  cy="60"
                  r={radius}
                  className="stroke-slate-800/80"
                  strokeWidth="11"
                  fill="transparent"
                />
                {arcs.map((arc) => {
                  const isHighlighted = selectedSlice === 'todos' || selectedSlice === arc.id;
                  return (
                    <circle
                      key={arc.id}
                      cx="60"
                      cy="60"
                      r={radius}
                      stroke={arc.strokeColor}
                      strokeWidth={isHighlighted ? 12 : 7}
                      strokeDasharray={arc.strokeDasharray}
                      strokeDashoffset={arc.strokeDashoffset}
                      strokeLinecap="round"
                      fill="transparent"
                      className="transition-all duration-500 cursor-pointer"
                      style={{
                        opacity: isHighlighted ? 1 : 0.25,
                      }}
                      onClick={() =>
                        setSelectedSlice((prev) => (prev === arc.id ? 'todos' : (arc.id as any)))
                      }
                    />
                  );
                })}
              </svg>

              <div className="absolute inset-0 flex flex-col items-center justify-center text-center p-2 select-none pointer-events-none">
                <span
                  className="text-[9px] uppercase font-bold tracking-tight line-clamp-1 text-slate-300"
                  style={{ color: centerDisplay.color }}
                >
                  {centerDisplay.title}
                </span>
                <span className="text-base font-extrabold font-mono tabular-nums text-white">
                  ${centerDisplay.amount.toLocaleString()}
                </span>
                <span className="text-[9px] font-mono text-slate-400">
                  {centerDisplay.percent}%
                </span>
              </div>
            </div>

            {/* Quick KPI summary */}
            <div className="flex-1 space-y-2 min-w-0">
              <div className="p-2 rounded-xl bg-black/50 border border-white/5">
                <span className="text-[10px] text-slate-400 block">Saldo Neto</span>
                <span className="text-sm font-mono font-bold text-emerald-300 tabular-nums">
                  ${(totalIngresos - totalGastosReal).toLocaleString()}
                </span>
              </div>

              <div className="p-2 rounded-xl bg-black/50 border border-white/5">
                <span className="text-[10px] text-slate-400 block">Disponible</span>
                <span className="text-sm font-mono font-bold text-slate-200 tabular-nums">
                  ${disponiblePresupuesto.toLocaleString()}
                </span>
              </div>
            </div>
          </div>

          {/* Interactive Bento Category Tags */}
          <div className="grid grid-cols-3 gap-1.5 mt-2.5">
            {segmentsData.slice(0, 3).map((seg) => {
              const isSelected = selectedSlice === seg.id;
              return (
                <button
                  key={seg.id}
                  onClick={() =>
                    setSelectedSlice((prev) => (prev === seg.id ? 'todos' : (seg.id as any)))
                  }
                  className={`p-2 rounded-xl border text-left transition-all ${
                    isSelected
                      ? 'bg-slate-900 border-white/30 ring-1 ring-white/20'
                      : 'bg-black/30 border-white/5 hover:border-white/10'
                  }`}
                >
                  <div className="flex items-center gap-1.5 mb-0.5">
                    <span
                      className="w-2 h-2 rounded-full shrink-0"
                      style={{ backgroundColor: seg.strokeColor }}
                    />
                    <span className="text-[10px] font-semibold text-slate-300 truncate">
                      {seg.name}
                    </span>
                  </div>
                  <span className="text-xs font-mono font-bold text-white tabular-nums block truncate">
                    ${seg.amount.toLocaleString()}
                  </span>
                </button>
              );
            })}
          </div>
        </div>

        {/* ============================================================== */}
        {/* BENTO CARD 2: Ingresos del Mes Tile                            */}
        {/* ============================================================== */}
        <div className="bento-card rounded-3xl p-3.5 flex flex-col justify-between">
          <div className="flex items-center justify-between mb-2">
            <span className="text-[10px] font-semibold text-slate-400 uppercase">Ingresos</span>
            <div className="p-1.5 rounded-xl bg-emerald-950/80 border border-emerald-500/20 text-emerald-400">
              <ArrowDownLeft className="w-3.5 h-3.5" />
            </div>
          </div>
          <div>
            <span className="text-base font-mono font-bold text-emerald-300 tabular-nums block">
              +${totalIngresos.toLocaleString()}
            </span>
            <span className="text-[10px] text-slate-400 block mt-0.5">Total cobrado</span>
          </div>
        </div>

        {/* ============================================================== */}
        {/* BENTO CARD 3: Gastos del Mes Tile                              */}
        {/* ============================================================== */}
        <div className="bento-card rounded-3xl p-3.5 flex flex-col justify-between">
          <div className="flex items-center justify-between mb-2">
            <span className="text-[10px] font-semibold text-slate-400 uppercase">Gastos</span>
            <div className="p-1.5 rounded-xl bg-rose-950/60 border border-rose-500/20 text-rose-400">
              <ArrowUpRight className="w-3.5 h-3.5" />
            </div>
          </div>
          <div>
            <span className="text-base font-mono font-bold text-rose-300 tabular-nums block">
              -${totalGastosReal.toLocaleString()}
            </span>
            <span className="text-[10px] text-slate-400 block mt-0.5">Egresos registrados</span>
          </div>
        </div>

        {/* ============================================================== */}
        {/* BENTO CARD 4: Group & Invitar Integrante Tile                  */}
        {/* ============================================================== */}
        <div className="col-span-2 bento-card rounded-3xl p-3.5 flex items-center justify-between gap-3">
          <div className="flex items-center gap-3 min-w-0">
            <div className="w-10 h-10 rounded-2xl bg-emerald-950/60 border border-emerald-500/20 text-emerald-400 flex items-center justify-center shrink-0">
              <Users className="w-5 h-5" />
            </div>
            <div className="min-w-0">
              <h4 className="text-xs font-bold text-white truncate">
                {activeGrupo.nombre}
              </h4>
              <p className="text-[10px] text-slate-400 truncate">
                {groupMembers.length} {groupMembers.length === 1 ? 'integrante' : 'integrantes'} · {activeGrupo.tipo}
              </p>
            </div>
          </div>

          <button
            onClick={() => setShowInviteModal(true)}
            className="py-2 px-3 rounded-2xl bg-emerald-900/60 border border-emerald-500/30 text-emerald-300 text-xs font-bold flex items-center gap-1.5 hover:bg-emerald-800/80 transition-all shadow-sm shrink-0 active:scale-95"
            title="Enviar invitación a un usuario o compartir enlace"
          >
            <UserPlus className="w-3.5 h-3.5" />
            <span>Invitar</span>
          </button>
        </div>

        {/* ============================================================== */}
        {/* BENTO CARD 5: Estimaciones por Prioridad (Sub-bars Bento)     */}
        {/* ============================================================== */}
        <div className="col-span-2 bento-card rounded-3xl p-4">
          <div className="flex items-center justify-between mb-3">
            <div className="flex items-center gap-2">
              <div className="p-1.5 rounded-xl bg-emerald-950/60 border border-emerald-500/20 text-emerald-400">
                <ShoppingCart className="w-4 h-4" />
              </div>
              <div>
                <h3 className="text-xs font-bold text-white">Gastos Estimados en Listas</h3>
                <p className="text-[10px] text-slate-400 font-mono">
                  ${gastosEstimados.totalEstimado.toLocaleString()} requeridos
                </p>
              </div>
            </div>

            <button
              onClick={() => setPantallaActiva('listas')}
              className="text-xs font-semibold text-emerald-400 hover:text-emerald-300 flex items-center gap-0.5"
            >
              Ver listas
              <ChevronRight className="w-3.5 h-3.5" />
            </button>
          </div>

          {/* Mini progress strips */}
          <div className="space-y-2">
            {/* Esencial */}
            <div className="p-2.5 rounded-2xl bg-black/40 border border-white/5">
              <div className="flex items-center justify-between text-xs mb-1">
                <div className="flex items-center gap-1.5">
                  <span className="w-2 h-2 rounded-full bg-[#34d399]" />
                  <span className="font-semibold text-slate-200 text-[11px]">Esenciales</span>
                  <span className="text-[10px] text-slate-500">({gastosEstimados.countEsenciales})</span>
                </div>
                <span className="font-mono font-bold text-emerald-300 tabular-nums text-xs">
                  ${gastosEstimados.esenciales.toLocaleString()}
                </span>
              </div>
              <div className="w-full h-1.5 bg-black/60 rounded-full overflow-hidden">
                <div
                  className="h-full bg-[#34d399] rounded-full"
                  style={{
                    width: `${
                      gastosEstimados.totalEstimado > 0
                        ? (gastosEstimados.esenciales / gastosEstimados.totalEstimado) * 100
                        : 0
                    }%`,
                  }}
                />
              </div>
            </div>

            {/* Primario / Prioritario */}
            <div className="p-2.5 rounded-2xl bg-black/40 border border-white/5">
              <div className="flex items-center justify-between text-xs mb-1">
                <div className="flex items-center gap-1.5">
                  <span className="w-2 h-2 rounded-full bg-[#38bdf8]" />
                  <span className="font-semibold text-slate-200 text-[11px]">Primarios</span>
                  <span className="text-[10px] text-slate-500">({gastosEstimados.countPrioritarios})</span>
                </div>
                <span className="font-mono font-bold text-sky-300 tabular-nums text-xs">
                  ${gastosEstimados.prioritarios.toLocaleString()}
                </span>
              </div>
              <div className="w-full h-1.5 bg-black/60 rounded-full overflow-hidden">
                <div
                  className="h-full bg-[#38bdf8] rounded-full"
                  style={{
                    width: `${
                      gastosEstimados.totalEstimado > 0
                        ? (gastosEstimados.prioritarios / gastosEstimados.totalEstimado) * 100
                        : 0
                    }%`,
                  }}
                />
              </div>
            </div>

            {/* Secundario */}
            <div className="p-2.5 rounded-2xl bg-black/40 border border-white/5">
              <div className="flex items-center justify-between text-xs mb-1">
                <div className="flex items-center gap-1.5">
                  <span className="w-2 h-2 rounded-full bg-[#facc15]" />
                  <span className="font-semibold text-slate-200 text-[11px]">Secundarios</span>
                  <span className="text-[10px] text-slate-500">({gastosEstimados.countSecundarios})</span>
                </div>
                <span className="font-mono font-bold text-amber-300 tabular-nums text-xs">
                  ${gastosEstimados.secundarios.toLocaleString()}
                </span>
              </div>
              <div className="w-full h-1.5 bg-black/60 rounded-full overflow-hidden">
                <div
                  className="h-full bg-[#facc15] rounded-full"
                  style={{
                    width: `${
                      gastosEstimados.totalEstimado > 0
                        ? (gastosEstimados.secundarios / gastosEstimados.totalEstimado) * 100
                        : 0
                    }%`,
                  }}
                />
              </div>
            </div>
          </div>
        </div>

        {/* ============================================================== */}
        {/* BENTO CARD 6: Radar de Ofertas Banner Tile                     */}
        {/* ============================================================== */}
        <div
          onClick={() => setPantallaActiva('ofertas')}
          className="col-span-2 bento-card rounded-3xl p-3.5 hover:border-emerald-500/30 cursor-pointer transition-all flex items-center justify-between gap-3 group"
        >
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-2xl bg-gradient-to-tr from-emerald-800 to-teal-900 border border-emerald-500/20 text-emerald-300 flex items-center justify-center shrink-0 group-hover:scale-105 transition-transform">
              <BadgePercent className="w-5 h-5 stroke-[2.2]" />
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="text-xs font-bold text-white">Radar de Ofertas</span>
                <span className="text-[10px] font-semibold text-emerald-300 bg-emerald-950/80 px-1.5 py-0.2 rounded border border-emerald-500/20">
                  {ofertas.length} activas
                </span>
              </div>
              <p className="text-[10px] text-slate-400">
                Ahorra en comercios cercanos con descuentos del grupo
              </p>
            </div>
          </div>
          <ChevronRight className="w-4 h-4 text-slate-500 group-hover:text-emerald-400 transition-colors" />
        </div>

        {/* ============================================================== */}
        {/* BENTO CARD 7: Movimientos Recientes Feed                       */}
        {/* ============================================================== */}
        <div className="col-span-2 bento-card rounded-3xl p-4">
          <div className="flex items-center justify-between mb-3">
            <div className="flex items-center gap-2">
              <div className="p-1.5 rounded-xl bg-black/40 border border-white/10 text-emerald-400">
                <ReceiptText className="w-4 h-4" />
              </div>
              <h3 className="text-xs font-bold text-white">Actividad Reciente</h3>
            </div>
            <button
              onClick={() => setPantallaActiva('historial')}
              className="text-xs font-semibold text-emerald-400 hover:text-emerald-300 flex items-center gap-0.5"
            >
              Historial
              <ChevronRight className="w-3.5 h-3.5" />
            </button>
          </div>

          <div className="space-y-2">
            {activeMovimientos.slice(0, 4).map((mov) => {
              const isIngreso = mov.tipo === 'ingreso';
              const creador = usuarios.find((u) => u.id_usuario === mov.id_usuario_creador);

              return (
                <div
                  key={mov.id_movimiento}
                  className="p-2.5 rounded-2xl bg-black/30 border border-white/5 flex items-center justify-between gap-2"
                >
                  <div className="flex items-center gap-2.5 min-w-0">
                    <div
                      className={`p-1.5 rounded-xl shrink-0 ${
                        isIngreso
                          ? 'bg-emerald-950/60 text-emerald-400 border border-emerald-500/20'
                          : 'bg-rose-950/40 text-rose-400 border border-rose-500/20'
                      }`}
                    >
                      {isIngreso ? (
                        <ArrowDownLeft className="w-3.5 h-3.5" />
                      ) : (
                        <ArrowUpRight className="w-3.5 h-3.5" />
                      )}
                    </div>
                    <div className="min-w-0">
                      <h4 className="text-xs font-semibold text-white truncate">{mov.nombre}</h4>
                      <p className="text-[10px] text-slate-400 truncate">
                        {creador?.nombre.split(' ')[0] || 'Usuario'} · {mov.fecha_movimiento}
                      </p>
                    </div>
                  </div>

                  <span
                    className={`text-xs font-mono font-bold tabular-nums shrink-0 ${
                      isIngreso ? 'text-emerald-400' : 'text-slate-200'
                    }`}
                  >
                    {isIngreso ? '+' : '-'}${mov.monto.toLocaleString()}
                  </span>
                </div>
              );
            })}
          </div>
        </div>
      </div>

      {/* Invite Modal */}
      {showInviteModal && <InviteGroupModal onClose={() => setShowInviteModal(false)} />}
    </div>
  );
};
