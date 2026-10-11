package com.example.canasta_frontend.ui.screens.ofertasScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canasta_frontend.data.network.RetrofitClient
import com.example.canasta_frontend.ui.theme.*

@Composable
fun OfertasScreen() {
    val context = LocalContext.current
    var ofertas by remember { mutableStateOf<List<OfertaModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        try {
            val api = RetrofitClient.createService(context)
            val remoteOfertas = api.getOfertas().data?.ofertas ?: emptyList()
            ofertas = remoteOfertas.map { o ->
                OfertaModel(
                    id = o.id_oferta,
                    productoNombre = o.nombre_comercio,
                    comercio = o.nombre_comercio,
                    localidad = o.localidad ?: "Córdoba Capital",
                    porcentajeDescuento = o.porcentaje_descuento ?: 0.0,
                    precioOriginal = o.precio_original ?: 0,
                    precioOferta = o.precio_oferta ?: 0,
                    prioridad = "esencial",
                    fechaFin = o.fecha_fin
                )
            }
        } catch (_: Exception) {
            ofertas = emptyList()
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OfertasHeader(
            ofertasCount = ofertas.size,
            onAddOfferClick = {
                val nueva = OfertaModel(
                    id = System.currentTimeMillis(),
                    productoNombre = "Nueva Oferta",
                    comercio = "Comercio Local",
                    localidad = "Córdoba Capital",
                    porcentajeDescuento = 20.0,
                    precioOriginal = 3000,
                    precioOferta = 2400,
                    prioridad = "esencial",
                    fechaFin = "2025-04-30"
                )
                ofertas = listOf(nueva) + ofertas
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (ofertas.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
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
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                            .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.LocalOffer, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(28.dp))
                    }

                    Text(
                        text = "No Hay Ofertas Registradas",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Aún no hay descuentos o promociones cargadas en tu zona. ¡Sé el primero en publicar una oferta!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = {
                            val nueva = OfertaModel(
                                id = System.currentTimeMillis(),
                                productoNombre = "Oferta Promocional",
                                comercio = "Supermercado Local",
                                localidad = "Córdoba Capital",
                                porcentajeDescuento = 25.0,
                                precioOriginal = 4000,
                                precioOferta = 3000,
                                prioridad = "esencial",
                                fechaFin = "2025-04-30"
                            )
                            ofertas = listOf(nueva)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
                            Text(text = "Cargar Primera Oferta", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        } else {
            ofertas.forEach { oferta ->
                OfertasCardItem(
                    oferta = oferta,
                    onAddToListClick = {
                        // Action
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
