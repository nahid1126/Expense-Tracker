package com.nahid.expensetracker.ui.presentation.component

import android.graphics.Paint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

data class BarChartData(val label: String, val value: Float)

@Composable
fun BarChart(
    data: List<BarChartData>,
    colors: List<Color>,
    isIncome: Boolean = false,
    maxLimit: Float = 0f,
    currencySymbol: String = "৳",
    showLabel: Boolean = true,
    showPercentBarTop: Boolean = false,
    showYAxis: Boolean = true,
    showAnimation: Boolean = true,
    animationDuration: Int = 1200,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val density = LocalDensity.current
    val barBottomPaddingPx = with(density) { 24.dp.toPx() }
    val yAxisWidthPx = with(density) { 50.dp.toPx() }
    val labelColor = MaterialTheme.colorScheme.primary.toArgb()

    val maxValueInData = data.maxOfOrNull { it.value } ?: 0f
    val effectiveMaxLimit = remember(data, maxLimit, isIncome) {
        if (maxLimit > 0f) {
            maxLimit
        } else {
            val base = if (maxValueInData > 0) maxValueInData else 1000f
            val multiplier = when {
                base <= 1000f -> 200f
                base <= 5000f -> 1000f
                base <= 10000f -> 2000f
                base <= 20000f -> 5000f
                base <= 50000f -> 10000f
                else -> 20000f
            }
            (Math.ceil(base / multiplier.toDouble()) * multiplier).toFloat().coerceAtLeast(base)
        }
    }

    var animationPlayed by remember { mutableStateOf(false) }
    val animationProgress = animateFloatAsState(
        targetValue = if (animationPlayed && showAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = animationDuration),
        label = "bar_anim"
    )
    LaunchedEffect(Unit) { animationPlayed = true }

    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val barCount = data.size
            val barSpacing = with(density) { 12.dp.toPx() }
            val minBarWidth = with(density) { 28.dp.toPx() }
            val effectiveYAxisSpacingPx = if (showYAxis) yAxisWidthPx else 0f

            val availableWidth = constraints.maxWidth.toFloat() - effectiveYAxisSpacingPx
            val calculatedBarWidth =
                (availableWidth - (barSpacing * (barCount + 1))) / barCount.coerceAtLeast(1)
            val barWidth = if (calculatedBarWidth < minBarWidth) minBarWidth else calculatedBarWidth
            val totalContentWidth = (barWidth + barSpacing) * barCount + barSpacing

            Box(modifier = Modifier.fillMaxSize()) {
                // ---------- Y AXIS & GRID (Fixed) ----------
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val chartHeight = size.height - barBottomPaddingPx

                    if (showYAxis) {
                        val stepCount = 5
                        val valueStep = effectiveMaxLimit / stepCount
                        val yValues = (0..stepCount).map { it * valueStep }

                        yValues.forEach { valItem ->
                            val y = chartHeight - (valItem * (chartHeight / effectiveMaxLimit))

                            // Grid Line
                            drawLine(
                                color = Color.LightGray.copy(alpha = 0.3f),
                                start = Offset(effectiveYAxisSpacingPx, y),
                                end = Offset(size.width, y),
                                strokeWidth = 1f
                            )

                            // Y Label
                            val formattedLabel = when {
                                valItem == 0f -> "0"
                                valItem < 1000f -> valItem.toInt().toString()
                                valItem >= 1000f -> "${(valItem / 1000).toInt()}k"
                                else -> valItem.toInt().toString()
                            }

                            drawContext.canvas.nativeCanvas.drawText(
                                "$currencySymbol $formattedLabel",
                                effectiveYAxisSpacingPx - 10f,
                                y + 8f,
                                Paint().apply {
                                    color = labelColor
                                    textSize = 24f
                                    textAlign = Paint.Align.RIGHT
                                    isAntiAlias = true
                                }
                            )
                        }
                    }
                }

                // ---------- SCROLLABLE BARS & X AXIS LABELS ----------
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = with(density) { effectiveYAxisSpacingPx.toDp() })
                        .horizontalScroll(rememberScrollState())
                ) {
                    Canvas(
                        modifier = Modifier
                            .weight(1f)
                            .width(with(density) { totalContentWidth.toDp() })
                    ) {
                        val chartHeight = size.height // Height is already constrained by weight(1f)

                        data.forEachIndexed { index, item ->
                            val scaleFactor = chartHeight / effectiveMaxLimit
                            val barHeight = (item.value.coerceAtMost(effectiveMaxLimit) * scaleFactor) * animationProgress.value

                            val left = barSpacing + index * (barWidth + barSpacing)
                            val top = chartHeight - barHeight

                            // Main Bar with rounded top
                            drawRoundRect(
                                color = colors[index % colors.size],
                                topLeft = Offset(left, top),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(8f, 8f)
                            )

                            // Optional percentage/value on top
                            if (showPercentBarTop && barHeight > 20f) {
                                drawContext.canvas.nativeCanvas.drawText(
                                    item.value.roundToInt().toString(),
                                    left + barWidth / 2,
                                    top - 10f,
                                    Paint().apply {
                                        color = labelColor
                                        textSize = 22f
                                        textAlign = Paint.Align.CENTER
                                        isFakeBoldText = true
                                    }
                                )
                            }
                        }

                        // ---------- X AXIS LINE ----------
                        drawLine(
                            color = Color.Gray.copy(alpha = 0.4f),
                            start = Offset(0f, chartHeight),
                            end = Offset(totalContentWidth, chartHeight),
                            strokeWidth = 2f
                        )
                    }

                    // ---------- X AXIS LABELS ----------
                    if (showLabel) {
                        Row(
                            modifier = Modifier
                                .width(with(density) { totalContentWidth.toDp() })
                                .height(with(density) { barBottomPaddingPx.toDp() }),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            data.forEachIndexed { index, item ->
                                Spacer(modifier = Modifier.width(with(density) { barSpacing.toDp() }))
                                Text(
                                    text = item.label,
                                    modifier = Modifier.width(with(density) { barWidth.toDp() }),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (index == data.size - 1) {
                                    Spacer(modifier = Modifier.width(with(density) { barSpacing.toDp() }))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
