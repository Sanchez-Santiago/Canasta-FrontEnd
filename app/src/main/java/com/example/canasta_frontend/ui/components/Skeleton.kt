package com.example.canasta_frontend.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
private fun shimmerModifier(corner: Dp = 12.dp): Modifier {
    val cs = MaterialTheme.colorScheme
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.35f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "shimmerAlpha"
    )
    return Modifier
        .clip(RoundedCornerShape(corner))
        .background(cs.surfaceVariant.copy(alpha = alpha))
        .semantics { contentDescription = "Cargando" }
}

@Composable
fun SkeletonBox(modifier: Modifier = Modifier, corner: Dp = 12.dp) {
    Box(modifier = modifier.then(shimmerModifier(corner)))
}

@Composable
fun CardListSkeleton(count: Int = 3, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(count) {
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SkeletonBox(Modifier.fillMaxWidth().height(18.dp), 8.dp)
                SkeletonBox(Modifier.fillMaxWidth(0.6f).height(12.dp), 8.dp)
                SkeletonBox(Modifier.fillMaxWidth().height(6.dp), 3.dp)
            }
        }
    }
}

@Composable
fun RowListSkeleton(count: Int = 4, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(count) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SkeletonBox(Modifier.size(44.dp), 12.dp)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SkeletonBox(Modifier.fillMaxWidth(0.7f).height(14.dp), 6.dp)
                    SkeletonBox(Modifier.fillMaxWidth(0.45f).height(11.dp), 6.dp)
                }
                SkeletonBox(Modifier.width(64.dp).height(18.dp), 6.dp)
            }
        }
    }
}

@Composable
fun HeroSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.surface).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SkeletonBox(Modifier.width(120.dp).height(12.dp), 6.dp)
        SkeletonBox(Modifier.fillMaxWidth(0.5f).height(30.dp), 8.dp)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SkeletonBox(Modifier.size(112.dp), 56.dp)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                SkeletonBox(Modifier.fillMaxWidth().height(40.dp), 10.dp)
                SkeletonBox(Modifier.fillMaxWidth().height(40.dp), 10.dp)
            }
        }
    }
}

@Composable
fun ProfileSkeleton(modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SkeletonBox(Modifier.size(56.dp), 28.dp)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                SkeletonBox(Modifier.fillMaxWidth(0.6f).height(16.dp), 6.dp)
                SkeletonBox(Modifier.fillMaxWidth(0.4f).height(12.dp), 6.dp)
            }
        }
        CardListSkeleton(2)
    }
}
