package com.nahid.expensetracker.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nahid.expensetracker.core.Results
import com.nahid.expensetracker.domain.repository.ExpenseRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val expenseRepository: ExpenseRepository by inject()

    override suspend fun doWork(): Result {
        return when (expenseRepository.syncUnsyncedExpenses()) {
            is Results.Success -> Result.success()
            is Results.Error -> Result.retry()
        }
    }
}
