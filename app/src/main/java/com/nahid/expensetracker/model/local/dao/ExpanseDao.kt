package com.nahid.expensetracker.model.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nahid.expensetracker.model.data.Expanse
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpanseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpanse(expanse: Expanse)

    @Query("SELECT * FROM Expanse")
    fun getAllExpanse(): Flow<List<Expanse>>

    @Update
    suspend fun updateExpanse(expanse: Expanse)

    @Delete
    suspend fun deleteExpanse(expanse: Expanse)

}