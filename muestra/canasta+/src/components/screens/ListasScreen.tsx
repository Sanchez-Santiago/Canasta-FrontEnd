import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { Lista, ProductoLista } from '../../types/database';
import {
  ShoppingCart,
  Plus,
  ChevronDown,
  ChevronUp,
  CheckCircle2,
  Circle,
  Tag,
  Store,
  DollarSign,
  AlertCircle,
  Trash2,
  Calendar,
  Sparkles,
  Barcode,
  Layers,
  Archive,
  Check,
} from 'lucide-react';
import { AddProductModal } from '../modals/AddProductModal';
import { CreateListModal } from '../modals/CreateListModal';
import { AddOfferModal } from '../modals/AddOfferModal';

export const ListasScreen: React.FC = () => {
  const {
    activeListas,
    productos,
    usuarios,
    currentUsuario,
    activeGrupo,
    actualizarEstadoProducto,
    actualizarEstadoLista,
    eliminarProducto,
    eliminarLista,
    ofertas,
  } = useApp();

  // Selected / Expanded list ID (accordion)
  const [selectedListId, setSelectedListId] = useState<number | null>(() => {
    return activeListas.length > 0 ? activeListas[0].id_lista : null;
  });

  // Modals state
  const [showAddProductModal, setShowAddProductModal] = useState(false);
  const [showCreateListModal, setShowCreateListModal] = useState(false);
  const [selectedProductForOffer, setSelectedProductForOffer] = useState<ProductoLista | null>(null);

  // Filter tabs for lists
  const [tabEstado, setTabEstado] = useState<'todas' | 'activas' | 'completadas'>('todas');

  const filteredListas = activeListas.filter((lista) => {
    if (tabEstado === 'activas') return lista.estado === 'activa';
    if (tabEstado === 'completadas') return lista.estado === 'completada';
    return true;
  });

  const toggleList = (id_lista: number) => {
    setSelectedListId((prev) => (prev === id_lista ? null : id_lista));
  };

  const getPriorityBadge = (prioridad: string) => {
    switch (prioridad) {
      case 'esencial':
        return (
          <span className="text-[10px] font-semibold text-emerald-300 bg-emerald-950/80 border border-emerald-500/40 px-1.5 py-0.5 rounded">
            Esencial
          </span>
        );
      case 'prioritario':
        return (
          <span className="text-[10px] font-semibold text-teal-300 bg-teal-950/80 border border-teal-500/40 px-1.5 py-0.5 rounded">
            Prioritario
          </span>
        );
      case 'secundario':
      default:
        return (
          <span className="text-[10px] font-semibold text-slate-300 bg-slate-900 border border-slate-700 px-1.5 py-0.5 rounded">
            Secundario
          </span>
        );
    }
  };

  return (
    <div className="space-y-4 pb-20 relative min-h-[calc(100vh-14rem)]">
      {/* Header and Create Button */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-extrabold text-white tracking-tight flex items-center gap-2">
            <span>Listas de Compras</span>
            <span className="text-xs px-2 py-0.5 rounded-full bg-emerald-500/20 text-emerald-300 font-mono">
              {activeListas.length}
            </span>
          </h2>
          <p className="text-xs text-slate-400">
            {activeGrupo.nombre} · Planificación y presupuestos
          </p>
        </div>

        <button
          onClick={() => setShowCreateListModal(true)}
          className="py-2 px-3 rounded-xl bg-gradient-to-r from-emerald-500 to-teal-600 text-black text-xs font-bold flex items-center gap-1.5 shadow-md shadow-emerald-950/60 hover:from-emerald-400 hover:to-teal-500 transition-all active:scale-95"
        >
          <Plus className="w-4 h-4 stroke-[2.5]" />
          <span>Nueva Lista</span>
        </button>
      </div>

      {/* Filter Segmented Control */}
      <div className="flex items-center p-1 bg-black/50 rounded-xl border border-white/10">
        <button
          onClick={() => setTabEstado('todas')}
          className={`flex-1 py-1.5 text-xs font-medium rounded-lg transition-colors ${
            tabEstado === 'todas'
              ? 'bg-emerald-950/80 border border-emerald-500/40 text-emerald-300 font-semibold shadow-sm'
              : 'text-slate-400 hover:text-white'
          }`}
        >
          Todas ({activeListas.length})
        </button>
        <button
          onClick={() => setTabEstado('activas')}
          className={`flex-1 py-1.5 text-xs font-medium rounded-lg transition-colors ${
            tabEstado === 'activas'
              ? 'bg-emerald-950/80 border border-emerald-500/40 text-emerald-300 font-semibold shadow-sm'
              : 'text-slate-400 hover:text-white'
          }`}
        >
          Activas ({activeListas.filter((l) => l.estado === 'activa').length})
        </button>
        <button
          onClick={() => setTabEstado('completadas')}
          className={`flex-1 py-1.5 text-xs font-medium rounded-lg transition-colors ${
            tabEstado === 'completadas'
              ? 'bg-emerald-950/80 border border-emerald-500/40 text-emerald-300 font-semibold shadow-sm'
              : 'text-slate-400 hover:text-white'
          }`}
        >
          Completadas ({activeListas.filter((l) => l.estado === 'completada').length})
        </button>
      </div>

      {/* Lists Accordion Container */}
      {filteredListas.length === 0 ? (
        <div className="p-8 text-center rounded-3xl bento-card border border-white/5 my-6">
          <div className="w-12 h-12 rounded-2xl bg-emerald-950/50 border border-emerald-500/20 text-emerald-400 flex items-center justify-center mx-auto mb-3">
            <ShoppingCart className="w-6 h-6" />
          </div>
          <h4 className="text-sm font-bold text-white mb-1">No hay listas para mostrar</h4>
          <p className="text-xs text-slate-400 max-w-xs mx-auto mb-4">
            Crea una nueva lista de compras para este grupo y comienza a planificar tus adquisiciones.
          </p>
          <button
            onClick={() => setShowCreateListModal(true)}
            className="py-2.5 px-4 rounded-2xl bg-emerald-700/80 hover:bg-emerald-600 text-white text-xs font-bold inline-flex items-center gap-2 shadow-lg shadow-black/60 transition-all"
          >
            <Plus className="w-4 h-4" />
            <span>Crear Lista de Compras</span>
          </button>
        </div>
      ) : (
        <div className="space-y-3">
          {filteredListas.map((lista) => {
            const isExpanded = selectedListId === lista.id_lista;
            const listProds = productos.filter((p) => p.id_lista === lista.id_lista);
            const totalEstimado = listProds.reduce(
              (sum, p) => sum + p.cantidad * p.precio_estimado_unitario,
              0
            );
            const compradosCount = listProds.filter((p) => p.estado === 'comprado').length;
            const creador = usuarios.find((u) => u.id_usuario === lista.id_usuario_creador);

            return (
              <div
                key={lista.id_lista}
                className={`rounded-3xl transition-all overflow-hidden border ${
                  isExpanded
                    ? 'bento-card-active border-emerald-500/30 shadow-xl'
                    : 'bento-card border-white/[0.06] hover:border-white/15'
                }`}
              >
                {/* List Header (Accordion Trigger) */}
                <div
                  onClick={() => toggleList(lista.id_lista)}
                  className="p-4 cursor-pointer select-none flex items-start justify-between gap-3"
                >
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 mb-1">
                      <h3 className="text-sm font-bold text-white truncate">{lista.nombre}</h3>
                      {lista.estado === 'completada' ? (
                        <span className="text-[10px] font-semibold text-emerald-400 bg-emerald-950/80 border border-emerald-500/30 px-1.5 py-0.2 rounded-md">
                          Completada
                        </span>
                      ) : (
                        <span className="text-[10px] font-medium text-slate-300 bg-black/40 border border-white/10 px-1.5 py-0.2 rounded-md">
                          Activa
                        </span>
                      )}
                    </div>

                    {lista.descripcion && (
                      <p className="text-xs text-slate-400 line-clamp-1 mb-2">
                        {lista.descripcion}
                      </p>
                    )}

                    {/* Metadata & Cost calculation */}
                    <div className="flex flex-wrap items-center gap-x-2.5 gap-y-1 text-[11px] text-slate-400">
                      <span className="font-mono font-bold text-emerald-300 tabular-nums">
                        ${totalEstimado.toLocaleString()} est.
                      </span>
                      <span aria-hidden="true">·</span>
                      <span>
                        {compradosCount}/{listProds.length} comprados
                      </span>
                      <span aria-hidden="true">·</span>
                      <span className="truncate max-w-[110px]">
                        Por {creador?.nombre.split(' ')[0] || 'Usuario'}
                      </span>
                    </div>
                  </div>

                  <div className="flex items-center gap-2 shrink-0 pt-0.5">
                    {isExpanded ? (
                      <div className="p-1.5 rounded-full bg-emerald-950/70 border border-emerald-500/30 text-emerald-300">
                        <ChevronUp className="w-4 h-4" />
                      </div>
                    ) : (
                      <div className="p-1.5 rounded-full bg-black/40 border border-white/10 text-slate-400">
                        <ChevronDown className="w-4 h-4" />
                      </div>
                    )}
                  </div>
                </div>

                {/* Expanded Products Section */}
                {isExpanded && (
                  <div className="px-4 pb-4 pt-1 border-t border-white/[0.06] bg-black/30 animate-in fade-in duration-200">
                    {/* List Actions Toolbar */}
                    <div className="flex items-center justify-between pb-3 pt-1 border-b border-white/5 text-xs">
                      <span className="text-slate-400 text-[11px]">
                        Productos en esta lista: <strong className="text-white font-mono">{listProds.length}</strong>
                      </span>
                      <div className="flex items-center gap-2">
                        <button
                          onClick={(e) => {
                            e.stopPropagation();
                            actualizarEstadoLista(
                              lista.id_lista,
                              lista.estado === 'activa' ? 'completada' : 'activa'
                            );
                          }}
                          className="text-[11px] text-slate-300 hover:text-emerald-300 py-1 px-2.5 rounded-xl bg-white/5 hover:bg-white/10 transition-colors"
                        >
                          {lista.estado === 'activa' ? 'Marcar Completada' : 'Reactivar'}
                        </button>
                        <button
                          onClick={(e) => {
                            e.stopPropagation();
                            if (confirm(`¿Eliminar la lista "${lista.nombre}"?`)) {
                              eliminarLista(lista.id_lista);
                            }
                          }}
                          className="text-[11px] text-rose-400 hover:text-rose-300 p-1 rounded-xl hover:bg-rose-950/30"
                          title="Eliminar lista"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
                        </button>
                      </div>
                    </div>

                    {/* Products List */}
                    {listProds.length === 0 ? (
                      <div className="text-center py-6 text-slate-500 text-xs">
                        Esta lista aún no tiene productos.
                        <p className="text-[11px] text-emerald-400 mt-1">
                          Presiona el botón flotante (+) para agregar el primero.
                        </p>
                      </div>
                    ) : (
                      <div className="space-y-2 mt-3">
                        {listProds.map((prod) => {
                          const isComprado = prod.estado === 'comprado';
                          const isOmitido = prod.estado === 'omitido';
                          const subtotal = prod.cantidad * prod.precio_estimado_unitario;
                          const prodOfertas = ofertas.filter(
                            (o) => o.id_producto_lista === prod.id_producto_lista
                          );
                          const prodCreador = usuarios.find((u) => u.id_usuario === prod.id_usuario_creador);

                          return (
                            <div
                              key={prod.id_producto_lista}
                              className={`p-3 rounded-2xl border transition-all ${
                                isComprado
                                  ? 'bg-emerald-950/15 border-emerald-500/20 opacity-80'
                                  : isOmitido
                                  ? 'bg-black/30 border-white/5 opacity-50'
                                  : 'bg-black/40 border-white/10 hover:border-emerald-500/25'
                              }`}
                            >
                              <div className="flex items-start gap-2.5">
                                {/* Status Toggle Checkbox */}
                                <button
                                  onClick={() =>
                                    actualizarEstadoProducto(
                                      prod.id_producto_lista,
                                      isComprado ? 'pendiente' : 'comprado'
                                    )
                                  }
                                  className={`mt-0.5 p-1 rounded-lg transition-colors ${
                                    isComprado
                                      ? 'text-emerald-400 bg-emerald-950/80 border border-emerald-500/40'
                                      : 'text-slate-500 hover:text-slate-300'
                                  }`}
                                  title={isComprado ? 'Marcar pendiente' : 'Marcar comprado'}
                                >
                                  {isComprado ? (
                                    <CheckCircle2 className="w-5 h-5" />
                                  ) : (
                                    <Circle className="w-5 h-5" />
                                  )}
                                </button>

                                {/* Product details */}
                                <div className="flex-1 min-w-0">
                                  <div className="flex items-center justify-between gap-2 mb-1">
                                    <h4
                                      className={`text-xs font-bold truncate ${
                                        isComprado ? 'line-through text-slate-400' : 'text-white'
                                      }`}
                                    >
                                      {prod.nombre}
                                    </h4>
                                    <span className="text-xs font-mono font-bold text-emerald-300 tabular-nums shrink-0">
                                      ${subtotal.toLocaleString()}
                                    </span>
                                  </div>

                                  {prod.descripcion && (
                                    <p className="text-[11px] text-slate-400 line-clamp-1 mb-1.5">
                                      {prod.descripcion}
                                    </p>
                                  )}

                                  <div className="flex flex-wrap items-center gap-1.5 text-[10px] text-slate-400 mb-2">
                                    {getPriorityBadge(prod.prioridad)}
                                    <span aria-hidden="true">·</span>
                                    <span className="font-mono text-slate-300">
                                      {prod.cantidad} x ${prod.precio_estimado_unitario.toLocaleString()}
                                    </span>
                                    {prod.codigo_producto && (
                                      <>
                                        <span aria-hidden="true">·</span>
                                        <span className="font-mono text-slate-400">
                                          {prod.codigo_producto}
                                        </span>
                                      </>
                                    )}
                                    <span aria-hidden="true">·</span>
                                    <span>Por {prodCreador?.nombre.split(' ')[0] || 'Usuario'}</span>
                                  </div>

                                  {/* Offer Banner if available */}
                                  {prodOfertas.length > 0 && (
                                    <div className="mb-2 p-2 rounded-xl bg-emerald-950/50 border border-emerald-500/30 flex items-center justify-between gap-2 text-[11px]">
                                      <div className="flex items-center gap-1.5 text-emerald-300 min-w-0">
                                        <Store className="w-3.5 h-3.5 shrink-0 text-emerald-400" />
                                        <span className="truncate">
                                          {prodOfertas[0].nombre_comercio}
                                          {prodOfertas[0].porcentaje_descuento
                                            ? ` (${prodOfertas[0].porcentaje_descuento}% OFF)`
                                            : ''}
                                        </span>
                                      </div>
                                      {prodOfertas[0].precio_oferta && (
                                        <span className="font-mono font-bold text-emerald-300 shrink-0">
                                          ${prodOfertas[0].precio_oferta.toLocaleString()}
                                        </span>
                                      )}
                                    </div>
                                  )}

                                  {/* Product Action Footers */}
                                  <div className="flex items-center justify-between pt-1 border-t border-white/5 text-[11px]">
                                    <button
                                      onClick={() => setSelectedProductForOffer(prod)}
                                      className="text-emerald-400 hover:text-emerald-300 flex items-center gap-1 font-medium"
                                    >
                                      <Tag className="w-3 h-3" />
                                      <span>
                                        {prodOfertas.length > 0
                                          ? `Ver ${prodOfertas.length} ofertas`
                                          : 'Vincular oferta'}
                                      </span>
                                    </button>

                                    <div className="flex items-center gap-2">
                                      <button
                                        onClick={() =>
                                          actualizarEstadoProducto(
                                            prod.id_producto_lista,
                                            isOmitido ? 'pendiente' : 'omitido'
                                          )
                                        }
                                        className="text-slate-400 hover:text-slate-200 text-[10px]"
                                      >
                                        {isOmitido ? 'Restaurar' : 'Omitir'}
                                      </button>
                                      <button
                                        onClick={() => eliminarProducto(prod.id_producto_lista)}
                                        className="text-rose-400 hover:text-rose-300 p-0.5"
                                        title="Eliminar producto"
                                      >
                                        <Trash2 className="w-3 h-3" />
                                      </button>
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </div>
                          );
                        })}
                      </div>
                    )}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}

      {/* Botón flotante (FAB) en la parte inferior derecha para añadir nuevos productos al seleccionar una lista */}
      {selectedListId && (
        <div className="fixed bottom-24 right-5 z-40 max-w-md mx-auto pointer-events-auto">
          <button
            onClick={() => setShowAddProductModal(true)}
            className="w-14 h-14 rounded-2xl bg-gradient-to-tr from-emerald-500 via-emerald-400 to-teal-300 text-black shadow-[0_4px_25px_rgba(16,185,129,0.5)] border border-emerald-200/50 flex items-center justify-center transition-all hover:scale-105 active:scale-95 group"
            title="Añadir nuevo producto a la lista"
          >
            <Plus className="w-7 h-7 stroke-[2.7] group-hover:rotate-90 transition-transform duration-200" />
          </button>
        </div>
      )}

      {/* Modals */}
      {showAddProductModal && selectedListId && (
        <AddProductModal
          id_lista={selectedListId}
          onClose={() => setShowAddProductModal(false)}
        />
      )}

      {showCreateListModal && (
        <CreateListModal
          onClose={() => setShowCreateListModal(false)}
          onCreated={(newId) => setSelectedListId(newId)}
        />
      )}

      {selectedProductForOffer && (
        <AddOfferModal
          producto={selectedProductForOffer}
          onClose={() => setSelectedProductForOffer(null)}
        />
      )}
    </div>
  );
};
