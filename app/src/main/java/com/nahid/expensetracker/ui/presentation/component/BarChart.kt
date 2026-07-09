package com.nahid.expensetracker.ui.presentation.component

import android.graphics.Paint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
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
    showLabel: Boolean = true,
    showPercentBarTop: Boolean = true,
    showYAxis: Boolean = false,
    showAnimation: Boolean = true,
    animationDuration: Int = 1200,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val density = LocalDensity.current
    val barBottomPaddingPx = with(density) { 8.dp.toPx() }
    val labelSpacingPx = with(density) { 4.dp.toPx() }
    val yAxisSpacingPx = with(density) { 24.dp.toPx() }

    val totalValue = data.sumOf { it.value.toDouble() }.toFloat()

    var animationPlayed by remember { mutableStateOf(false) }
    val animationProgress = animateFloatAsState(
        targetValue = if (animationPlayed && showAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = animationDuration),
        label = "bar_anim"
    )
    LaunchedEffect(Unit) { animationPlayed = true }

    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {

        // ============================
        //      CHART CANVAS
        // ============================
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val barCount = data.size
            val spacing = with(density) { 8.dp.toPx() }

            val effectiveYAxisSpacingPx = if (showYAxis) yAxisSpacingPx else 0f

            val totalSpacing = spacing * (barCount + 1)
            val barWidth =
                (constraints.maxWidth.toFloat() - effectiveYAxisSpacingPx - totalSpacing) / barCount

            val startOffset = effectiveYAxisSpacingPx + spacing

            Canvas(modifier = Modifier.fillMaxSize()) {

                // ---------- Y AXIS ----------
                if (showYAxis) {
                    val stepCount = 5
                    val maxValue = data.maxOf { it.value }
                    val effectiveMaxValue = maxValue.toFloat()

                    val stepHeight = (size.height - barBottomPaddingPx) / stepCount
                    val valueStep = effectiveMaxValue / stepCount

                    for (i in 0..stepCount) {
                        val y = size.height - barBottomPaddingPx - i * stepHeight

                        // ---- draw grid line ----
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.4f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1f
                        )

                        // ---- correct label ----
                        val raw = valueStep * i
                        val labelValue = if (effectiveMaxValue >= 100) {
                            (raw / 100f).roundToInt() * 100
                        } else raw.roundToInt()

                        drawContext.canvas.nativeCanvas.drawText(
                            labelValue.toString(),
                            0f,
                            y + 8f,
                            Paint().apply {
                                color = android.graphics.Color.BLACK
                                textSize = 24f
                                textAlign = Paint.Align.LEFT
                            }
                        )
                    }
                }

                // ---------- BARS ----------
                data.forEachIndexed { index, item ->

                    val maxValue = data.maxOf { it.value }
                    val topPaddingFraction = 0.1f
                    val effectiveMaxValue = maxValue.toFloat()

                    val scaleFactor = (size.height - barBottomPaddingPx) / effectiveMaxValue
                    val normalizedHeight = item.value * scaleFactor * animationProgress.value

                    val left = startOffset + index * (barWidth + spacing)
                    val top = size.height - barBottomPaddingPx - normalizedHeight

                    drawRect(
                        color = colors[index % colors.size],
                        topLeft = Offset(left, top),
                        size = Size(barWidth, normalizedHeight)
                    )

                    if (showPercentBarTop) {
                        val percent = item.value.roundToInt()
                        drawContext.canvas.nativeCanvas.drawText(
                            "$percent%",
                            left + barWidth / 2,
                            top - 12,
                            Paint().apply {
                                color = android.graphics.Color.BLACK
                                textSize = 20f
                                textAlign = Paint.Align.CENTER
                                isFakeBoldText = true
                            }
                        )
                    }
                }

                // ---------- X AXIS ----------
                val xAxisStart = if (showYAxis) effectiveYAxisSpacingPx else startOffset
                val xAxisEnd = size.width - spacing

                drawLine(
                    color = Color.Gray.copy(alpha = 0.5f),
                    start = Offset(xAxisStart, size.height - barBottomPaddingPx),
                    end = Offset(xAxisEnd, size.height - barBottomPaddingPx),
                    strokeWidth = 2f
                )
            }
        }

        // ============================
        //      BOTTOM LABELS
        // ============================

        if (showLabel) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {

                val barCount = data.size
                val spacing = with(density) { 8.dp.toPx() }
                val effectiveYAxisSpacingPx = if (showYAxis) yAxisSpacingPx else 0f

                val totalSpacing = spacing * (barCount + 1)
                val barWidth =
                    (constraints.maxWidth.toFloat() - effectiveYAxisSpacingPx - totalSpacing) / barCount

                val startOffset = effectiveYAxisSpacingPx + spacing

                data.forEachIndexed { index, item ->
                    Box(
                        modifier = Modifier
                            .width(with(density) { barWidth.toDp() })
                            .offset(x = with(density) {
                                (startOffset + index * (barWidth + spacing)).toDp()
                            })
                            .wrapContentSize(Alignment.TopCenter)
                    ) {
                        Text(
                            text = item.label,
                            fontSize = 8.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}








