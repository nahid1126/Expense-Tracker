package com.nahid.expensetracker

import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nahid.expensetracker.data.di.expenseMigration1To2
import com.nahid.expensetracker.data.local.AppDatabase
import com.nahid.expensetracker.data.local.entity.ExpenseEntity
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExpenseStorageTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun migrationPreservesUnownedLegacyRowsWithoutExposingThemToAccounts() = runBlocking {
        val name = "expense-migration-${UUID.randomUUID()}"
        val path = context.getDatabasePath(name)
        path.parentFile?.mkdirs()
        try {
            SQLiteDatabase.openOrCreateDatabase(path, null).use { legacy ->
                legacy.execSQL("""
                    CREATE TABLE ExpenseEntity (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title TEXT NOT NULL, amount INTEGER NOT NULL, date TEXT NOT NULL,
                        type TEXT NOT NULL, category TEXT NOT NULL, isSynced INTEGER NOT NULL
                    )
                """.trimIndent())
                legacy.execSQL("INSERT INTO ExpenseEntity VALUES (1, 'Legacy', 50, '01-Jan-2025', 'Expense', 'Food', 0)")
                legacy.version = 1
            }
            val database = Room.databaseBuilder(context, AppDatabase::class.java, name)
                .addMigrations(expenseMigration1To2)
                .build()
            try {
                val dao = database.expanseDao()
                assertTrue(dao.observeExpenses("account-a").first().isEmpty())
                assertTrue(dao.getPending("account-a").isEmpty())
                assertEquals("Legacy", dao.getPending("").single().title)
            } finally {
                database.close()
            }
        } finally {
            context.deleteDatabase(name)
        }
        Unit
    }

    @Test
    fun pendingChangesSurviveRefreshAndOldAcknowledgement() = runBlocking {
        val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        try {
            val dao = database.expanseDao()
            val original = ExpenseEntity(title = "First", amount = 50, date = "2026-01-02",
                type = "Expense", category = "Food", ownerUid = "a", remoteId = "stable-id")
            val id = dao.insertExpense(original).toInt()
            dao.insertExpense(original.copy(ownerUid = "b"))
            assertEquals(0, dao.edit(id, "b", "Wrong account", 1, original.date,
                original.type, original.category, "bad"))
            assertEquals(1, dao.edit(id, "a", "Edited", 70, original.date,
                original.type, original.category, "edit"))
            dao.acknowledge(id, "a", "")
            dao.mergeSnapshot("a", setOf(original.remoteId), listOf(original.copy(isSynced = true)))
            assertEquals("Edited", dao.getPending("a").single().title)
            assertEquals(1, dao.markDeleted(id, "a", "delete"))
            dao.mergeSnapshot("a", setOf(original.remoteId), listOf(original.copy(isSynced = true)))
            assertTrue(dao.observeExpenses("a").first().isEmpty())
            assertEquals("First", dao.observeExpenses("b").first().single().title)
            assertTrue(dao.getPending("a").single().isDeleted)
            dao.removeTombstone(id, "a", "delete")
            assertTrue(dao.getPending("a").isEmpty())
        } finally {
            database.close()
        }
    }
}
