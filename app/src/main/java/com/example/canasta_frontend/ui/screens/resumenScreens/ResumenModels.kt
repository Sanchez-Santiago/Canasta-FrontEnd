package com.example.canasta_frontend.ui.screens.resumenScreens

import androidx.compose.ui.graphics.Color

data class ChartSegment(
    val id: String,
    val name: String,
    val amount: Int,
    val percent: Float,
    val color: Color
)

data class CenterDisplay(
    val title: String,
    val amount: Int,
    val percent: Int,
    val color: Color
)

data class ActivityModel(
    val title: String,
    val subtitle: String,
    val amount: String,
    val isIngreso: Boolean
)
