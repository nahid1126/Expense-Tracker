package com.nahid.expensetracker.model.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nahid.expensetracker.model.data.Expanse
import com.nahid.expensetracker.model.local.dao.ExpanseDao

@Database(entities = [Expanse::class], version = 1, exportSchema = false)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun expanseDao(): ExpanseDao

    companion object {
        private const val DATABASE_NAME = "Expanse_Database"

        @JvmStatic
        fun getDataBase(context: Context): LocalDatabase {
            return Room.databaseBuilder(
                context,
                LocalDatabase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}