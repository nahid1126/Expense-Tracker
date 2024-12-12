package com.nahid.expensetracker.model.repository

import com.nahid.expensetracker.model.data.Expanse
import com.nahid.expensetracker.model.local.db.LocalDatabase
import kotlinx.coroutines.flow.Flow

class ExpanseRepository(private val localDatabase: LocalDatabase) {
    private val expanseDao = localDatabase.expanseDao()
    suspend fun insertExpanse(data: Expanse) {
        expanseDao.insertExpanse(data)
    }

    fun getAllExpanse(): Flow<List<Expanse>>? {
        return expanseDao.getAllExpanse()
    }

    suspend fun updateExpanse(data: Expanse) {
        expanseDao.updateExpanse(data)
    }

    suspend fun deleteExpanse(data: Expanse) {
        expanseDao.deleteExpanse(data)
    }

}