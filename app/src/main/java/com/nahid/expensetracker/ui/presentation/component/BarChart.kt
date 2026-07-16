package com.nahid.expensetracker.ui.presentation.component

import android.graphics.Paint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
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
    maxLimit: Float = 50000f,
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
    val yAxisWidthPx = with(density) { 45.dp.toPx() }
    val labelColor = MaterialTheme.colorScheme.primary.toArgb()

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
            val effectiveYAxisSpacingPx = if (showYAxis) yAxisWidthPx else 0f
            
            val availableWidth = constraints.maxWidth.toFloat() - effectiveYAxisSpacingPx
            val barWidth = (availableWidth - (barSpacing * (barCount + 1))) / barCount.coerceAtLeast(1)

            Canvas(modifier = Modifier.fillMaxSize()) {
                val chartHeight = size.height - barBottomPaddingPx
                
                // ---------- Y AXIS & GRID ----------
                if (showYAxis) {
                    val  stepCount = 5
                    val valueStep = maxLimit / stepCount
                    val yValues = (0..stepCount).map { it * valueStep }
                    
                    yValues.forEach { valItem ->
                        val y = chartHeight - (valItem * (chartHeight / maxLimit))
                        
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

                // ---------- BARS ----------
                data.forEachIndexed { index, item ->
                    val scaleFactor = chartHeight / maxLimit
                    val barHeight = (item.value.coerceAtMost(maxLimit) * scaleFactor) * animationProgress.value

                    val left = effectiveYAxisSpacingPx + barSpacing + index * (barWidth + barSpacing)
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
                    start = Offset(effectiveYAxisSpacingPx, chartHeight),
                    end = Offset(size.width, chartHeight),
                    strokeWidth = 2f
                )
            }
        }

        // ---------- X AXIS LABELS ----------
        if (showLabel) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 45.dp), // Match effectiveYAxisSpacingPx
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                data.forEach { item ->
                    Text(
                        text = item.label,
                        modifier = Modifier.weight(1f),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
