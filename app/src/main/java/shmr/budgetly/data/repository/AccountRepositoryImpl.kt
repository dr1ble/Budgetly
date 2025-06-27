package shmr.budgetly.data.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import shmr.budgetly.data.mapper.toDomainModel
import shmr.budgetly.data.source.remote.RemoteDataSource
import shmr.budgetly.data.util.safeApiCall
import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.repository.AccountRepository
import shmr.budgetly.domain.util.DomainError
import shmr.budgetly.domain.util.Result
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    @Named("useHardcodedAccountId") private val useHardcodedAccountId: Boolean
) : AccountRepository {

    @Volatile
    private var cachedAccountId: Int? = null
    private val mutex = Mutex()

    private suspend fun resolveCurrentAccountId(): Result<Int> {
        if (useHardcodedAccountId) {
            return Result.Success(1)
        }

        cachedAccountId?.let { return Result.Success(it) }

        return mutex.withLock {
            cachedAccountId?.let { return@withLock Result.Success(it) }

            when (val result = safeApiCall { remoteDataSource.getAccounts() }) {
                is Result.Success -> {
                    val firstAccountId = result.data.firstOrNull()?.id
                    if (firstAccountId != null) {
                        cachedAccountId = firstAccountId
                        Result.Success(firstAccountId)
                    } else {
                        Result.Error(DomainError.Unknown(IllegalStateException("No accounts found")))
                    }
                }

                is Result.Error -> result
            }
        }
    }

    override suspend fun getMainAccount(): Result<Account> {
        return when (val accountIdResult = resolveCurrentAccountId()) {
            is Result.Success -> {
                safeApiCall {
                    remoteDataSource.getAccountById(accountIdResult.data).toDomainModel()
                }
            }

            is Result.Error -> Result.Error(accountIdResult.error)
        }
    }
}