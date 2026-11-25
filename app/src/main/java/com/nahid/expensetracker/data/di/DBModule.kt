package com.nahid.expensetracker.data.di

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nahid.expensetracker.core.AppConstants
import com.nahid.expensetracker.data.local.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val expenseMigration1To2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE ExpenseEntity ADD COLUMN ownerUid TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE ExpenseEntity ADD COLUMN remoteId TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE ExpenseEntity ADD COLUMN revision TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE ExpenseEntity ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
        // Version 1 did not record ownership. Keep these rows, outside all account queries.
        db.execSQL("UPDATE ExpenseEntity SET remoteId = CAST(id AS TEXT)")
        db.execSQL("CREATE UNIQUE INDEX index_ExpenseEntity_ownerUid_remoteId ON ExpenseEntity(ownerUid, remoteId)")
    }
}

val dbModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, AppConstants.DB_NAME)
            .addMigrations(expenseMigration1To2)
            .build()
    }

    single { get<AppDatabase>().expanseDao() }
}
