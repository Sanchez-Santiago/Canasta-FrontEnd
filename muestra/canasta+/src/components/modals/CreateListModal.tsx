import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { ShoppingCart, X } from 'lucide-react';

interface CreateListModalProps {
  onClose: () => void;
  onCreated?: (id_lista: number) => void;
}

export const CreateListModal: React.FC<CreateListModalProps> = ({ onClose, onCreated }) => {
  const { crearLista, activeGrupo } = useApp();
  const [nombre, setNombre] = useState('');
  const [descripcion, setDescripcion] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!nombre.trim()) return;

    const newList = crearLista(nombre.trim(), descripcion.trim() || undefined);
    if (onCreated) {
      onCreated(newList.id_lista);
    }
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/80 backdrop-blur-sm animate-in fade-in duration-200">
      <div className="w-full max-w-md bg-[#0e1612] border-t sm:border border-emerald-500/30 rounded-t-3xl sm:rounded-2xl p-5 shadow-2xl">
        <div className="w-10 h-1 bg-slate-700/60 rounded-full mx-auto mb-3" />

        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2">
            <div className="p-2 rounded-xl bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
              <ShoppingCart className="w-5 h-5" />
            </div>
            <div>
              <h3 className="text-base font-bold text-white">Nueva Lista de Compras</h3>
              <p className="text-xs text-slate-400">Para el grupo {activeGrupo.nombre}</p>
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
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              Nombre de la Lista *
            </label>
            <input
              type="text"
              required
              placeholder="Ej. Supermercado Mensual, Carnicería Asado, Farmacia"
              value={nombre}
              onChange={(e) => setNombre(e.target.value)}
              className="w-full py-2.5 px-3 rounded-xl glass-input text-sm"
              autoFocus
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              Descripción u Objetivo
            </label>
            <textarea
              rows={2}
              placeholder="Ej. Compras necesarias para la segunda quincena"
              value={descripcion}
              onChange={(e) => setDescripcion(e.target.value)}
              className="w-full py-2 px-3 rounded-xl glass-input text-sm resize-none"
            />
          </div>

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
              Crear Lista
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
