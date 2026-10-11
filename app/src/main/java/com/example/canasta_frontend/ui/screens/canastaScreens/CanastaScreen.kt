package com.example.canasta_frontend.ui.screens.canastaScreens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canasta_frontend.core.sync.SyncEngine
import com.example.canasta_frontend.core.sync.SyncUiState
import com.example.canasta_frontend.data.database.entity.ListaEntity
import com.example.canasta_frontend.data.database.entity.ProductoListaEntity
import com.example.canasta_frontend.data.network.RetrofitClient
import com.example.canasta_frontend.data.repository.ShoppingRepository
import com.example.canasta_frontend.ui.theme.*
import kotlinx.coroutines.launch

// Cache por grupo: evita mezclar listas personal/grupal al cambiar de tab
private var cachedListasByGroup = mutableMapOf<Long, List<ListaModel>>()

@Composable
fun CanastaScreen(
    activeGroupId: Long?,
    groupName: String = "",
    onDataChanged: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val shoppingRepo = remember { ShoppingRepository(context) }

    var selectedTab by remember { mutableStateOf("todas") }
    var expandedListId by remember { mutableStateOf<Long?>(null) }
    
    var showCreateListDialog by remember { mutableStateOf(false) }
    var showAddProductDialog by remember { mutableStateOf(false) }

    var uiState by remember(activeGroupId) { mutableStateOf<SyncUiState<List<ListaModel>>>(SyncUiState.Loading) }
    val scrollState = rememberScrollState()

    fun currentCache(): MutableList<ListaModel> {
        val gid = activeGroupId ?: -1L
        return cachedListasByGroup.getOrPut(gid) { emptyList() }.toMutableList()
    }

    fun setCache(list: List<ListaModel>) {
        if (activeGroupId != null) cachedListasByGroup[activeGroupId] = list.toList()
    }

    fun loadData() {
        val targetGroup = activeGroupId ?: return
        coroutineScope.launch {
            val api = RetrofitClient.createService(context)

            SyncEngine.sync(
                fetchLocal = {
                    cachedListasByGroup[targetGroup]?.takeIf { it.isNotEmpty() }
                },
                fetchRemote = {
                    val listasResp = api.getListas(targetGroup)
                    val remoteListas = listasResp.data?.listas ?: emptyList()

                    remoteListas.map { l ->
                        val prodsResp = try { api.getProductos(l.id_lista).data?.productos ?: emptyList() } catch (_: Exception) { emptyList() }
                        ListaModel(
                            id = l.id_lista,
                            nombre = l.nombre,
                            descripcion = l.descripcion ?: "",
                            estado = l.estado,
                            creador = "Miembro",
                            productos = prodsResp.map { p ->
                                ProductoModel(
                                    id = p.id_producto_lista,
                                    nombre = p.nombre,
                                    descripcion = p.descripcion,
                                    prioridad = p.prioridad,
                                    cantidad = p.cantidad,
                                    precioUnitario = p.precio_estimado_unitario ?: 0,
                                    codigoProducto = p.codigo_producto,
                                    estado = p.estado
                                )
                            }
                        )
                    }
                },
                saveLocal = { newListas ->
                    setCache(newListas)
                },
                isDataEmpty = { it.isEmpty() }
            ).collect { state ->
                uiState = state
            }
        }
    }

    LaunchedEffect(activeGroupId) {
        expandedListId = null
        if (activeGroupId != null) {
            val cached = cachedListasByGroup[activeGroupId]
            uiState = if (!cached.isNullOrEmpty()) {
                SyncUiState.Content(data = cached, hasLocalData = true)
            } else {
                SyncUiState.Loading
            }
            loadData()
        }
    }

    val currentListas = when (val s = uiState) {
        is SyncUiState.Content -> s.data
        else -> cachedListasByGroup[activeGroupId] ?: emptyList()
    }

    val filteredListas = remember(currentListas, selectedTab) {
        when (selectedTab) {
            "activa" -> currentListas.filter { it.estado == "activa" }
            "completada" -> currentListas.filter { it.estado == "completada" }
            else -> currentListas
        }
    }

    val activeCount = currentListas.count { it.estado == "activa" }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CanastaHeader(
                activeCount = activeCount,
                groupName = groupName,
                onCreateListClick = { showCreateListDialog = true }
            )

            CanastaFilterTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(4.dp))

            when (val state = uiState) {
                is SyncUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is SyncUiState.Empty -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                            }

                            Text(
                                text = "No tienes ninguna canasta creada",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "Crea tu primera lista de compras para organizar los productos esenciales y estimar presupuestos.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Button(
                                onClick = { showCreateListDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
                                    Text(text = "Crear Mi Primera Canasta", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }

                is SyncUiState.Error -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                            .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = state.message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp, textAlign = TextAlign.Center)
                            Button(
                                onClick = { loadData() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Text(text = "Reintentar", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                is SyncUiState.Content -> {
                    if (filteredListas.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                            Text(text = "No hay canastas en esta categoría.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        }
                    } else {
                        filteredListas.forEach { lista ->
                            CanastaCardItem(
                                lista = lista,
                                isExpanded = expandedListId == lista.id,
                                onToggleExpand = {
                                    expandedListId = if (expandedListId == lista.id) null else lista.id
                                },
                                onToggleProduct = { listId, prodId ->
                                    // Toggle product state
                                },
                                onDeleteList = { listId ->
                                    val gid = activeGroupId ?: return@CanastaCardItem
                                    val updated = (cachedListasByGroup[gid] ?: emptyList()).filterNot { it.id == listId }
                                    cachedListasByGroup[gid] = updated
                                    if (expandedListId == listId) expandedListId = null
                                    uiState = if (updated.isEmpty()) SyncUiState.Empty else SyncUiState.Content(data = updated, hasLocalData = true)
                                    onDataChanged() // Refresh Resumen screen metrics!
                                    Toast.makeText(context, "Canasta eliminada", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(64.dp))
        }

        // Floating Action Button (FAB) when a list is expanded
        if (expandedListId != null) {
            FloatingActionButton(
                onClick = { showAddProductDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 80.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir producto", modifier = Modifier.size(24.dp))
            }
        }
    }

    // Dialog to create a list — usa ID real del server para que productos suban bien en grupal
    if (showCreateListDialog && activeGroupId != null) {
        CreateListDialog(
            onDismiss = { showCreateListDialog = false },
            onCreateSuccess = { name, desc ->
                coroutineScope.launch {
                    val targetGroup = activeGroupId
                    val created = shoppingRepo.saveLista(targetGroup, name, desc.ifBlank { null })
                    if (created != null) {
                        val model = ListaModel(created.id_lista, created.nombre, created.descripcion ?: "", created.estado, "Santiago", emptyList())
                        val updated = listOf(model) + (cachedListasByGroup[targetGroup] ?: emptyList())
                        cachedListasByGroup[targetGroup] = updated
                        expandedListId = model.id
                        uiState = SyncUiState.Content(data = updated, hasLocalData = true)
                        onDataChanged()
                        Toast.makeText(context, "Canasta '$name' guardada con éxito", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "No se pudo crear la canasta. Reintentá.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    // Dialog to add a product — payload mínimo al backend, ID server en caché
    if (showAddProductDialog && expandedListId != null && activeGroupId != null) {
        AddProductDialog(
            onDismiss = { showAddProductDialog = false },
            onAddSuccess = { name, desc, priority, qty, price, code ->
                coroutineScope.launch {
                    val currentListId = expandedListId ?: return@launch
                    val created = shoppingRepo.saveProducto(
                        idLista = currentListId,
                        nombre = name,
                        descripcion = desc.ifBlank { null },
                        prioridad = priority,
                        cantidad = qty,
                        precioUnitario = price,
                        codigoProducto = code.ifBlank { null }
                    )
                    if (created != null) {
                        val prodModel = ProductoModel(created.id_producto_lista, created.nombre, created.descripcion, created.prioridad, created.cantidad, created.precio_estimado_unitario ?: 0, created.codigo_producto, created.estado)
                        val gid = activeGroupId
                        val groupList = (cachedListasByGroup[gid] ?: emptyList()).toMutableList()
                        val idx = groupList.indexOfFirst { it.id == currentListId }
                        if (idx != -1) {
                            val updatedList = groupList[idx].copy(productos = groupList[idx].productos + prodModel)
                            groupList[idx] = updatedList
                            cachedListasByGroup[gid] = groupList.toList()
                            uiState = SyncUiState.Content(data = groupList.toList(), hasLocalData = true)
                            onDataChanged()
                        }
                        Toast.makeText(context, "Producto '$name' añadido con éxito", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "No se pudo subir el producto. Verificá conexión.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }
}
