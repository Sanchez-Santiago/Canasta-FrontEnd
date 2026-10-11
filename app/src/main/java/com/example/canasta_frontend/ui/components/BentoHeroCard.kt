package com.example.canasta_frontend.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canasta_frontend.ui.theme.*
import java.util.Locale

@Composable
fun BentoHeroCard(
    groupName: String,
    totalIngresos: Int,
    totalGastos: Int,
    estimadoGastar: Int = 0,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    // Solo luminancia del background: respeta tema forzado claro/oscuro
    val dark = cs.background.red + cs.background.green + cs.background.blue < 1.5f

    val balance = totalIngresos - totalGastos
    val disponible = maxOf(0, balance)
    val gastado = maxOf(0, totalGastos)
    val estimado = maxOf(0, estimadoGastar)
    val total = (disponible + gastado + estimado).coerceAtLeast(1)

    val pDisponible = disponible.toFloat() / total
    val pGastado = gastado.toFloat() / total
    val pEstimado = estimado.toFloat() / total

    val cDisponible = cs.primary
    val cGastado = cs.error
    val cEstimado = cs.secondary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(if (dark) EmeraldDepthGradient else LightDepthGradient)
            .border(1.dp, cs.outline, RoundedCornerShape(22.dp))
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CANASTA+",
                        color = cs.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = groupName,
                        color = cs.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(cs.primary.copy(alpha = 0.15f))
                        .border(1.dp, cs.primary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Activo",
                        color = cs.primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Balance disponible",
                        color = cs.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$${balance.toLocaleString()}",
                        color = if (balance >= 0) cs.onBackground else cs.error,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (balance >= 0) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = null,
                            tint = if (balance >= 0) cs.primary else cs.error,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (balance >= 0) "Saldo a favor" else "Déficit — revisá tus gastos",
                            color = if (balance >= 0) cs.primary else cs.error,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Donut: disponible / gastado / estimado
                Box(
                    modifier = Modifier.size(112.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val stroke = 13.dp.toPx()
                        val r = (size.width / 2f) - stroke
                        val center = Offset(size.width / 2f, size.height / 2f)
                        var start = -90f
                        val segs = listOf(
                            Triple(pDisponible, cDisponible, 1f),
                            Triple(pGastado, cGastado, 1f),
                            Triple(pEstimado, cEstimado, 1f)
                        )
                        // track
                        drawArc(
                            color = cs.onSurfaceVariant.copy(alpha = 0.20f),
                            startAngle = 0f, sweepAngle = 360f, useCenter = false,
                            topLeft = Offset(center.x - r, center.y - r),
                            size = Size(r * 2f, r * 2f),
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                        segs.forEach { (frac, col, _) ->
                            if (frac <= 0f) return@forEach
                            val sweep = frac * 360f
                            drawArc(
                                color = col,
                                startAngle = start, sweepAngle = (sweep - 3f).coerceAtLeast(2f),
                                useCenter = false,
                                topLeft = Offset(center.x - r, center.y - r),
                                size = Size(r * 2f, r * 2f),
                                style = Stroke(width = stroke, cap = StrokeCap.Round)
                            )
                            start += sweep
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${((pDisponible) * 100).toInt()}%",
                            color = cs.onBackground,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(text = "disponible", color = cs.onSurfaceVariant, fontSize = 9.sp)
                    }
                }
            }

            // Leyenda 3 tipos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DonutLegend(
                    modifier = Modifier.weight(1f),
                    dot = cDisponible, label = "Disponible",
                    value = "$${disponible.toLocaleString()}", valueColor = cs.onBackground
                )
                DonutLegend(
                    modifier = Modifier.weight(1f),
                    dot = cGastado, label = "Gastado",
                    value = "$${gastado.toLocaleString()}", valueColor = cs.onBackground
                )
                DonutLegend(
                    modifier = Modifier.weight(1f),
                    dot = cEstimado, label = "Por gastar",
                    value = "$${estimado.toLocaleString()}", valueColor = cs.onBackground
                )
            }

            HorizontalDivider(color = cs.outline.copy(alpha = 0.6f), thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(cs.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = cs.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(text = "Ingresos", color = cs.onSurfaceVariant, fontSize = 11.sp)
                        Text(text = "$${totalIngresos.toLocaleString()}", color = cs.onBackground, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(cs.error.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = null,
                            tint = cs.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(text = "Gastos", color = cs.onSurfaceVariant, fontSize = 11.sp)
                        Text(text = "$${totalGastos.toLocaleString()}", color = cs.onBackground, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DonutLegend(modifier: Modifier, dot: androidx.compose.ui.graphics.Color, label: String, value: String, valueColor: androidx.compose.ui.graphics.Color) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(cs.surface)
            .border(1.dp, cs.outline, RoundedCornerShape(12.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(modifier = Modifier.size(8.dp).background(dot, RoundedCornerShape(4.dp)))
            Text(text = label, color = cs.onSurfaceVariant, fontSize = 9.sp, maxLines = 1)
        }
        Text(text = value, color = valueColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080A09)
@Composable
fun BentoHeroCardPreview() {
    CanastaTheme(darkTheme = true) {
        Box(modifier = Modifier.padding(16.dp)) {
            BentoHeroCard(
                groupName = "Canasta Personal",
                totalIngresos = 850000,
                totalGastos = 45000,
                estimadoGastar = 25000
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC)
@Composable
fun BentoHeroCardLightPreview() {
    CanastaTheme(darkTheme = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            BentoHeroCard(
                groupName = "Canasta Personal",
                totalIngresos = 850000,
                totalGastos = 45000,
                estimadoGastar = 25000
            )
        }
    }
}

private fun Int.toLocaleString(): String {
    return String.format(Locale.getDefault(), "%,d", this).replace(',', '.')
}
