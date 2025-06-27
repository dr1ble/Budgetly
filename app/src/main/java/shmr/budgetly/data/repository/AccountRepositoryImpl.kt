package shmr.budgetly.data.repository

import shmr.budgetly.BuildConfig
import shmr.budgetly.data.mapper.toDomainModel
import shmr.budgetly.data.source.remote.RemoteDataSource
import shmr.budgetly.data.util.safeApiCall
import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.repository.AccountRepository
import shmr.budgetly.domain.util.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : AccountRepository {

    private var cachedAccountId: Int? = null

    // Эта логика перенесена сюда из старого репозитория
    private suspend fun resolveCurrentAccountId(): Int {
        if (BuildConfig.USE_HARDCODED_ACCOUNT_ID) {
            return 1
        }
        cachedAccountId?.let { return it }

        val accounts = remoteDataSource.getAccounts()
        val firstAccountId = accounts.firstOrNull()?.id
            ?: throw IllegalStateException("No accounts found for the user.")
        cachedAccountId = firstAccountId
        return firstAccountId
    }

    override suspend fun getMainAccount(): Result<Account> {
        return safeApiCall {
            val accountId = resolveCurrentAccountId()
            val accountDto = remoteDataSource.getAccountById(accountId)
            accountDto.toDomainModel()
        }
    }
}