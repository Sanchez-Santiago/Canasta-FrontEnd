import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { TipoMovimiento } from '../../types/database';
import {
  Receipt,
  ArrowUpRight,
  ArrowDownLeft,
  Calendar,
  Camera,
  X,
  Plus,
  DollarSign,
  Tag,
} from 'lucide-react';

interface AddMovementModalProps {
  onClose: () => void;
}

const CATEGORIAS_GASTO = [
  'Alimentos & Supermercado',
  'Frescos & Verdulería',
  'Limpieza & Hogar',
  'Salud & Farmacia',
  'Carnicería & Asado',
  'Servicios & Impuestos',
  'Transporte & Combustible',
  'Cafetería & Salidas',
  'Otros Gastos',
];

const CATEGORIAS_INGRESO = [
  'Aporte de Miembro',
  'Sueldo / Honorarios',
  'Fondo Común',
  'Reintegro / Devolución',
  'Ventas / Extra',
  'Otros Ingresos',
];

export const AddMovementModal: React.FC<AddMovementModalProps> = ({ onClose }) => {
  const { crearMovimiento, activeGrupo } = useApp();

  const [tipo, setTipo] = useState<TipoMovimiento>('gasto');
  const [nombre, setNombre] = useState('');
  const [monto, setMonto] = useState('');
  const [categoria, setCategoria] = useState(CATEGORIAS_GASTO[0]);
  const [descripcion, setDescripcion] = useState('');
  const [fecha, setFecha] = useState(() => new Date().toISOString().split('T')[0]);
  const [fotoUrl, setFotoUrl] = useState('');

  const handleTipoChange = (newTipo: TipoMovimiento) => {
    setTipo(newTipo);
    setCategoria(newTipo === 'gasto' ? CATEGORIAS_GASTO[0] : CATEGORIAS_INGRESO[0]);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!nombre.trim() || !monto) return;

    const parsedMonto = parseInt(monto.replace(/\D/g, ''), 10) || 0;
    if (parsedMonto <= 0) return;

    crearMovimiento({
      nombre: nombre.trim(),
      monto: parsedMonto,
      tipo,
      categoria,
      descripcion: descripcion.trim() || undefined,
      fecha_movimiento: fecha,
      foto_url: fotoUrl.trim() || undefined,
    });

    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/80 backdrop-blur-sm animate-in fade-in duration-200">
      <div className="w-full max-w-md bg-[#0e1612] border-t sm:border border-emerald-500/30 rounded-t-3xl sm:rounded-2xl p-5 shadow-2xl max-h-[90vh] overflow-y-auto">
        <div className="w-10 h-1 bg-slate-700/60 rounded-full mx-auto mb-3" />

        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2">
            <div
              className={`p-2 rounded-xl border ${
                tipo === 'ingreso'
                  ? 'bg-emerald-500/20 text-emerald-400 border-emerald-500/30'
                  : 'bg-rose-500/20 text-rose-400 border-rose-500/30'
              }`}
            >
              {tipo === 'ingreso' ? (
                <ArrowDownLeft className="w-5 h-5" />
              ) : (
                <ArrowUpRight className="w-5 h-5" />
              )}
            </div>
            <div>
              <h3 className="text-base font-bold text-white">Registrar Movimiento</h3>
              <p className="text-xs text-slate-400">Grupo: {activeGrupo.nombre}</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-white/10"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-3.5">
          {/* Tipo Toggle: Gasto vs Ingreso */}
          <div className="grid grid-cols-2 gap-2 p-1 bg-black/50 rounded-2xl border border-white/10">
            <button
              type="button"
              onClick={() => handleTipoChange('gasto')}
              className={`py-2 px-3 rounded-xl text-xs font-bold transition-all flex items-center justify-center gap-1.5 ${
                tipo === 'gasto'
                  ? 'bg-rose-950/80 border border-rose-500/50 text-rose-300 shadow-md ring-1 ring-rose-500/30'
                  : 'text-slate-400 hover:text-white'
              }`}
            >
              <ArrowUpRight className="w-4 h-4" />
              <span>Gasto / Egreso</span>
            </button>

            <button
              type="button"
              onClick={() => handleTipoChange('ingreso')}
              className={`py-2 px-3 rounded-xl text-xs font-bold transition-all flex items-center justify-center gap-1.5 ${
                tipo === 'ingreso'
                  ? 'bg-emerald-950/80 border border-emerald-500/50 text-emerald-300 shadow-md ring-1 ring-emerald-500/30'
                  : 'text-slate-400 hover:text-white'
              }`}
            >
              <ArrowDownLeft className="w-4 h-4" />
              <span>Ingreso / Aporte</span>
            </button>
          </div>

          {/* Monto Grande */}
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              Monto del Movimiento *
            </label>
            <div className="relative">
              <span className="absolute left-3.5 top-2.5 text-lg font-bold text-slate-400">$</span>
              <input
                type="number"
                required
                min="1"
                step="100"
                placeholder="0"
                value={monto}
                onChange={(e) => setMonto(e.target.value)}
                className="w-full py-2.5 pl-8 pr-3 rounded-xl glass-input text-lg font-mono font-bold text-white tabular-nums"
                autoFocus
              />
            </div>
          </div>

          {/* Nombre / Concepto */}
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              Concepto / Nombre *
            </label>
            <input
              type="text"
              required
              placeholder={
                tipo === 'gasto'
                  ? 'Ej. Supermercado Día, Pago Expensas, Verdulería'
                  : 'Ej. Sueldo octubre, Aporte para comida, Reintegro'
              }
              value={nombre}
              onChange={(e) => setNombre(e.target.value)}
              className="w-full py-2.5 px-3 rounded-xl glass-input text-xs"
            />
          </div>

          {/* Categoría y Fecha */}
          <div className="grid grid-cols-2 gap-2.5">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">
                Categoría
              </label>
              <select
                value={categoria}
                onChange={(e) => setCategoria(e.target.value)}
                className="w-full py-2.5 px-2 rounded-xl glass-input text-xs"
              >
                {(tipo === 'gasto' ? CATEGORIAS_GASTO : CATEGORIAS_INGRESO).map((cat) => (
                  <option key={cat} value={cat} className="bg-[#0e1612]">
                    {cat}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">
                Fecha
              </label>
              <input
                type="date"
                required
                value={fecha}
                onChange={(e) => setFecha(e.target.value)}
                className="w-full py-2 px-2.5 rounded-xl glass-input text-xs font-mono"
              />
            </div>
          </div>

          {/* Descripción */}
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              Descripción o Detalle (Opcional)
            </label>
            <textarea
              rows={2}
              placeholder="Detalle de la compra, participantes o notas..."
              value={descripcion}
              onChange={(e) => setDescripcion(e.target.value)}
              className="w-full py-2 px-3 rounded-xl glass-input text-xs resize-none"
            />
          </div>

          {/* Foto o Comprobante */}
          <div>
            <div className="flex items-center justify-between mb-1">
              <label className="text-xs font-semibold text-slate-300">
                Comprobante / Ticket (Opcional)
              </label>
              <button
                type="button"
                onClick={() =>
                  setFotoUrl(
                    'https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&w=400&q=80'
                  )
                }
                className="text-[11px] text-emerald-400 hover:text-emerald-300 flex items-center gap-1"
              >
                <Camera className="w-3 h-3" />
                <span>Simular foto ticket</span>
              </button>
            </div>
            {fotoUrl && (
              <div className="mb-2 p-2 rounded-xl bg-black/40 border border-white/10 flex items-center justify-between text-xs">
                <span className="text-emerald-300 truncate max-w-[240px]">Ticket adjuntado</span>
                <button
                  type="button"
                  onClick={() => setFotoUrl('')}
                  className="text-rose-400 hover:text-rose-300"
                >
                  Quitar
                </button>
              </div>
            )}
          </div>

          {/* Actions */}
          <div className="flex gap-2.5 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 py-2.5 px-4 rounded-xl border border-white/10 text-slate-300 text-sm font-medium hover:bg-white/5"
            >
              Cancelar
            </button>
            <button
              type="submit"
              className={`flex-1 py-2.5 px-4 rounded-xl text-black font-semibold text-sm shadow-lg ${
                tipo === 'ingreso'
                  ? 'bg-gradient-to-r from-emerald-500 to-teal-500 hover:from-emerald-400 hover:to-teal-400 shadow-emerald-950/60'
                  : 'bg-gradient-to-r from-emerald-500 to-teal-600 hover:from-emerald-400 hover:to-teal-500 shadow-emerald-950/60'
              }`}
            >
              Guardar Movimiento
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
