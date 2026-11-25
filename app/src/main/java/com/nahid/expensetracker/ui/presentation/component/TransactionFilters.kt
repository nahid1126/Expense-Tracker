package com.nahid.expensetracker.ui.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
internal fun TransactionFilters(
    options: List<String>,
    selected: String?,
    onSelect: (String) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val pill = RoundedCornerShape(13.dp)
    Row(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(17.dp))
            .background(colors.surfaceContainer)
            .selectableGroup()
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        for (option in options) {
            val active = option == selected
            Box(
                Modifier.weight(1f)
                    .clip(pill)
                    .background(if (active) colors.primary else colors.surfaceContainer)
                    .selectable(selected = active, role = Role.Tab, onClick = { onSelect(option) })
                    .heightIn(min = 48.dp)
                    .padding(horizontal = 5.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    option,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (active) colors.onPrimary else colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
