package com.nahid.expensetracker.ui.presentation.component

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class BottomNavCutoutShape(private val cornerRadius: Float = 60f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            Path().apply {
                val cutoutRadius = size.width * 0.08f
                val centerX = size.width / 2

                // Start from bottom left
                moveTo(0f, size.height)
                
                // Left side up to top corner
                lineTo(0f, cornerRadius)
                
                // Top-left corner
                arcTo(
                    Rect(0f, 0f, cornerRadius * 2, cornerRadius * 2),
                    180f, 90f, false
                )
                
                // Line to cutout start
                lineTo(centerX - cutoutRadius * 1.6f, 0f)

                // Smooth cutout arc
                arcTo(
                    rect = Rect(
                        left = centerX - cutoutRadius * 1.6f,
                        top = -cutoutRadius * 0.8f,
                        right = centerX + cutoutRadius * 1.6f,
                        bottom = cutoutRadius * 1.2f
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = -180f,
                    forceMoveTo = false
                )

                // Line to top-right corner
                lineTo(size.width - cornerRadius, 0f)
                
                // Top-right corner
                arcTo(
                    Rect(size.width - cornerRadius * 2, 0f, size.width, cornerRadius * 2),
                    270f, 90f, false
                )

                // Right side down to bottom
                lineTo(size.width, size.height)
                
                // Bottom edge
                lineTo(0f, size.height)
                close()
            }
        )
    }
}