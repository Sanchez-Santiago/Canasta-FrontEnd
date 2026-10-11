import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { PrioridadProducto } from '../../types/database';
import { Plus, X, Barcode, Sparkles, Tag } from 'lucide-react';

interface AddProductModalProps {
  id_lista: number;
  onClose: () => void;
}

export const AddProductModal: React.FC<AddProductModalProps> = ({ id_lista, onClose }) => {
  const { crearProducto, listasList } = useApp() as any;
  const { crearProducto: addProd, listas } = useApp();

  const currentList = listas.find((l) => l.id_lista === id_lista);

  const [nombre, setNombre] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [prioridad, setPrioridad] = useState<PrioridadProducto>('esencial');
  const [cantidad, setCantidad] = useState(1);
  const [precioUnitario, setPrecioUnitario] = useState('2500');
  const [codigoProducto, setCodigoProducto] = useState('');

  const generateBarcode = () => {
    const randomCode = `CAN-${Math.floor(10000 + Math.random() * 90000)}`;
    setCodigoProducto(randomCode);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!nombre.trim()) return;

    const parsedPrice = parseInt(precioUnitario.replace(/\D/g, ''), 10) || 0;

    addProd({
      id_lista,
      nombre: nombre.trim(),
      descripcion: descripcion.trim() || undefined,
      prioridad,
      cantidad: Number(cantidad) > 0 ? Number(cantidad) : 1,
      precio_estimado_unitario: parsedPrice,
      codigo_producto: codigoProducto.trim() || undefined,
    });

    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/80 backdrop-blur-sm animate-in fade-in duration-200">
      <div className="w-full max-w-md bg-[#0e1612] border-t sm:border border-emerald-500/30 rounded-t-3xl sm:rounded-2xl p-5 shadow-2xl max-h-[90vh] overflow-y-auto">
        {/* Drag handle */}
        <div className="w-10 h-1 bg-slate-700/60 rounded-full mx-auto mb-3" />

        <div className="flex items-center justify-between mb-3">
          <div>
            <span className="text-[10px] uppercase font-bold text-emerald-400 tracking-wider">
              {currentList?.nombre || 'Lista de Compras'}
            </span>
            <h3 className="text-base font-bold text-white">Añadir Producto a la Lista</h3>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-white/10"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-3.5">
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              Nombre del Producto *
            </label>
            <input
              type="text"
              required
              placeholder="Ej. Aceite de Oliva 500ml, Detergente, Pan"
              value={nombre}
              onChange={(e) => setNombre(e.target.value)}
              className="w-full py-2.5 px-3 rounded-xl glass-input text-sm"
              autoFocus
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              Descripción o Marca sugerida
            </label>
            <input
              type="text"
              placeholder="Ej. Primera marca o envase reciclable"
              value={descripcion}
              onChange={(e) => setDescripcion(e.target.value)}
              className="w-full py-2.5 px-3 rounded-xl glass-input text-sm"
            />
          </div>

          {/* Prioridad: esencial, prioritario, secundario */}
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">
              Nivel de Prioridad *
            </label>
            <div className="grid grid-cols-3 gap-2">
              <button
                type="button"
                onClick={() => setPrioridad('esencial')}
                className={`py-2 px-1 text-center rounded-xl text-xs font-semibold transition-all border ${
                  prioridad === 'esencial'
                    ? 'bg-emerald-950 border-emerald-400 text-emerald-300 shadow-md shadow-emerald-950/80 ring-1 ring-emerald-400/50'
                    : 'bg-black/40 border-white/10 text-slate-400 hover:text-white'
                }`}
              >
                Esencial
              </button>
              <button
                type="button"
                onClick={() => setPrioridad('prioritario')}
                className={`py-2 px-1 text-center rounded-xl text-xs font-semibold transition-all border ${
                  prioridad === 'prioritario'
                    ? 'bg-teal-950 border-teal-400 text-teal-300 shadow-md shadow-teal-950/80 ring-1 ring-teal-400/50'
                    : 'bg-black/40 border-white/10 text-slate-400 hover:text-white'
                }`}
              >
                Prioritario
              </button>
              <button
                type="button"
                onClick={() => setPrioridad('secundario')}
                className={`py-2 px-1 text-center rounded-xl text-xs font-semibold transition-all border ${
                  prioridad === 'secundario'
                    ? 'bg-slate-800 border-slate-400 text-slate-200 shadow-md ring-1 ring-slate-400/50'
                    : 'bg-black/40 border-white/10 text-slate-400 hover:text-white'
                }`}
              >
                Secundario
              </button>
            </div>
          </div>

          {/* Cantidad y Precio Estimado Unitario */}
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">
                Cantidad *
              </label>
              <div className="flex items-center rounded-xl glass-input p-1">
                <button
                  type="button"
                  onClick={() => setCantidad((prev) => Math.max(1, prev - 1))}
                  className="w-8 h-8 rounded-lg bg-black/40 hover:bg-white/10 text-white font-bold text-sm flex items-center justify-center"
                >
                  -
                </button>
                <input
                  type="number"
                  min="0.1"
                  step="0.5"
                  value={cantidad}
                  onChange={(e) => setCantidad(parseFloat(e.target.value) || 1)}
                  className="w-full text-center bg-transparent border-0 text-sm font-bold font-mono focus:outline-none text-white tabular-nums"
                />
                <button
                  type="button"
                  onClick={() => setCantidad((prev) => prev + 1)}
                  className="w-8 h-8 rounded-lg bg-black/40 hover:bg-white/10 text-white font-bold text-sm flex items-center justify-center"
                >
                  +
                </button>
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">
                Precio Unit. Estimado ($)
              </label>
              <input
                type="number"
                min="0"
                step="50"
                value={precioUnitario}
                onChange={(e) => setPrecioUnitario(e.target.value)}
                className="w-full py-2 px-3 rounded-xl glass-input text-sm font-mono font-semibold tabular-nums text-emerald-300"
              />
            </div>
          </div>

          {/* Subtotal estimado en vivo */}
          <div className="p-2.5 rounded-xl bg-black/50 border border-white/5 flex items-center justify-between text-xs">
            <span className="text-slate-400">Subtotal estimado:</span>
            <span className="font-mono font-bold text-emerald-400 text-sm tabular-nums">
              ${((cantidad || 1) * (parseInt(precioUnitario.replace(/\D/g, ''), 10) || 0)).toLocaleString()}
            </span>
          </div>

          {/* Código de producto opcional con generador */}
          <div>
            <div className="flex items-center justify-between mb-1">
              <label className="text-xs font-semibold text-slate-300">
                Código de Producto (Opcional)
              </label>
              <button
                type="button"
                onClick={generateBarcode}
                className="text-[11px] text-emerald-400 hover:text-emerald-300 flex items-center gap-1"
              >
                <Sparkles className="w-3 h-3" />
                <span>Generar</span>
              </button>
            </div>
            <div className="relative">
              <input
                type="text"
                placeholder="Ej. ART-4029"
                value={codigoProducto}
                onChange={(e) => setCodigoProducto(e.target.value)}
                className="w-full py-2 px-3 pl-8 rounded-xl glass-input text-xs font-mono"
              />
              <Barcode className="w-4 h-4 text-slate-500 absolute left-2.5 top-2.5" />
            </div>
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
              className="flex-1 py-2.5 px-4 rounded-xl bg-gradient-to-r from-emerald-500 to-teal-600 text-black font-semibold text-sm hover:from-emerald-400 hover:to-teal-500 shadow-lg shadow-emerald-950/60"
            >
              Guardar Producto
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
