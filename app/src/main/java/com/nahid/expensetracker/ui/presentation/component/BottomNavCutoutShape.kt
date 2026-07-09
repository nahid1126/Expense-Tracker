package com.nahid.expensetracker.ui.presentation.component

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density

class BottomNavCutoutShape(
    private val cornerRadius: Float = 40f,
    private val notchRadius: Float = 45f,
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: Density,
    ): Outline {
        val centerX = size.width / 2f
        val notchWidth = notchRadius * 2.3f
        val notchDepth = notchRadius * 1.7f

        val path = Path().apply {

            moveTo(cornerRadius, size.height)

            // Bottom Left
            arcTo(
                Rect(
                    0f,
                    size.height - cornerRadius * 2,
                    cornerRadius * 2,
                    size.height
                ),
                90f,
                90f,
                false
            )

            // Left Side
            lineTo(0f, cornerRadius)

            // Top Left Corner
            arcTo(
                Rect(
                    0f,
                    0f,
                    cornerRadius * 2,
                    cornerRadius * 2
                ),
                180f,
                90f,
                false
            )

            // ---------- CUTOUT ----------

            lineTo(centerX - notchWidth, 0f)

            cubicTo(
                centerX - notchWidth * 0.70f,
                4f,
                centerX - notchRadius * 1.2f,
                notchDepth,
                centerX,
                notchDepth
            )

            cubicTo(
                centerX + notchRadius * 1.2f,
                notchDepth,
                centerX + notchWidth * 0.70f,
                4f,
                centerX + notchWidth,
                0f
            )

            // ---------- END CUTOUT ----------

            lineTo(size.width - cornerRadius, 0f)

            // Top Right Corner
            arcTo(
                Rect(
                    size.width - cornerRadius * 2,
                    0f,
                    size.width,
                    cornerRadius * 2
                ),
                270f,
                90f,
                false
            )

            // Right Side
            lineTo(size.width, size.height - cornerRadius)

            // Bottom Right Corner
            arcTo(
                Rect(
                    size.width - cornerRadius * 2,
                    size.height - cornerRadius * 2,
                    size.width,
                    size.height
                ),
                0f,
                90f,
                false
            )

            close()
        }

        return Outline.Generic(path)
    }
}