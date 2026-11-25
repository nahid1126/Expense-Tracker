package com.nahid.expensetracker

import com.nahid.expensetracker.domain.model.Expense
import com.nahid.expensetracker.domain.model.asTransactionDate
import com.nahid.expensetracker.domain.model.isInMonth
import com.nahid.expensetracker.domain.model.recentTransactionOrder
import com.nahid.expensetracker.domain.model.toPickerMillis
import com.nahid.expensetracker.domain.model.transactionDateFromPicker
import com.nahid.expensetracker.domain.model.validTransactionAmount
import com.nahid.expensetracker.ui.presentation.home.spendingBreakdown
import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TransactionLogicTest {
    @Test
    fun recentTransactionsUseDateAcrossYearsAndAcceptLegacyFormat() {
        val earlier = expense(amount = 900, date = "31-Dec-2025")
        val later = expense(amount = 10, date = "2026-01-02")
        assertEquals(listOf(later, earlier), listOf(earlier, later).sortedWith(recentTransactionOrder))
        assertTrue(earlier.isInMonth(YearMonth.of(2025, 12)))
        assertFalse(earlier.isInMonth(YearMonth.of(2026, 12)))
        assertNull("31-Feb-2026".asTransactionDate())
    }

    @Test
    fun datePickerRoundTripUsesUtcCalendarDate() {
        val selected = LocalDate.of(2026, 1, 2)
        assertEquals(selected, transactionDateFromPicker(selected.toPickerMillis()))
    }

    @Test
    fun invalidAndOverflowingAmountsAreRejected() {
        listOf("", "0", "-1", "2147483648").forEach { assertNull(it.validTransactionAmount()) }
        assertEquals(Int.MAX_VALUE, "2147483647".validTransactionAmount())
    }

    @Test
    fun donutGroupsCategoriesAndOnlyCountsExpensesWithLongTotals() {
        val transactions = listOf(
            expense(Int.MAX_VALUE, category = "Food"),
            expense(Int.MAX_VALUE, category = " food "),
            expense(100, category = "Salary", type = "Income"),
        ) + (1..7).map { expense(it, category = "Category $it") }
        val spending = spendingBreakdown(transactions)
        assertEquals(6, spending.size)
        assertEquals(2L * Int.MAX_VALUE, spending.first().amount)
        assertEquals(6L, spending.last().amount)
        assertEquals(2L * Int.MAX_VALUE + 28, spending.sumOf { it.amount })
    }

    private fun expense(
        amount: Int,
        date: String = "2026-01-02",
        category: String = "Food",
        type: String = "Expense",
    ) = Expense(title = category, amount = amount, date = date, type = type, category = category)
}
