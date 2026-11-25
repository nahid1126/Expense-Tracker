package com.nahid.expensetracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nahid.expensetracker.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Query("SELECT * FROM ExpenseEntity WHERE ownerUid = :uid AND isDeleted = 0")
    fun observeExpenses(uid: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM ExpenseEntity WHERE ownerUid = :uid AND isSynced = 0 ORDER BY id")
    suspend fun getPending(uid: String): List<ExpenseEntity>

    @Query("""
        UPDATE ExpenseEntity SET title = :title, amount = :amount, date = :date,
            type = :type, category = :category, isSynced = 0, revision = :revision
        WHERE id = :id AND ownerUid = :uid AND isDeleted = 0
    """)
    suspend fun edit(id: Int, uid: String, title: String, amount: Int, date: String,
                     type: String, category: String, revision: String): Int

    @Query("""
        UPDATE ExpenseEntity SET isDeleted = 1, isSynced = 0, revision = :revision
        WHERE id = :id AND ownerUid = :uid AND isDeleted = 0
    """)
    suspend fun markDeleted(id: Int, uid: String, revision: String): Int

    @Query("UPDATE ExpenseEntity SET isSynced = 1 WHERE id = :id AND ownerUid = :uid AND revision = :revision")
    suspend fun acknowledge(id: Int, uid: String, revision: String)

    @Query("DELETE FROM ExpenseEntity WHERE id = :id AND ownerUid = :uid AND revision = :revision AND isDeleted = 1")
    suspend fun removeTombstone(id: Int, uid: String, revision: String)

    @Query("""
        UPDATE ExpenseEntity SET title = :title, amount = :amount, date = :date,
            type = :type, category = :category
        WHERE ownerUid = :uid AND remoteId = :remoteId AND isSynced = 1 AND isDeleted = 0
    """)
    suspend fun refreshSynced(uid: String, remoteId: String, title: String, amount: Int,
                              date: String, type: String, category: String)

    @Query("SELECT remoteId FROM ExpenseEntity WHERE ownerUid = :uid AND isSynced = 1")
    suspend fun syncedRemoteIds(uid: String): List<String>

    @Query("DELETE FROM ExpenseEntity WHERE ownerUid = :uid AND remoteId = :remoteId AND isSynced = 1")
    suspend fun removeMissing(uid: String, remoteId: String)

    @Transaction
    suspend fun mergeSnapshot(uid: String, remoteIds: Set<String>, expenses: List<ExpenseEntity>) {
        for (expense in expenses) {
            if (insertExpense(expense) == -1L) {
                refreshSynced(uid, expense.remoteId, expense.title, expense.amount, expense.date,
                    expense.type, expense.category)
            }
        }
        syncedRemoteIds(uid).filterNot(remoteIds::contains).forEach { removeMissing(uid, it) }
    }
}
