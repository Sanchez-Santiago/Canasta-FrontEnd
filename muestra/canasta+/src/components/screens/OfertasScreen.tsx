import React, { useState, useMemo } from 'react';
import { useApp } from '../../context/AppContext';
import { Oferta, PrioridadProducto } from '../../types/database';
import {
  Tag,
  MapPin,
  Store,
  Search,
  Plus,
  Percent,
  Check,
  ShoppingCart,
  Calendar,
  Sparkles,
  Filter,
  Navigation,
  ArrowRight,
  TrendingDown,
  Clock,
  Share2,
} from 'lucide-react';

export const OfertasScreen: React.FC = () => {
  const {
    ofertas,
    productos,
    listas,
    activeListas,
    activeGrupo,
    userLocation,
    setUserLocation,
    crearOferta,
    crearProducto,
    setPantallaActiva,
  } = useApp();

  const [searchQuery, setSearchQuery] = useState('');
  const [selectedComercio, setSelectedComercio] = useState<string>('todos');
  const [selectedPrioridad, setSelectedPrioridad] = useState<string>('todas');
  const [sortBy, setSortBy] = useState<'descuento' | 'precio' | 'fecha'>('descuento');

  // Modal para agregar oferta rápida
  const [showAddOfferModal, setShowAddOfferModal] = useState(false);
  const [newComercio, setNewComercio] = useState('');
  const [newProductoNombre, setNewProductoNombre] = useState('');
  const [newDescuento, setNewDescuento] = useState('25');
  const [newPrecioOriginal, setNewPrecioOriginal] = useState('4000');
  const [newPrecioOferta, setNewPrecioOferta] = useState('3000');
  const [newPrioridad, setNewPrioridad] = useState<PrioridadProducto>('esencial');
  const [newUbicacion, setNewUbicacion] = useState('Av. Colón 2400');
  const [newDescripcion, setNewDescripcion] = useState('');

  // Toast / feedback state
  const [addedSuccessId, setAddedSuccessId] = useState<number | null>(null);

  // Extraer lista de comercios únicos
  const availableComercios = useMemo(() => {
    const set = new Set<string>();
    ofertas.forEach((o) => {
      if (o.nombre_comercio) set.add(o.nombre_comercio);
    });
    return Array.from(set);
  }, [ofertas]);

  // Enriquecer ofertas con producto vinculado
  const enrichedOfertas = useMemo(() => {
    return ofertas.map((o) => {
      const prod = productos.find((p) => p.id_producto_lista === o.id_producto_lista);
      const ahorro = (o.precio_original || 0) - (o.precio_oferta || 0);
      return {
        ...o,
        producto: prod,
        ahorroCalculado: ahorro > 0 ? ahorro : 0,
      };
    });
  }, [ofertas, productos]);

  // Filtrado y ordenamiento
  const filteredOfertas = useMemo(() => {
    return enrichedOfertas
      .filter((o) => {
        // Filtro comercio
        if (selectedComercio !== 'todos' && o.nombre_comercio !== selectedComercio) {
          return false;
        }

        // Filtro prioridad del producto
        if (selectedPrioridad !== 'todas') {
          if (!o.producto || o.producto.prioridad !== selectedPrioridad) {
            return false;
          }
        }

        // Búsqueda
        if (searchQuery.trim()) {
          const q = searchQuery.toLowerCase();
          const matchComercio = o.nombre_comercio.toLowerCase().includes(q);
          const matchProd = o.producto?.nombre.toLowerCase().includes(q);
          const matchDesc = o.descripcion?.toLowerCase().includes(q);
          const matchUbi = o.ubicacion?.toLowerCase().includes(q);
          if (!matchComercio && !matchProd && !matchDesc && !matchUbi) {
            return false;
          }
        }

        return true;
      })
      .sort((a, b) => {
        if (sortBy === 'descuento') {
          return (b.porcentaje_descuento || 0) - (a.porcentaje_descuento || 0);
        }
        if (sortBy === 'precio') {
          return (a.precio_oferta || 0) - (b.precio_oferta || 0);
        }
        return new Date(a.fecha_fin).getTime() - new Date(b.fecha_fin).getTime();
      });
  }, [enrichedOfertas, selectedComercio, selectedPrioridad, searchQuery, sortBy]);

  // Agregar directamente producto con oferta a la lista activa
  const handleAddOfferToList = (oferta: typeof enrichedOfertas[0]) => {
    const targetList = activeListas[0];
    if (!targetList) {
      alert('Primero crea o activa una lista de compras en la pestaña "Listas".');
      return;
    }

    const prodNombre = oferta.producto?.nombre || `Oferta: ${oferta.nombre_comercio}`;
    const prodPrioridad = oferta.producto?.prioridad || 'esencial';
    const prodPrecio = oferta.precio_oferta || oferta.precio_original || 2000;

    crearProducto({
      id_lista: targetList.id_lista,
      nombre: prodNombre,
      descripcion: `Comprado en ${oferta.nombre_comercio} (${oferta.porcentaje_descuento || ''}% OFF)`,
      prioridad: prodPrioridad,
      cantidad: 1,
      precio_estimado_unitario: prodPrecio,
      codigo_producto: `OFR-${Math.floor(1000 + Math.random() * 9000)}`,
    });

    setAddedSuccessId(oferta.id_oferta);
    setTimeout(() => {
      setAddedSuccessId(null);
    }, 2500);
  };

  // Crear nueva oferta
  const handleSaveNewOffer = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newComercio.trim() || !newProductoNombre.trim()) return;

    // Buscar o usar el primer producto de la lista
    const firstProd = productos[0];
    const targetProdId = firstProd ? firstProd.id_producto_lista : 1;

    const pOrig = parseInt(newPrecioOriginal.replace(/\D/g, ''), 10) || 4000;
    const pOff = parseInt(newPrecioOferta.replace(/\D/g, ''), 10) || 3000;
    const pDesc = parseFloat(newDescuento) || 25;

    const now = new Date();
    const nextWeek = new Date(now.getTime() + 10 * 24 * 60 * 60 * 1000);

    crearOferta({
      id_producto_lista: targetProdId,
      nombre_comercio: newComercio.trim(),
      ubicacion: newUbicacion.trim() || `${userLocation.localidad}`,
      localidad: userLocation.localidad,
      provincia: userLocation.provincia,
      fecha_inicio: now.toISOString().split('T')[0],
      fecha_fin: nextWeek.toISOString().split('T')[0],
      porcentaje_descuento: pDesc,
      precio_original: pOrig,
      precio_oferta: pOff,
      descripcion: newDescripcion.trim() || `Oferta especial en ${newProductoNombre.trim()}`,
    });

    setShowAddOfferModal(false);
  };

  return (
    <div className="space-y-4 pb-24 relative min-h-[calc(100vh-14rem)]">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-extrabold text-white tracking-tight flex items-center gap-2">
            <span>Radar de Ofertas</span>
            <span className="text-xs px-2 py-0.5 rounded-full bg-emerald-500/20 text-emerald-300 font-mono">
              {filteredOfertas.length} activas
            </span>
          </h2>
          <p className="text-xs text-slate-400">
            Ahorro inteligente y promociones geolocalizadas
          </p>
        </div>

        <button
          onClick={() => setShowAddOfferModal(true)}
          className="py-2 px-3 rounded-xl bg-gradient-to-r from-emerald-500 to-teal-600 text-black text-xs font-bold flex items-center gap-1.5 shadow-lg shadow-emerald-950/60 hover:from-emerald-400 hover:to-teal-500 transition-all active:scale-95"
        >
          <Plus className="w-4 h-4 stroke-[2.5]" />
          <span>Cargar Oferta</span>
        </button>
      </div>

      {/* Location Banner (Glassmorphic Bar) */}
      <div className="p-3.5 rounded-2xl glass-panel border border-emerald-500/25 flex items-center justify-between text-xs">
        <div className="flex items-center gap-2.5">
          <div className="p-2 rounded-xl bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
            <Navigation className="w-4 h-4 animate-pulse" />
          </div>
          <div>
            <div className="flex items-center gap-1.5">
              <span className="text-[11px] font-bold text-white">Comercios cercanos en:</span>
              <span className="text-[10px] text-emerald-400 font-semibold bg-emerald-950/80 px-1.5 py-0.2 rounded border border-emerald-500/30">
                GPS Activo
              </span>
            </div>
            <p className="text-xs text-slate-300 font-medium">
              {userLocation.localidad}, {userLocation.provincia}
            </p>
          </div>
        </div>

        <button
          onClick={() => {
            const nuevasLocs = ['Córdoba Capital', 'Villa Carlos Paz', 'Rosario', 'Buenos Aires'];
            const nextLoc =
              nuevasLocs[(nuevasLocs.indexOf(userLocation.localidad) + 1) % nuevasLocs.length];
            setUserLocation({
              localidad: nextLoc,
              provincia: nextLoc === 'Buenos Aires' ? 'Buenos Aires' : 'Córdoba',
            });
          }}
          className="text-[11px] text-emerald-400 hover:text-emerald-300 font-semibold py-1.5 px-2.5 rounded-xl bg-emerald-950/70 border border-emerald-500/30"
        >
          Cambiar Zona
        </button>
      </div>

      {/* Search and Filters */}
      <div className="space-y-2">
        {/* Search Bar */}
        <div className="relative">
          <Search className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
          <input
            type="text"
            placeholder="Buscar por supermercado, producto o marca..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full py-2.5 pl-9 pr-3 rounded-2xl glass-input text-xs"
          />
        </div>

        {/* Priority Filter Chips */}
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 text-xs">
          <button
            onClick={() => setSelectedPrioridad('todas')}
            className={`py-1.5 px-3 rounded-xl border text-[11px] whitespace-nowrap transition-all ${
              selectedPrioridad === 'todas'
                ? 'bg-emerald-950/90 border-emerald-500/60 text-emerald-300 font-bold shadow-sm'
                : 'bg-black/40 border-white/10 text-slate-400 hover:text-white'
            }`}
          >
            Todas las Prioridades
          </button>
          <button
            onClick={() => setSelectedPrioridad('esencial')}
            className={`py-1.5 px-3 rounded-xl border text-[11px] whitespace-nowrap transition-all ${
              selectedPrioridad === 'esencial'
                ? 'bg-emerald-950/90 border-emerald-400 text-emerald-300 font-bold shadow-sm'
                : 'bg-black/40 border-white/10 text-slate-400 hover:text-white'
            }`}
          >
            Esenciales
          </button>
          <button
            onClick={() => setSelectedPrioridad('prioritario')}
            className={`py-1.5 px-3 rounded-xl border text-[11px] whitespace-nowrap transition-all ${
              selectedPrioridad === 'prioritario'
                ? 'bg-teal-950/90 border-teal-400 text-teal-300 font-bold shadow-sm'
                : 'bg-black/40 border-white/10 text-slate-400 hover:text-white'
            }`}
          >
            Primarias / Prioritarias
          </button>
          <button
            onClick={() => setSelectedPrioridad('secundario')}
            className={`py-1.5 px-3 rounded-xl border text-[11px] whitespace-nowrap transition-all ${
              selectedPrioridad === 'secundario'
                ? 'bg-slate-900 border-slate-400 text-slate-200 font-bold shadow-sm'
                : 'bg-black/40 border-white/10 text-slate-400 hover:text-white'
            }`}
          >
            Secundarias
          </button>
        </div>

        {/* Store selector & Sort */}
        <div className="grid grid-cols-2 gap-2">
          <select
            value={selectedComercio}
            onChange={(e) => setSelectedComercio(e.target.value)}
            className="w-full py-2 px-2.5 rounded-xl glass-input text-xs"
          >
            <option value="todos" className="bg-[#0e1612]">
              Todos los Comercios
            </option>
            {availableComercios.map((com) => (
              <option key={com} value={com} className="bg-[#0e1612]">
                {com}
              </option>
            ))}
          </select>

          <select
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value as any)}
            className="w-full py-2 px-2.5 rounded-xl glass-input text-xs"
          >
            <option value="descuento" className="bg-[#0e1612]">
              Mayor Descuento (%)
            </option>
            <option value="precio" className="bg-[#0e1612]">
              Menor Precio ($)
            </option>
            <option value="fecha" className="bg-[#0e1612]">
              Próximas a vencer
            </option>
          </select>
        </div>
      </div>

      {/* Offers Cards Grid */}
      <div className="space-y-3">
        {filteredOfertas.length === 0 ? (
          <div className="p-8 text-center rounded-3xl bento-card border border-white/5 my-4">
            <Tag className="w-8 h-8 text-slate-600 mx-auto mb-2" />
            <h4 className="text-xs font-bold text-white mb-1">No se encontraron ofertas</h4>
            <p className="text-[11px] text-slate-400">
              Prueba cambiando los filtros de comercio o añade una nueva oferta con el botón superior.
            </p>
          </div>
        ) : (
          filteredOfertas.map((oferta) => {
            const hasAdded = addedSuccessId === oferta.id_oferta;

            return (
              <div
                key={oferta.id_oferta}
                className="p-4 rounded-3xl bento-card border border-white/[0.08] shadow-xl hover:border-emerald-500/30 transition-all relative overflow-hidden group"
              >
                {/* Subtle illumination */}
                <div className="absolute top-0 right-0 w-28 h-28 bg-emerald-900/10 rounded-full blur-2xl pointer-events-none group-hover:bg-emerald-900/20 transition-all" />

                <div className="flex items-start justify-between gap-3 mb-2">
                  <div className="flex items-center gap-2 min-w-0">
                    <div className="p-2 rounded-xl bg-slate-900 border border-white/10 text-emerald-400 shrink-0">
                      <Store className="w-4 h-4" />
                    </div>
                    <div className="min-w-0">
                      <h4 className="text-sm font-bold text-white truncate">
                        {oferta.nombre_comercio}
                      </h4>
                      <p className="text-[11px] text-slate-400 flex items-center gap-1 truncate">
                        <MapPin className="w-3 h-3 text-emerald-400 shrink-0" />
                        {oferta.ubicacion || oferta.localidad}
                      </p>
                    </div>
                  </div>

                  {oferta.porcentaje_descuento && (
                    <div className="px-2.5 py-1 rounded-xl bg-emerald-950/90 border border-emerald-500/30 text-emerald-300 font-extrabold text-xs shadow-md shrink-0 flex items-center gap-0.5">
                      <Percent className="w-3 h-3 stroke-[2.5]" />
                      <span>{oferta.porcentaje_descuento} OFF</span>
                    </div>
                  )}
                </div>

                {/* Product Title and Priority */}
                <div className="mb-2.5">
                  <div className="flex items-center gap-2 mb-0.5">
                    <h3 className="text-sm font-bold text-white">
                      {oferta.producto?.nombre || 'Producto en Promoción'}
                    </h3>
                    {oferta.producto?.prioridad && (
                      <span className="text-[9px] font-semibold text-emerald-300 bg-emerald-950/80 border border-emerald-500/30 px-1.5 py-0.2 rounded uppercase">
                        {oferta.producto.prioridad}
                      </span>
                    )}
                  </div>
                  {oferta.descripcion && (
                    <p className="text-xs text-slate-300/90 leading-relaxed">
                      {oferta.descripcion}
                    </p>
                  )}
                </div>

                {/* Price & Savings Row */}
                <div className="p-2.5 rounded-2xl bg-black/40 border border-white/5 flex items-center justify-between mb-3">
                  <div>
                    <span className="text-[10px] text-slate-400 block">Precio Oferta</span>
                    <div className="flex items-baseline gap-2">
                      <span className="text-base font-mono font-extrabold text-emerald-300 tabular-nums">
                        ${(oferta.precio_oferta || 0).toLocaleString()}
                      </span>
                      {oferta.precio_original && (
                        <span className="text-xs font-mono line-through text-slate-400 tabular-nums">
                          ${oferta.precio_original.toLocaleString()}
                        </span>
                      )}
                    </div>
                  </div>

                  {oferta.ahorroCalculado > 0 && (
                    <div className="text-right">
                      <span className="text-[10px] text-slate-400 block">Ahorro Estimado</span>
                      <span className="text-xs font-mono font-bold text-teal-400 tabular-nums">
                        -${oferta.ahorroCalculado.toLocaleString()}
                      </span>
                    </div>
                  )}
                </div>

                {/* Card Footer: Expiration date & Direct Add to Shopping List */}
                <div className="flex items-center justify-between pt-1 border-t border-white/10 text-xs">
                  <div className="flex items-center gap-1 text-[11px] text-slate-400">
                    <Calendar className="w-3 h-3 text-slate-500" />
                    <span>Hasta {oferta.fecha_fin}</span>
                  </div>

                  <button
                    onClick={() => handleAddOfferToList(oferta)}
                    className={`py-1.5 px-3 rounded-2xl font-bold text-xs flex items-center gap-1.5 transition-all shadow-sm ${
                      hasAdded
                        ? 'bg-emerald-600 text-white'
                        : 'bg-emerald-950/70 border border-emerald-500/30 text-emerald-300 hover:bg-emerald-900/80 hover:border-emerald-400'
                    }`}
                  >
                    {hasAdded ? (
                      <>
                        <Check className="w-3.5 h-3.5 stroke-[3]" />
                        <span>¡Agregado a la lista!</span>
                      </>
                    ) : (
                      <>
                        <ShoppingCart className="w-3.5 h-3.5" />
                        <span>Agregar a mi Lista</span>
                      </>
                    )}
                  </button>
                </div>
              </div>
            );
          })
        )}
      </div>

      {/* Modal Cargar Nueva Oferta */}
      {showAddOfferModal && (
        <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/80 backdrop-blur-sm animate-in fade-in duration-200">
          <div className="w-full max-w-md bg-[#0e1612] border-t sm:border border-emerald-500/30 rounded-t-3xl sm:rounded-2xl p-5 shadow-2xl max-h-[88vh] overflow-y-auto">
            <div className="w-10 h-1 bg-slate-700/60 rounded-full mx-auto mb-3" />

            <div className="flex items-center justify-between mb-3">
              <div>
                <h3 className="text-base font-bold text-white">Registrar Oferta Encontrada</h3>
                <p className="text-xs text-slate-400">Comparte promociones con tu grupo</p>
              </div>
              <button
                onClick={() => setShowAddOfferModal(false)}
                className="p-1.5 rounded-lg text-slate-400 hover:text-white"
              >
                ✕
              </button>
            </div>

            <form onSubmit={handleSaveNewOffer} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">
                  Comercio / Supermercado *
                </label>
                <input
                  type="text"
                  required
                  placeholder="Ej. Coto, Carrefour, Día, Super Mami"
                  value={newComercio}
                  onChange={(e) => setNewComercio(e.target.value)}
                  className="w-full py-2 px-3 rounded-xl glass-input text-xs"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">
                  Producto en Oferta *
                </label>
                <input
                  type="text"
                  required
                  placeholder="Ej. Aceite de Oliva 500ml, Asado, Café"
                  value={newProductoNombre}
                  onChange={(e) => setNewProductoNombre(e.target.value)}
                  className="w-full py-2 px-3 rounded-xl glass-input text-xs"
                />
              </div>

              <div className="grid grid-cols-3 gap-2">
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">
                    % Descuento
                  </label>
                  <input
                    type="number"
                    value={newDescuento}
                    onChange={(e) => setNewDescuento(e.target.value)}
                    className="w-full py-2 px-2 rounded-xl glass-input text-xs font-mono font-bold text-emerald-300"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">
                    Precio Normal ($)
                  </label>
                  <input
                    type="number"
                    value={newPrecioOriginal}
                    onChange={(e) => setNewPrecioOriginal(e.target.value)}
                    className="w-full py-2 px-2 rounded-xl glass-input text-xs font-mono"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">
                    Precio Oferta ($)
                  </label>
                  <input
                    type="number"
                    value={newPrecioOferta}
                    onChange={(e) => setNewPrecioOferta(e.target.value)}
                    className="w-full py-2 px-2 rounded-xl glass-input text-xs font-mono font-bold text-emerald-400"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">
                  Prioridad del Producto
                </label>
                <div className="grid grid-cols-3 gap-2">
                  <button
                    type="button"
                    onClick={() => setNewPrioridad('esencial')}
                    className={`py-1.5 px-1 text-center rounded-xl text-xs font-semibold border ${
                      newPrioridad === 'esencial'
                        ? 'bg-emerald-950 border-emerald-400 text-emerald-300'
                        : 'bg-black/40 border-white/10 text-slate-400'
                    }`}
                  >
                    Esencial
                  </button>
                  <button
                    type="button"
                    onClick={() => setNewPrioridad('prioritario')}
                    className={`py-1.5 px-1 text-center rounded-xl text-xs font-semibold border ${
                      newPrioridad === 'prioritario'
                        ? 'bg-teal-950 border-teal-400 text-teal-300'
                        : 'bg-black/40 border-white/10 text-slate-400'
                    }`}
                  >
                    Prioritario
                  </button>
                  <button
                    type="button"
                    onClick={() => setNewPrioridad('secundario')}
                    className={`py-1.5 px-1 text-center rounded-xl text-xs font-semibold border ${
                      newPrioridad === 'secundario'
                        ? 'bg-slate-800 border-slate-400 text-slate-200'
                        : 'bg-black/40 border-white/10 text-slate-400'
                    }`}
                  >
                    Secundario
                  </button>
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">
                  Ubicación / Sucursal
                </label>
                <input
                  type="text"
                  placeholder="Ej. Av. Colón 2400"
                  value={newUbicacion}
                  onChange={(e) => setNewUbicacion(e.target.value)}
                  className="w-full py-2 px-3 rounded-xl glass-input text-xs"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">
                  Detalle de la Promoción
                </label>
                <input
                  type="text"
                  placeholder="Ej. Llevando 2 unidades o pagando con app"
                  value={newDescripcion}
                  onChange={(e) => setNewDescripcion(e.target.value)}
                  className="w-full py-2 px-3 rounded-xl glass-input text-xs"
                />
              </div>

              <div className="flex gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setShowAddOfferModal(false)}
                  className="flex-1 py-2 px-3 rounded-xl border border-white/10 text-slate-300 text-xs"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2 px-3 rounded-xl bg-gradient-to-r from-emerald-500 to-teal-600 text-black font-bold text-xs shadow-md shadow-emerald-950/60"
                >
                  Guardar Oferta
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
