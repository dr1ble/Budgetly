package shmr.budgetly.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import shmr.budgetly.data.local.AppDatabase
import shmr.budgetly.di.scope.AppScope

/**
 * Модуль Dagger для предоставления зависимостей базы данных Room.
 */
@Module
object DatabaseModule {

    @Provides
    @AppScope
    fun provideAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "budgetly_database"
        ).build()
    }

    @Provides
    @AppScope
    fun provideAccountDao(database: AppDatabase) = database.accountDao()

    @Provides
    @AppScope
    fun provideCategoryDao(database: AppDatabase) = database.categoryDao()

    @Provides
    @AppScope
    fun provideTransactionDao(database: AppDatabase) = database.transactionDao()
}