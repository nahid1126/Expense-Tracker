package com.nahid.expensetracker.ui.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nahid.expensetracker.domain.model.asTaka
import java.text.NumberFormat
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

internal data class CashFlowPeriod(
    val label: String,
    val description: String,
    val income: Long,
    val expense: Long,
)

@Composable
internal fun BarChart(
    periods: List<CashFlowPeriod>,
    showIncome: Boolean,
    showExpense: Boolean,
) {
    key(periods.map { it.description }, showIncome, showExpense) {
        CashFlowPlot(periods, showIncome, showExpense)
    }
}

@Composable
private fun CashFlowPlot(
    periods: List<CashFlowPeriod>,
    showIncome: Boolean,
    showExpense: Boolean,
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(-1) }
    val selected = periods.getOrNull(selectedIndex)
    val colors = MaterialTheme.colorScheme
    val peak = periods.maxOf { maxOf(if (showIncome) it.income else 0, if (showExpense) it.expense else 0) }
    val step = axisStep(peak)
    val axisMax = step * 2
    val plotHeight = 126.dp

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (showIncome) {
                ChartTotal(
                    "Income", selected?.income ?: periods.sumOf { it.income },
                    colors.primary, colors.primaryContainer, colors.onPrimaryContainer,
                    Modifier.weight(1f),
                )
            }
            if (showExpense) {
                ChartTotal(
                    "Expenses", selected?.expense ?: periods.sumOf { it.expense },
                    colors.tertiary, colors.tertiaryContainer, colors.onTertiaryContainer,
                    Modifier.weight(1f),
                )
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            Column(
                Modifier.height(plotHeight),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                for (tick in listOf(axisMax, step, 0)) {
                    Text(
                        axisLabel(tick),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant,
                    )
                }
            }
            BoxWithConstraints(Modifier.weight(1f)) {
                val slotWidth = maxOf(52.dp, maxWidth / periods.size.coerceAtLeast(1))
                val visibleSlots = (maxWidth / slotWidth).toInt().coerceAtLeast(1)
                val scrollState = rememberLazyListState(
                    initialFirstVisibleItemIndex = (periods.size - visibleSlots).coerceAtLeast(0),
                )
                Column(Modifier.fillMaxWidth().height(plotHeight), verticalArrangement = Arrangement.SpaceBetween) {
                    repeat(3) { HorizontalDivider(color = colors.outlineVariant) }
                }
                LazyRow(state = scrollState) {
                    itemsIndexed(periods, key = { _, period -> period.description }) { index, period ->
                        val active = selectedIndex == index
                        Column(
                            Modifier.width(slotWidth).clip(RoundedCornerShape(9.dp))
                                .background(if (active) colors.surfaceContainer else Color.Transparent)
                                .selectable(
                                    selected = active,
                                    role = Role.Button,
                                    onClick = { selectedIndex = if (active) -1 else index },
                                )
                                .semantics {
                                    val summary = buildList {
                                        add(period.description)
                                        if (showIncome) add("Income ${period.income.asTaka()}")
                                        if (showExpense) add("Expenses ${period.expense.asTaka()}")
                                    }
                                    contentDescription = summary.joinToString(", ")
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Row(
                                Modifier.height(plotHeight),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.Bottom,
                            ) {
                                if (showIncome) ChartBar(period.income, axisMax, plotHeight, colors.primary)
                                if (showExpense) ChartBar(period.expense, axisMax, plotHeight, colors.tertiary)
                            }
                            Text(
                                period.label,
                                Modifier.padding(horizontal = 2.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (active) colors.onSurface else colors.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }

        Text(
            selected?.description ?: "Tap a bar to see its totals",
            style = MaterialTheme.typography.labelMedium,
            color = colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun ChartBar(amount: Long, axisMax: Long, plotHeight: Dp, color: Color) {
    val fraction = (amount.toDouble() / axisMax).coerceIn(0.0, 1.0).toFloat()
    Box(
        Modifier.width(16.dp).height(plotHeight * fraction)
            .background(color, RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)),
    )
}

@Composable
private fun ChartTotal(
    label: String,
    value: Long,
    accent: Color,
    container: Color,
    foreground: Color,
    modifier: Modifier,
) {
    Surface(modifier, shape = RoundedCornerShape(14.dp), color = container, contentColor = foreground) {
        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).background(accent, CircleShape))
                Text(label, style = MaterialTheme.typography.labelMedium)
            }
            Text(value.asTaka(), style = MaterialTheme.typography.titleMedium)
        }
    }
}

private fun axisStep(peak: Long): Long {
    val midpoint = (peak / 2.0).coerceAtLeast(1.0)
    val magnitude = 10.0.pow(floor(log10(midpoint)))
    return (ceil(midpoint / magnitude) * magnitude).toLong()
}

private fun axisLabel(value: Long): String {
    val (unit, suffix) = when {
        value >= 1_000_000_000 -> 1_000_000_000 to "B"
        value >= 1_000_000 -> 1_000_000 to "M"
        value >= 1_000 -> 1_000 to "K"
        else -> return value.asTaka()
    }
    val formatted = NumberFormat.getNumberInstance().apply { maximumFractionDigits = 1 }
        .format(value.toDouble() / unit)
    return "৳$formatted$suffix"
}
