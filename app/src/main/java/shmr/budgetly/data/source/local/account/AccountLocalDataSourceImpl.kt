package shmr.budgetly.data.source.local.account

import kotlinx.coroutines.flow.Flow
import shmr.budgetly.data.local.dao.AccountDao
import shmr.budgetly.data.local.model.AccountEntity
import shmr.budgetly.di.scope.AppScope
import javax.inject.Inject

@AppScope
class AccountLocalDataSourceImpl @Inject constructor(
    private val accountDao: AccountDao
) : AccountLocalDataSource {

    override fun getAccountById(id: Int): Flow<AccountEntity?> =
        accountDao.getAccountById(id)

    override suspend fun getDirtyAccounts(): List<AccountEntity> =
        accountDao.getDirtyAccounts()

    override suspend fun upsertAccount(account: AccountEntity) =
        accountDao.upsertAccount(account)
}