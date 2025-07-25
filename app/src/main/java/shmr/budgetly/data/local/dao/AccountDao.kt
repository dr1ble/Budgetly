package shmr.budgetly.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import shmr.budgetly.data.local.model.AccountEntity

/**
 * DAO для работы с сущностью AccountEntity.
 */
@Dao
interface AccountDao {
    /**
     * Вставляет или обновляет счет.
     */
    @Upsert
    suspend fun upsertAccount(account: AccountEntity)

    /**
     * Получает счет по ID в виде потока.
     */
    @Query("SELECT * FROM accounts WHERE id = :id")
    fun getAccountById(id: Int): Flow<AccountEntity?>

    /**
     * Получает счет по ID один раз (не поток).
     * Нужен для расчетов внутри транзакций.
     */
    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getAccountByIdSync(id: Int): AccountEntity?

    /**
     * Получает все "грязные" счета (измененные локально).
     */
    @Query("SELECT * FROM accounts WHERE isDirty = 1")
    suspend fun getDirtyAccounts(): List<AccountEntity>
}