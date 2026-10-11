import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { ProductoLista, Oferta } from '../../types/database';
import { Tag, MapPin, Plus, Store, Check, Percent, X, Calendar } from 'lucide-react';

interface AddOfferModalProps {
  producto: ProductoLista;
  onClose: () => void;
}

export const AddOfferModal: React.FC<AddOfferModalProps> = ({ producto, onClose }) => {
  const { ofertas, crearOferta, userLocation } = useApp();

  const [isCreating, setIsCreating] = useState(false);
  const [comercio, setComercio] = useState('');
  const [ubicacion, setUbicacion] = useState('');
  const [localidad, setLocalidad] = useState(userLocation.localidad || 'Córdoba Capital');
  const [provincia, setProvincia] = useState(userLocation.provincia || 'Córdoba');
  const [descuento, setDescuento] = useState('20');
  const [precioOriginal, setPrecioOriginal] = useState(
    producto.precio_estimado_unitario ? String(producto.precio_estimado_unitario) : '3000'
  );
  const [precioOferta, setPrecioOferta] = useState('');
  const [descripcion, setDescripcion] = useState('');

  // Ofertas existentes para este producto
  const productOffers = ofertas.filter((o) => o.id_producto_lista === producto.id_producto_lista);

  const handlePriceChange = (orig: string, desc: string) => {
    const pOrig = parseInt(orig.replace(/\D/g, ''), 10) || 0;
    const pDesc = parseFloat(desc) || 0;
    if (pOrig > 0 && pDesc > 0) {
      const pOff = Math.round(pOrig * (1 - pDesc / 100));
      setPrecioOferta(String(pOff));
    }
  };

  const handleCreateOffer = (e: React.FormEvent) => {
    e.preventDefault();
    if (!comercio.trim()) return;

    const pOrig = parseInt(precioOriginal.replace(/\D/g, ''), 10) || undefined;
    const pOff = precioOferta ? parseInt(precioOferta.replace(/\D/g, ''), 10) : undefined;
    const pDesc = descuento ? parseFloat(descuento) : undefined;

    const now = new Date();
    const nextWeek = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000);

    crearOferta({
      id_producto_lista: producto.id_producto_lista,
      nombre_comercio: comercio.trim(),
      ubicacion: ubicacion.trim() || undefined,
      localidad: localidad.trim(),
      provincia: provincia.trim(),
      fecha_inicio: now.toISOString().split('T')[0],
      fecha_fin: nextWeek.toISOString().split('T')[0],
      porcentaje_descuento: pDesc,
      precio_original: pOrig,
      precio_oferta: pOff,
      descripcion: descripcion.trim() || undefined,
    });

    setIsCreating(false);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/80 backdrop-blur-sm animate-in fade-in duration-200">
      <div className="w-full max-w-md bg-[#0e1612] border-t sm:border border-emerald-500/30 rounded-t-3xl sm:rounded-2xl p-5 shadow-2xl max-h-[88vh] overflow-y-auto">
        <div className="w-10 h-1 bg-slate-700/60 rounded-full mx-auto mb-3" />

        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2">
            <div className="p-2 rounded-xl bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
              <Tag className="w-5 h-5" />
            </div>
            <div>
              <h3 className="text-base font-bold text-white">Ofertas y Descuentos</h3>
              <p className="text-xs text-slate-400 truncate max-w-[200px]">{producto.nombre}</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-white/10"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Info de ubicación activa */}
        <div className="mb-3.5 p-2.5 rounded-xl bg-black/40 border border-white/5 flex items-center justify-between text-xs">
          <div className="flex items-center gap-1.5 text-slate-300">
            <MapPin className="w-3.5 h-3.5 text-emerald-400" />
            <span>Tu ubicación: <strong className="text-white">{userLocation.localidad}</strong></span>
          </div>
          <span className="text-[10px] text-emerald-400 font-medium">GPS Activo</span>
        </div>

        {!isCreating ? (
          <div className="space-y-3">
            {productOffers.length === 0 ? (
              <div className="p-6 text-center rounded-2xl bg-black/30 border border-white/5 text-slate-400 text-xs">
                No hay ofertas registradas para este producto todavía.
                <p className="text-slate-500 text-[11px] mt-1">
                  Puedes registrar un descuento o promoción que viste en un supermercado local.
                </p>
              </div>
            ) : (
              <div className="space-y-2.5">
                {productOffers.map((oferta) => (
                  <div
                    key={oferta.id_oferta}
                    className="p-3.5 rounded-2xl bg-emerald-950/40 border border-emerald-500/40 shadow-sm"
                  >
                    <div className="flex items-start justify-between gap-2 mb-1.5">
                      <div className="flex items-center gap-1.5">
                        <Store className="w-4 h-4 text-emerald-400 shrink-0" />
                        <h4 className="font-bold text-sm text-white">{oferta.nombre_comercio}</h4>
                      </div>
                      {oferta.porcentaje_descuento && (
                        <span className="px-2 py-0.5 rounded-lg bg-emerald-500 text-black font-extrabold text-xs">
                          {oferta.porcentaje_descuento}% OFF
                        </span>
                      )}
                    </div>

                    {oferta.descripcion && (
                      <p className="text-xs text-slate-300 mb-2 leading-relaxed">
                        {oferta.descripcion}
                      </p>
                    )}

                    <div className="flex items-center justify-between text-xs pt-1 border-t border-emerald-500/20">
                      <div className="flex items-center gap-1 text-[11px] text-slate-400">
                        <MapPin className="w-3 h-3 text-emerald-400" />
                        <span>{oferta.ubicacion || oferta.localidad}</span>
                      </div>
                      <div className="text-right">
                        {oferta.precio_original && (
                          <span className="text-[11px] line-through text-slate-400 mr-1.5 font-mono">
                            ${oferta.precio_original.toLocaleString()}
                          </span>
                        )}
                        {oferta.precio_oferta && (
                          <span className="text-xs font-mono font-bold text-emerald-400 tabular-nums">
                            ${oferta.precio_oferta.toLocaleString()}
                          </span>
                        )}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}

            <button
              onClick={() => setIsCreating(true)}
              className="w-full py-2.5 px-4 rounded-xl border border-dashed border-emerald-500/40 bg-emerald-950/30 hover:bg-emerald-950/60 text-emerald-300 text-xs font-semibold flex items-center justify-center gap-2 transition-all mt-3"
            >
              <Plus className="w-4 h-4" />
              <span>Registrar Nueva Oferta para este Producto</span>
            </button>
          </div>
        ) : (
          <form onSubmit={handleCreateOffer} className="space-y-3">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">
                Comercio / Supermercado *
              </label>
              <input
                type="text"
                required
                placeholder="Ej. Coto, Carrefour, Super Mami, Verdulería"
                value={comercio}
                onChange={(e) => setComercio(e.target.value)}
                className="w-full py-2 px-3 rounded-xl glass-input text-xs"
                autoFocus
              />
            </div>

            <div className="grid grid-cols-2 gap-2.5">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">
                  Localidad
                </label>
                <input
                  type="text"
                  value={localidad}
                  onChange={(e) => setLocalidad(e.target.value)}
                  className="w-full py-2 px-3 rounded-xl glass-input text-xs"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">
                  Sucursal / Dirección
                </label>
                <input
                  type="text"
                  placeholder="Ej. Av. Colón 1200"
                  value={ubicacion}
                  onChange={(e) => setUbicacion(e.target.value)}
                  className="w-full py-2 px-3 rounded-xl glass-input text-xs"
                />
              </div>
            </div>

            <div className="grid grid-cols-3 gap-2">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">
                  % Descuento
                </label>
                <input
                  type="number"
                  min="1"
                  max="100"
                  value={descuento}
                  onChange={(e) => {
                    setDescuento(e.target.value);
                    handlePriceChange(precioOriginal, e.target.value);
                  }}
                  className="w-full py-2 px-2.5 rounded-xl glass-input text-xs font-mono font-bold text-emerald-300"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">
                  Precio Normal ($)
                </label>
                <input
                  type="number"
                  value={precioOriginal}
                  onChange={(e) => {
                    setPrecioOriginal(e.target.value);
                    handlePriceChange(e.target.value, descuento);
                  }}
                  className="w-full py-2 px-2.5 rounded-xl glass-input text-xs font-mono"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">
                  Precio Oferta ($)
                </label>
                <input
                  type="number"
                  value={precioOferta}
                  onChange={(e) => setPrecioOferta(e.target.value)}
                  className="w-full py-2 px-2.5 rounded-xl glass-input text-xs font-mono font-bold text-emerald-400"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">
                Detalle o Condición
              </label>
              <input
                type="text"
                placeholder="Ej. 2da unidad al 70%, abonando con débito"
                value={descripcion}
                onChange={(e) => setDescripcion(e.target.value)}
                className="w-full py-2 px-3 rounded-xl glass-input text-xs"
              />
            </div>

            <div className="flex gap-2 pt-2">
              <button
                type="button"
                onClick={() => setIsCreating(false)}
                className="flex-1 py-2 px-3 rounded-xl border border-white/10 text-slate-300 text-xs font-medium"
              >
                Cancelar
              </button>
              <button
                type="submit"
                className="flex-1 py-2 px-3 rounded-xl bg-gradient-to-r from-emerald-500 to-teal-600 text-black font-semibold text-xs shadow-md shadow-emerald-950/60"
              >
                Vincular Oferta
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};
