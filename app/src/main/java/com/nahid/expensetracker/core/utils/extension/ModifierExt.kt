package com.nahid.expensetracker.core.utils.extension

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role

/**
 * A modifier that prevents multiple clicks within a short period (1000ms by default).
 */
fun Modifier.onSingleClick(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    delay: Long = 1000L,
    onClick: () -> Unit
): Modifier = composed {
    val lastClickTime = remember { mutableLongStateOf(0L) }
    val interactionSource = remember { MutableInteractionSource() }
    val indication = LocalIndication.current

    this.clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        interactionSource = interactionSource,
        indication = indication
    ) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime.longValue > delay) {
            lastClickTime.longValue = currentTime
            onClick()
        }
    }
}
