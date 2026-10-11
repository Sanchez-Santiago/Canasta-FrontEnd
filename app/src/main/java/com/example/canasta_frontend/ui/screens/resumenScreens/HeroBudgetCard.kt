package com.example.canasta_frontend.ui.screens.resumenScreens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canasta_frontend.ui.theme.*
import java.util.Locale

@Composable
fun HeroBudgetCard(
    groupName: String,
    presupuestoTotal: Int,
    totalIngresos: Int,
    totalGastos: Int,
    esenciales: Int,
    primarios: Int,
    secundarios: Int
) {
    var chartMode by remember { mutableStateOf("completo") }
    var selectedSlice by remember { mutableStateOf("todos") }

    val totalEstimado = esenciales + primarios + secundarios
    val saldoNeto = totalIngresos - totalGastos
    val disponible = maxOf(0, saldoNeto)

    val cs = MaterialTheme.colorScheme
    val cPrimary = cs.primary
    val cSecondary = cs.secondary
    val cTertiary = cs.tertiary
    val cError = cs.error
    val cSurfaceVariant = cs.surfaceVariant

    val segmentsData = remember(chartMode, presupuestoTotal, totalIngresos, totalGastos, esenciales, primarios, secundarios, cPrimary, cSecondary, cTertiary, cError, cSurfaceVariant) {
        val baseTotal = maxOf(
            totalIngresos.toFloat(),
            presupuestoTotal.toFloat(),
            (totalGastos + totalEstimado).toFloat()
        ).coerceAtLeast(1f)

        if (chartMode == "solo_prioridades") {
            val sum = maxOf(1, totalEstimado).toFloat()
            listOf(
                ChartSegment("esencial", "Esenciales", esenciales, (esenciales / sum) * 100f, cPrimary),
                ChartSegment("primario", "Primarios", primarios, (primarios / sum) * 100f, cSecondary),
                ChartSegment("secundario", "Secundarios", secundarios, (secundarios / sum) * 100f, cTertiary)
            )
        } else {
            val pGastado = (totalGastos / baseTotal) * 100f
            val pEsencial = (esenciales / baseTotal) * 100f
            val pPrimario = (primarios / baseTotal) * 100f
            val pSecundario = (secundarios / baseTotal) * 100f
            val pDisponible = maxOf(0f, 100f - (pGastado + pEsencial + pPrimario + pSecundario))

            listOf(
                ChartSegment("gastado", "Gasto Real", totalGastos, pGastado, cError),
                ChartSegment("esencial", "Esenciales", esenciales, pEsencial, cPrimary),
                ChartSegment("primario", "Primarios", primarios, pPrimario, cSecondary),
                ChartSegment("secundario", "Secundarios", secundarios, pSecundario, cTertiary),
                ChartSegment("disponible", "Disponible", maxOf(0, (baseTotal - (totalGastos + totalEstimado)).toInt()), pDisponible, cSurfaceVariant)
            )
        }
    }

    val centerDisplay = remember(selectedSlice, segmentsData, chartMode, totalIngresos, totalGastos, saldoNeto, totalEstimado, cPrimary, cError) {
        if (selectedSlice != "todos") {
            val match = segmentsData.find { it.id == selectedSlice }
            if (match != null) {
                return@remember CenterDisplay(match.name, match.amount, match.percent.toInt(), match.color)
            }
        }
        if (chartMode == "solo_prioridades") {
            CenterDisplay("Estimados", totalEstimado, 100, cPrimary)
        } else {
            val pct = if (totalIngresos > 0) minOf(100, ((disponible.toFloat() / totalIngresos) * 100).toInt()) else 0
            val isPositive = saldoNeto >= 0
            CenterDisplay(
                title = if (isPositive) "Saldo Neto" else "Déficit",
                amount = saldoNeto,
                percent = pct,
                color = if (isPositive) cPrimary else cError
            )
        }
    }

    val isLight = cs.background.red + cs.background.green + cs.background.blue > 1.5f
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(if (isLight) LightDepthGradient else EmeraldDepthGradient)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(22.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "RESUMEN FINANCIERO",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = groupName,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (chartMode == "completo") MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable {
                                chartMode = "completo"
                                selectedSlice = "todos"
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "General",
                            color = if (chartMode == "completo") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (chartMode == "solo_prioridades") MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable {
                                chartMode = "solo_prioridades"
                                selectedSlice = "todos"
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Estimados",
                            color = if (chartMode == "solo_prioridades") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Donut Chart + KPI Summary
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        val center = Offset(canvasWidth / 2f, canvasHeight / 2f)
                        val strokeWidth = 14f
                        val r = (canvasWidth / 2f) - strokeWidth

                        var startAngle = -90f
                        segmentsData.forEach { seg ->
                            val sweepAngle = (seg.percent / 100f) * 360f
                            val isHighlight = selectedSlice == "todos" || selectedSlice == seg.id
                            drawArc(
                                color = seg.color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                topLeft = Offset(center.x - r, center.y - r),
                                size = Size(r * 2f, r * 2f),
                                style = Stroke(
                                    width = if (isHighlight) strokeWidth else strokeWidth * 0.6f,
                                    cap = StrokeCap.Round
                                ),
                                alpha = if (isHighlight) 1f else 0.3f
                            )
                            startAngle += sweepAngle
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = centerDisplay.title,
                            color = centerDisplay.color,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "$${centerDisplay.amount.toLocaleString()}",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${centerDisplay.percent}%",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 9.sp
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(text = "Saldo Neto", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                        Text(
                            text = "$${saldoNeto.toLocaleString()}",
                            color = if (saldoNeto >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(text = "Estimado Compras", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                        Text(
                            text = "$${totalEstimado.toLocaleString()}",
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Interactive Category Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                segmentsData.take(4).forEach { seg ->
                    val isSelected = selectedSlice == seg.id
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                            .clickable { selectedSlice = if (selectedSlice == seg.id) "todos" else seg.id }
                            .padding(6.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(6.dp).background(seg.color, RoundedCornerShape(3.dp)))
                                Text(text = seg.name, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 8.sp, maxLines = 1)
                            }
                            Text(text = "$${seg.amount.toLocaleString()}", color = MaterialTheme.colorScheme.onBackground, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080A09)
@Composable
fun HeroBudgetCardWithDonutPreview() {
    CanastaTheme(darkTheme = true) {
        Box(modifier = Modifier.padding(16.dp)) {
            HeroBudgetCard(
                groupName = "Canasta Personal",
                presupuestoTotal = 0,
                totalIngresos = 850000,
                totalGastos = 45000,
                esenciales = 12000,
                primarios = 8000,
                secundarios = 5000
            )
        }
    }
}

private fun Int.toLocaleString(): String {
    return String.format(Locale.getDefault(), "%,d", this).replace(',', '.')
}
