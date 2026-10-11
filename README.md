# Canasta-FrontEnd

Aquí tienes la descripción técnica, visual y funcional exhaustiva de Canasta+, detallando su arquitectura visual, paleta cromática, layout de componentes y cada una de sus pantallas.
1. Sistema Visual y Filosofía de Diseño

   Estética Principal: Bento Grid + Glassmorfismo Ahumado (Smoked Frosted Glass).

   Base Dark Mode: El fondo general utiliza tonos carbón y obsidiana (#050806 y #090e0b), optimizados para pantallas OLED, evitando el negro plano absoluto y empleando luz ambiental difusa en capas de desenfoque (blur-[120px]).

   Tratamiento del Vidrio (Glassmorphism):

        Gradientes lineales en ángulos de 145° a 155°: linear-gradient(rgba(16, 24, 19, 0.72), rgba(10, 15, 12, 0.85)).

        Desenfoque de fondo profundo: backdrop-filter: blur(18px) a blur(24px).

        Bordes sutiles (hairline): border: 1px solid rgba(255, 255, 255, 0.07) con resaltados especulares en verde esmeralda translúcido (rgba(52, 211, 153, 0.25)).

   Paleta Cromática Exacta:

        Verde Esmeralda Bosque / Salvia (#34d399 / #10b981): Color primario de marca, acentos de selección y gastos esenciales.

        Cian Glaciar (#38bdf8): Identificador de gastos Primarios / Prioritarios.

        Ocre / Ámbar Cálido (#facc15): Identificador de gastos Secundarios.

        Rosa / Coral Muted (#fb7185): Egresos reales y alertas sin estridencia.

        Pizarra Ahumado (#334155): Presupuesto disponible remanente y superficies de contraste.

   Tipografía:

        Títulos e interfaz: Plus Jakarta Sans.

        Precios, porcentajes y fechas: JetBrains Mono con alineación vertical perfecta mediante tabular-nums.

2. Estructura del Shell Móvil y Cabecera Persistente

La aplicación está contenida en un marco que emula un smartphone Android de gama alta (max-w-md, bordes redondeados rounded-[36px], sombra de 50px):

    Barra de Estado de Android (Parte Superior, 28px de alto):

        Izquierda: Reloj en tiempo real (font-mono font-semibold text-slate-300).

        Centro: Cámara frontal (punch hole notch) circular con sombra interior.

        Derecha: Indicadores de red móvil (Signal), WiFi (Wifi) y porcentaje de batería en verde esmeralda (BatteryMedium al 87%).

    TopHeader Persistente (Visible en todas las pantallas):

        Fila Superior:

            Isotipo: Bloque redondeado de 32x32px con gradiente esmeralda y monograma "C+".

            Nombre: Título Canasta+ junto a una micro-etiqueta que indica si el grupo actual es Personal o Grupal.

            Subtítulo: Nombre truncado del grupo en uso.

            Acciones Derecha: Campana de notificaciones con insignia numérica animada cuando hay alertas sin leer + avatar circular del usuario activo.

        Fila Inferior (Controles de Modo):

            Switch Segmentado: Conmutador deslizable con botones Individual (icono User) y Grupal (icono Users).

            Selector de Grupo (Solo en Modo Grupal): Botón con el nombre del grupo activo y flecha desplegable (ChevronDown) para abrir el selector de grupos.

            Botón [ + Invitar ] (Solo en Modo Grupal): Botón destacado con icono UserPlus que abre de forma inmediata el modal para invitar miembros al grupo.

3. Barra de Navegación Inferior (BottomNav)

Anclada en la base (fixed bottom-0, 64px de alto) con acabado en vidrio ahumado y micro-borde superior:

    Distribución en 5 Columnas Simétricas:

        Resumen: Icono Wallet (Billetera / Balance).

        Listas: Icono ClipboardList (Listas de compras).

        Ofertas: Icono BadgePercent (Radar de promociones).

        Movimientos: Icono ReceiptText (Historial de ingresos/egresos).

        Perfil: Icono CircleUserRound (Gestión de cuenta y grupo).

    Indicador de Estado Activo: Barra luminosa horizontal de 20px en la parte superior del icono seleccionado y resaltado en verde esmeralda.

    Barra de Gestos de Android: Píldora horizontal gris (w-28 h-1) en la base inferior.

4. Detalle Pantalla por Pantalla
   Pantalla 1: Resumen (Dashboard Financiero en Bento Grid)

   Bento Card 1 (Hero - 2 Columnas de Ancho):

        Selector de Vista: Botones pequeños para alternar el gráfico entre Vista Completa (Gasto real + Esencial + Primario + Secundario + Disponible) y Solo Estimados (Proporción entre prioridades).

        Gráfico Donut Segmentado (SVG Circular):

            Arcos matemáticos exactos coloreados por segmento: Rosa (Gasto real), Esmeralda (Esenciales), Cian (Primarios), Ámbar (Secundarios), Slate (Disponible).

            Al tocar cualquier categoría en el gráfico o en las tarjetas inferiores, el centro del donut cambia interactivamente mostrando el nombre de la categoría, su monto en pesos y su porcentaje relativo.

        Bloques Laterales KPI: Saldo Neto mensual y Presupuesto Disponible.

   Bento Cards 2 y 3 (Tarjetas Simétricas de 1 Columna):

        Card de Ingresos: Icono ArrowDownLeft, total de ingresos en verde esmeralda (+$210.000).

        Card de Gastos: Icono ArrowUpRight, total gastado en rosa suave (-$68.400).

   Bento Card 4 (Tarjeta de Integrantes y Colaboración):

        Muestra el nombre del grupo, cantidad de integrantes activos y el botón directo "Invitar" con icono UserPlus.

   Bento Card 5 (Desglose de Estimaciones por Prioridad):

        Monto total estimado necesario para las compras pendientes.

        Tres mini-barras horizontales con métricas exactas:

            Esenciales: Barra esmeralda (#34d399), cantidad de ítems y costo total.

            Primarios: Barra cian (#38bdf8), cantidad de ítems y costo total.

            Secundarios: Barra ámbar (#facc15), cantidad de ítems y costo total.

   Bento Card 6 (Banner de Ofertas Cercanas):

        Acceso directo con icono BadgePercent que informa la cantidad de ofertas activas y redirige a la pantalla de Ofertas.

   Bento Card 7 (Feed de Actividad Reciente):

        Lista cronológica de los últimos movimientos con el usuario que los cargó, fecha y monto formateado.

Pantalla 2: Listas de Compras

    Encabezado: Contador de listas activas y botón superior "+ Nueva Lista".

    Filtro de Estado Segmentado: Pestañas Todas, Activas y Completadas.

    Tarjetas Acordeón de Listas (Bento-style):

        Al estar cerrada: Muestra nombre, descripción, presupuesto estimado calculado automáticamente, relación de ítems comprados/totales (ej. 2/5 comprados) y usuario creador.

        Al desplegarse:

            Barra de herramientas para marcar la lista como completada o eliminarla.

            Lista de productos con casillas interactivas (CheckCircle2 / Circle) para alternar entre Pendiente y Comprado.

            Etiquetas de prioridad coloreadas: Esencial, Prioritario o Secundario.

            Subtotal calculado por producto (cantidad × precio_unitario).

            Insignia de oferta vinculada con comercio y precio promocional si existe.

            Botón para vincular o ver ofertas para ese producto específico.

    Botón Flotante de Acción (FAB):

        Ubicado en la esquina inferior derecha (fixed bottom-24 right-5).

        Botón circular con icono Plus (+) que se activa al tener una lista seleccionada para añadir nuevos productos mediante un modal completo (nombre, cantidad con selector numérico, prioridad, precio estimado y código de barras).

Pantalla 3: Radar de Ofertas

    Encabezado y Botón "Cargar Oferta": Permite a cualquier integrante registrar promociones detectadas en el supermercado.

    Barra de Geolocalización Activa: Muestra la localidad y provincia actual del usuario (ej. Córdoba Capital, Córdoba) con indicador GPS y botón para alternar rápidamente entre zonas.

    Buscador en Vivo y Filtros de Comercio:

        Input de búsqueda por nombre de producto, marca o tienda.

        Selector desplegable de comercios (Hipermercado Libertad, Super Mami, Día+, Carrefour, etc.).

        Chips horizontales de prioridad (Esenciales, Primarias, Secundarias).

        Selector de ordenamiento: Mayor Descuento (%), Menor Precio ($) o Próximas a vencer.

    Tarjetas Bento de Ofertas:

        Nombre y dirección del comercio con icono Store.

        Insignia destacada de porcentaje de descuento (ej. 25% OFF).

        Nombre del producto con etiqueta de prioridad.

        Precio original tachado junto al precio de oferta en verde esmeralda.

        Cálculo de ahorro neto en pesos (ej. -$850 de ahorro).

        Fecha de vigencia.

        Botón de Acción Directa "Agregar a mi Lista": Al pulsarlo, el producto con precio de oferta se incorpora automáticamente a la lista de compras activa del grupo con confirmación háptica.

Pantalla 4: Historial de Movimientos

    Encabezado y Botón "Registrar": Permite añadir gastos o ingresos.

    Tarjeta Bento de Balance del Período: Total de ingresos, total de gastos y saldo neto del intervalo filtrado.

    Barra de Filtros Múltiples:

        Tipo de movimiento: Todos, Solo Gastos, Solo Ingresos.

        Rango temporal: Este Mes, Últimos 30 días, Este Año, Todo el Historial.

        Filtro por categoría (Alimentos, Frescos, Limpieza, Salud, Carnicería, Servicios, Aporte, etc.).

    Lista Cronológica de Transacciones:

        Tarjetas translúcidas con icono direccional (ArrowDownLeft para ingresos en verde o ArrowUpRight para gastos en rosa).

        Nombre, descripción, categoría, fecha, usuario que registró el movimiento y comprobante adjunto si existe.

    Botón Flotante de Acción (FAB):

        Botón circular inferior derecho (+) para registrar ingresos o egresos rápidamente con adjunto de ticket simulado.

Pantalla 5: Perfil y Gestión de Grupo

    Tarjeta Bento de Perfil:

        Avatar con iniciales, nombre, correo, domicilio y localidad.

        Botón "Editar" que abre el modal para modificar datos personales.

        Simulador de Roles y Usuarios: Carrusel horizontal para cambiar de usuario con un toque (Santiago, Lucía, Matías, Valentina) y probar la experiencia multiusuario.

    Gestión de Integrantes del Grupo:

        Lista de miembros con sus roles asignados (Administrador o Miembro). Si el usuario es administrador, puede cambiar los roles de los demás mediante un selector.

        Botón "Invitar": Abre el modal de invitaciones del grupo.

        Sección de Invitaciones Pendientes con botones de Aceptar o Rechazar.

    Ajustes del Sistema:

        Acceso al Centro de Notificaciones.

        Actualización de zona geográfica para ofertas.

        Botón para Restaurar Datos Iniciales (reinicia la base de datos a los valores de prueba).

        Botón de Cierre de Sesión.

5. Modales y Flujos de Carga

   InviteGroupModal (Invitación al Grupo):

        Pestaña 1 (Invitar Usuario): Selector de integrantes del sistema, elección de rol (Miembro o Administrador) y botón de envío directo.

        Pestaña 2 (Enlace y Código): Enlace web único (https://canasta.app/join/...) y código de grupo con botón de un clic para copiar al portapapeles.

   AddProductModal: Formulario de carga con nombre, descripción, prioridad (Esencial, Prioritario, Secundario), stepper de cantidad, precio unitario estimado con subtotal en vivo y generador aleatorio de código de producto.

   AddMovementModal: Selector entre Gasto e Ingreso, campo de monto grande en tipografía monoespaciada, concepto, categoría, fecha y opción de adjuntar foto de comprobante.

   CreateListModal: Carga de título y descripción para nuevas listas asociadas al grupo.

   AddOfferModal: Vinculación de descuentos por comercio, localidad, porcentaje de descuento y cálculo automático de precio promocional.

   GroupSelectorModal: Hoja inferior deslizable para conmutar entre los grupos grupales del usuario o crear uno nuevo con presupuesto asignado.

   NotificationsModal: Centro de notificaciones con filtros por listas, movimientos, ofertas e invitaciones, con opción de marcar todas como leídas.

6. Persistencia y Arquitectura de Datos

   Cumplimiento Estricto del Esquema SQLite: Las interfaces y modelos de TypeScript (Usuario, Grupo, UsuarioGrupo, Lista, ProductoLista, Movimiento, Oferta, InvitacionGrupo, Notificacion) replican exactamente las tablas, claves foráneas y restricciones del esquema SQL.

   Reactividad Total en Contexto (AppContext): Cada creación de lista, movimiento o producto actualiza en tiempo real los cálculos de presupuesto disponible, costos estimados por prioridad y notificaciones del grupo, persistiendo los cambios en localStorage para que la información se conserve al recargar.

DB

PRAGMA foreign_keys = ON;

BEGIN TRANSACTION;

-- =========================================================
-- CANASTA+
-- Base de datos SQLite
-- =========================================================


-- =========================================================
-- 1. USUARIO
-- =========================================================

CREATE TABLE usuario (
id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,

    nombre TEXT NOT NULL,

    email TEXT NOT NULL UNIQUE,

    contrasena TEXT NOT NULL,

    localidad TEXT,
    provincia TEXT,
    pais TEXT,

    domicilio TEXT,

    fecha_creacion TEXT NOT NULL DEFAULT (datetime('now'))
);


-- =========================================================
-- 2. GRUPO
-- =========================================================

CREATE TABLE grupo (
id_grupo INTEGER PRIMARY KEY AUTOINCREMENT,

    nombre TEXT NOT NULL,

    descripcion TEXT,

    categoria TEXT,

    tipo TEXT NOT NULL DEFAULT 'personal'
        CHECK (tipo IN ('personal', 'grupal')),

    fecha_creacion TEXT NOT NULL DEFAULT (datetime('now'))
);


-- =========================================================
-- 3. USUARIO_GRUPO
-- Relación N:N entre usuarios y grupos
-- =========================================================

CREATE TABLE usuario_grupo (
id_usuario_grupo INTEGER PRIMARY KEY AUTOINCREMENT,

    id_usuario INTEGER NOT NULL,

    id_grupo INTEGER NOT NULL,

    rol TEXT NOT NULL DEFAULT 'miembro'
        CHECK (rol IN ('administrador', 'miembro')),

    fecha_ingreso TEXT NOT NULL DEFAULT (datetime('now')),

    FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE,

    FOREIGN KEY (id_grupo)
        REFERENCES grupo(id_grupo)
        ON DELETE CASCADE,

    UNIQUE (id_usuario, id_grupo)
);


-- =========================================================
-- 4. LISTA
-- =========================================================

CREATE TABLE lista (
id_lista INTEGER PRIMARY KEY AUTOINCREMENT,

    id_grupo INTEGER NOT NULL,

    id_usuario_creador INTEGER NOT NULL,

    id_usuario_administrador INTEGER,

    nombre TEXT NOT NULL,

    descripcion TEXT,

    estado TEXT NOT NULL DEFAULT 'activa'
        CHECK (estado IN (
            'activa',
            'completada',
            'archivada'
        )),

    fecha_creacion TEXT NOT NULL DEFAULT (datetime('now')),

    fecha_actualizacion TEXT NOT NULL DEFAULT (datetime('now')),

    FOREIGN KEY (id_grupo)
        REFERENCES grupo(id_grupo)
        ON DELETE CASCADE,

    FOREIGN KEY (id_usuario_creador)
        REFERENCES usuario(id_usuario)
        ON DELETE RESTRICT,

    FOREIGN KEY (id_usuario_administrador)
        REFERENCES usuario(id_usuario)
        ON DELETE SET NULL
);


-- =========================================================
-- 5. PRODUCTO_LISTA
-- =========================================================

CREATE TABLE producto_lista (
id_producto_lista INTEGER PRIMARY KEY AUTOINCREMENT,

    id_lista INTEGER NOT NULL,

    id_usuario_creador INTEGER NOT NULL,

    nombre TEXT NOT NULL,

    descripcion TEXT,

    prioridad TEXT NOT NULL DEFAULT 'secundario'
        CHECK (prioridad IN (
            'prioritario',
            'esencial',
            'secundario'
        )),

    cantidad REAL NOT NULL DEFAULT 1
        CHECK (cantidad > 0),

    precio_estimado_unitario INTEGER
        CHECK (precio_estimado_unitario >= 0),

    codigo_producto TEXT,

    estado TEXT NOT NULL DEFAULT 'pendiente'
        CHECK (estado IN (
            'pendiente',
            'comprado',
            'omitido'
        )),

    fecha_creacion TEXT NOT NULL DEFAULT (datetime('now')),

    FOREIGN KEY (id_lista)
        REFERENCES lista(id_lista)
        ON DELETE CASCADE,

    FOREIGN KEY (id_usuario_creador)
        REFERENCES usuario(id_usuario)
        ON DELETE RESTRICT
);


-- =========================================================
-- 6. MOVIMIENTO
-- Ingresos y egresos
-- =========================================================

CREATE TABLE movimiento (
id_movimiento INTEGER PRIMARY KEY AUTOINCREMENT,

    id_usuario_creador INTEGER NOT NULL,

    id_grupo INTEGER NOT NULL,

    nombre TEXT NOT NULL,

    monto INTEGER NOT NULL
        CHECK (monto >= 0),

    descripcion TEXT,

    tipo TEXT NOT NULL
        CHECK (tipo IN (
            'ingreso',
            'gasto'
        )),

    foto_url TEXT,

    fecha_movimiento TEXT NOT NULL,

    fecha_creacion TEXT NOT NULL DEFAULT (datetime('now')),

    FOREIGN KEY (id_usuario_creador)
        REFERENCES usuario(id_usuario)
        ON DELETE RESTRICT,

    FOREIGN KEY (id_grupo)
        REFERENCES grupo(id_grupo)
        ON DELETE CASCADE
);


-- =========================================================
-- 7. OFERTA
-- =========================================================

CREATE TABLE oferta (
id_oferta INTEGER PRIMARY KEY AUTOINCREMENT,

    id_producto_lista INTEGER NOT NULL,

    nombre_comercio TEXT NOT NULL,

    ubicacion TEXT,

    localidad TEXT,

    provincia TEXT,

    latitud REAL,

    longitud REAL,

    fecha_inicio TEXT NOT NULL,

    fecha_fin TEXT NOT NULL,

    porcentaje_descuento REAL
        CHECK (
            porcentaje_descuento IS NULL
            OR (
                porcentaje_descuento >= 0
                AND porcentaje_descuento <= 100
            )
        ),

    precio_original INTEGER
        CHECK (precio_original IS NULL OR precio_original >= 0),

    precio_oferta INTEGER
        CHECK (precio_oferta IS NULL OR precio_oferta >= 0),

    descripcion TEXT,

    fecha_creacion TEXT NOT NULL DEFAULT (datetime('now')),

    FOREIGN KEY (id_producto_lista)
        REFERENCES producto_lista(id_producto_lista)
        ON DELETE CASCADE,

    CHECK (fecha_fin > fecha_inicio)
);


-- =========================================================
-- 8. INVITACION_GRUPO
-- =========================================================

CREATE TABLE invitacion_grupo (
id_invitacion INTEGER PRIMARY KEY AUTOINCREMENT,

    id_usuario_origen INTEGER NOT NULL,

    id_usuario_destino INTEGER NOT NULL,

    id_grupo INTEGER NOT NULL,

    estado TEXT NOT NULL DEFAULT 'pendiente'
        CHECK (estado IN (
            'pendiente',
            'aceptada',
            'rechazada'
        )),

    fecha_creacion TEXT NOT NULL DEFAULT (datetime('now')),

    fecha_respuesta TEXT,

    FOREIGN KEY (id_usuario_origen)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE,

    FOREIGN KEY (id_usuario_destino)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE,

    FOREIGN KEY (id_grupo)
        REFERENCES grupo(id_grupo)
        ON DELETE CASCADE
);


-- =========================================================
-- 9. NOTIFICACION
-- =========================================================

CREATE TABLE notificacion (
id_notificacion INTEGER PRIMARY KEY AUTOINCREMENT,

    id_grupo INTEGER NOT NULL,

    tipo TEXT NOT NULL,

    titulo TEXT NOT NULL,

    descripcion TEXT,

    fecha_creacion TEXT NOT NULL DEFAULT (datetime('now')),

    FOREIGN KEY (id_grupo)
        REFERENCES grupo(id_grupo)
        ON DELETE CASCADE
);


-- =========================================================
-- ÍNDICES
-- =========================================================

CREATE INDEX idx_usuario_email
ON usuario(email);


CREATE INDEX idx_usuario_grupo_usuario
ON usuario_grupo(id_usuario);


CREATE INDEX idx_usuario_grupo_grupo
ON usuario_grupo(id_grupo);


CREATE INDEX idx_lista_grupo
ON lista(id_grupo);


CREATE INDEX idx_lista_creador
ON lista(id_usuario_creador);


CREATE INDEX idx_lista_administrador
ON lista(id_usuario_administrador);


CREATE INDEX idx_producto_lista_lista
ON producto_lista(id_lista);


CREATE INDEX idx_producto_lista_creador
ON producto_lista(id_usuario_creador);


CREATE INDEX idx_producto_lista_codigo
ON producto_lista(codigo_producto);


CREATE INDEX idx_movimiento_grupo
ON movimiento(id_grupo);


CREATE INDEX idx_movimiento_usuario
ON movimiento(id_usuario_creador);


CREATE INDEX idx_movimiento_fecha
ON movimiento(fecha_movimiento);


CREATE INDEX idx_oferta_producto
ON oferta(id_producto_lista);


CREATE INDEX idx_oferta_fecha
ON oferta(fecha_inicio, fecha_fin);


CREATE INDEX idx_oferta_ubicacion
ON oferta(localidad, provincia);


CREATE INDEX idx_invitacion_destino
ON invitacion_grupo(id_usuario_destino);


CREATE INDEX idx_invitacion_grupo
ON invitacion_grupo(id_grupo);


CREATE INDEX idx_invitacion_estado
ON invitacion_grupo(estado);


CREATE INDEX idx_notificacion_grupo
ON notificacion(id_grupo);


CREATE INDEX idx_notificacion_fecha
ON notificacion(fecha_creacion);


COMMIT;

Para Canasta+ vamos a definir una arquitectura Android con Kotlin, Jetpack Compose y MVVM que utilice Room para la base de datos local, DataStore para las preferencias y almacenamiento de archivos locales cuando sea necesario. SharedPreferences quedará como tecnología heredada, sin utilizarla para funcionalidades nuevas.
1. Tecnologías de almacenamiento









1. Room — Base de datos local
   Principal
   Guardará los datos estructurados de Canasta+ en el dispositivo.
- Grupos y miembros.
- Listas de compras y productos.
- Ingresos y gastos.
- Ofertas guardadas.
- Datos en caché para consultar sin conexión.
  Tecnología: Room sobre SQLite, con Entity, DAO y Database.












2. DataStore — Preferencias
   Recomendado
   Almacenará configuraciones pequeñas de la aplicación.
- Tema claro, oscuro o del sistema.
- Preferencias de notificaciones.
- Filtros predeterminados.
- Configuraciones de visualización.
  Tecnología: Preferences DataStore; Proto DataStore si necesitamos preferencias tipadas con un esquema.












3. Archivos locales — Imágenes y documentos
   Servirá para almacenar archivos que no conviene guardar directamente en Room.
- Comprobantes de gastos.
- Fotografías adjuntas a movimientos.
- Imágenes descargadas para uso temporal.
- Archivos exportados, como informes.
  Almacenamiento interno privado para datos de la aplicación; almacenamiento compartido cuando el usuario necesite exportar o compartir archivos.

  

2. Arquitectura de datos
   La arquitectura MVVM tendrá esta organización:
   Screen (Jetpack Compose)
   Interfaz de usuario

ViewModel
Estado de pantalla y acciones

Repository
Coordina el acceso a datos

Room
Datos estructurados


DataStore
Preferencias


File Storage
Imágenes y documentos




Más adelante, si Canasta+ se conecta con un backend, los repositorios también podrán consultar la API REST y sincronizar los datos locales. Room seguirá funcionando como almacenamiento local, pero habrá que definir cómo resolver conflictos y qué datos tienen prioridad.
3. Estructura de carpetas propuesta
   Mantendremos la organización por funcionalidades y separaremos la infraestructura local dentro de data/.
   com.canastaplus/
   ├── app/
   │   ├── MainActivity.kt
   │   └── navigation/
   │       ├── AppNavigation.kt
   │       └── NavRoutes.kt
   │
   ├── core/
   │   ├── database/
   │   │   ├── CanastaDatabase.kt
   │   │   ├── dao/
   │   │   └── entity/
   │   ├── datastore/
   │   │   └── UserPreferencesDataStore.kt
   │   ├── storage/
   │   │   └── LocalFileStorage.kt
   │   ├── network/
   │   └── ui/
   │       └── theme/
   │
   ├── data/
   │   ├── repository/
   │   └── mapper/
   │
   ├── feature/
   │   ├── auth/
   │   ├── home/
   │   ├── finances/
   │   ├── groups/
   │   ├── shopping/
   │   ├── offers/
   │   ├── notifications/
   │   └── profile/
   │
   └── di/
   └── AppModule.kt


Dentro de cada funcionalidad seguiremos usando Screen, ViewModel, UiState y components/. Los DAO y las entidades de Room pueden organizarse por dominio o dentro de cada módulo; para empezar, recomiendo agruparlos por dominio cuando el proyecto crezca.
4. Decisiones importantes para Canasta+
- Room: guardará los datos que necesitamos consultar, filtrar, relacionar y actualizar.
- DataStore: guardará preferencias; no será la base de datos de usuarios, grupos o movimientos.
- Archivos: Room almacenará la ruta o URI de una imagen, no los bytes de la imagen en una columna normal.
- Contraseñas y tokens: no guardaremos contraseñas en texto plano. Los tokens de autenticación requieren un tratamiento específico y no deben mezclarse con las preferencias comunes.
- Datos financieros: los importes se manejarán con precisión, usando enteros en centavos para los valores monetarios.
- Sin conexión: podremos consultar y modificar datos locales, pero la sincronización entre usuarios de un grupo requerirá una API y reglas de sincronización.
  Siguiente paso recomendado: definir las entidades de Room (UsuarioEntity, GrupoEntity, ListaEntity, ProductoListaEntity, MovimientoEntity, etc.), sus relaciones, los DAO y las migraciones iniciales antes de implementar las pantallas. Así, la arquitectura visual y la persistencia tendrán una base consistente desde el principio.


Stack de alto rendimiento
Tecnologías y prácticas que adoptaría desde la primera versión.
01

Kotlin Coroutines + Flow
Operaciones asíncronas y datos reactivos.


02

Room + índices bien diseñados
Consultas locales rápidas y persistencia fiable.


03

Jetpack Compose optimizado
Interfaz fluida y recomposiciones controladas.


04

Coil
Carga y caché de imágenes.


05

Baseline Profiles
Mejor inicio y ejecución de recorridos frecuentes.


06

Macrobenchmark
Medición objetiva antes y después de optimizar.

4. Cómo implementar la sincronización
   Usaría estas tecnologías:
- Room + Flow: lectura reactiva de los datos locales.
- Retrofit + OkHttp: comunicación con la API REST.
- Coroutines: ejecución asíncrona.
- DataStore: preferencias y configuración de sincronización, si hace falta.
- WorkManager: reintentos y sincronizaciones diferidas que deban persistir incluso si la aplicación se cierra.
- Hilt: inyección de repositorios, DAO y clientes de red.
  No usaría WorkManager para cada actualización visible de pantalla. Para una actualización inmediata al abrir el inicio, el repositorio puede lanzar una corrutina vinculada al ciclo de vida adecuado; WorkManager se reserva para trabajos diferidos y persistentes.

Para Canasta+ te recomiendo implementar una estrategia Offline-First con actualización en segundo plano. La aplicación mostrará inmediatamente los datos guardados en Room y, al mismo tiempo, consultará la API de forma asíncrona para obtener los datos más recientes.
Esto permite que la aplicación se sienta rápida incluso con una conexión lenta, y que siga siendo útil cuando no haya Internet.
1. Cómo funcionará Canasta+

1. El usuario inicia sesión
   Se valida la sesión y se identifica al usuario y sus grupos.




2. Se muestran los datos locales inmediatamente
   Room entrega los grupos, movimientos, listas y productos previamente guardados. La pantalla no necesita esperar a la API.




3. Se consulta la API en paralelo
   Las peticiones se ejecutan en segundo plano. La interfaz muestra un indicador discreto de actualización.




4. Room recibe los datos nuevos
   El repositorio guarda los cambios en una transacción. Como la pantalla observa Room mediante Flow, los datos se actualizan automáticamente.



Hay una distinción importante: el inicio de sesión debe validarse con el servidor cuando corresponda. Mostrar datos locales permite entrar rápidamente a la experiencia de la aplicación, pero no debe interpretarse como una autenticación nueva y válida si no hay una sesión autorizada.
2. Feedback visual de actualización
   No mostraría una pantalla de carga completa cada vez que se actualizan los datos. Si ya tenemos información local, debemos conservarla visible.
   diseñemos Canasta+ con este patrón desde el comienzo: Room como fuente observable, API como fuente de actualización, sincronización explícita y feedback de estado separado de los datos. Es una buena base para ofrecer rapidez sin sacrificar consistencia ni funcionamiento sin conexión.