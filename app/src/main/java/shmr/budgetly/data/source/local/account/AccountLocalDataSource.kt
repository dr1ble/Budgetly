package shmr.budgetly.data.source.local.account

import kotlinx.coroutines.flow.Flow
import shmr.budgetly.data.local.model.AccountEntity

interface AccountLocalDataSource {
    fun getAccountById(id: Int): Flow<AccountEntity?>
    suspend fun getAccountByIdSync(id: Int): AccountEntity?
    suspend fun getDirtyAccounts(): List<AccountEntity>
    suspend fun upsertAccount(account: AccountEntity)
}