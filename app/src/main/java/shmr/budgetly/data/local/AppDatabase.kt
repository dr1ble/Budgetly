package shmr.budgetly.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import shmr.budgetly.data.local.dao.AccountDao
import shmr.budgetly.data.local.dao.CategoryDao
import shmr.budgetly.data.local.dao.TransactionDao
import shmr.budgetly.data.local.model.AccountEntity
import shmr.budgetly.data.local.model.CategoryEntity
import shmr.budgetly.data.local.model.TransactionEntity

/**
 * Основной класс базы данных приложения.
 */
@Database(
    entities = [AccountEntity::class, CategoryEntity::class, TransactionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
}