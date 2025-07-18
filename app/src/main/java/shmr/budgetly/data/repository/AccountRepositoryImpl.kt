package shmr.budgetly.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import shmr.budgetly.data.local.model.toDomainModel
import shmr.budgetly.data.local.model.toEntity
import shmr.budgetly.data.mapper.toDomainModel
import shmr.budgetly.data.mapper.toEntity
import shmr.budgetly.data.network.dto.UpdateAccountRequestDto
import shmr.budgetly.data.source.local.account.AccountLocalDataSource
import shmr.budgetly.data.source.remote.account.AccountRemoteDataSource
import shmr.budgetly.data.util.safeApiCall
import shmr.budgetly.di.scope.AppScope
import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.repository.AccountRepository
import shmr.budgetly.domain.util.DomainError
import shmr.budgetly.domain.util.Result
import javax.inject.Inject
import javax.inject.Named

@AppScope
class AccountRepositoryImpl @Inject constructor(
    private val remoteDataSource: AccountRemoteDataSource,
    private val localDataSource: AccountLocalDataSource,
    @Named("useHardcodedAccountId") private val useHardcodedAccountId: Boolean
) : AccountRepository {

    @Volatile
    private var cachedAccountId: Int? = null
    private val mutex = Mutex()

    override fun getMainAccount(): Flow<Result<Account>> {
        return resolveCurrentAccountId().flatMapLatest { accountIdResult ->
            when (accountIdResult) {
                is Result.Success -> {
                    localDataSource.getAccountById(accountIdResult.data).map { entity ->
                        if (entity != null) {
                            Result.Success(entity.toDomainModel())
                        } else {
                            Result.Error(DomainError.Unknown(IllegalStateException("Account ${accountIdResult.data} not found in DB.")))
                        }
                    }
                }

                is Result.Error -> {
                    flowOf(Result.Error(accountIdResult.error))
                }
            }
        }
    }

    override suspend fun refreshMainAccount() {
        val accountIdResult = resolveCurrentAccountIdSuspend()
        if (accountIdResult is Result.Success) {
            val result = safeApiCall { remoteDataSource.getAccountById(accountIdResult.data) }
            if (result is Result.Success) {
                localDataSource.upsertAccount(result.data.toEntity())
            } else {
                Log.e(
                    "AccountRepository",
                    "Failed to refresh account: ${(result as? Result.Error)?.error}"
                )
            }
        }
    }


    override suspend fun updateAccount(
        name: String,
        balance: String,
        currency: String
    ): Result<Account> {
        val accountIdResult = resolveCurrentAccountIdSuspend()
        if (accountIdResult is Result.Error) {
            return Result.Error(accountIdResult.error)
        }
        val accountId = (accountIdResult as Result.Success).data

        val updatedAccount = Account(accountId, name, balance, currency)
        localDataSource.upsertAccount(updatedAccount.toEntity(isDirty = true))

        val remoteResult = safeApiCall {
            val request = UpdateAccountRequestDto(name, balance, currency)
            remoteDataSource.updateAccount(accountId, request)
        }

        return if (remoteResult is Result.Success) {
            localDataSource.upsertAccount(remoteResult.data.toEntity(isDirty = false))
            Result.Success(remoteResult.data.toDomainModel())
        } else {
            Result.Success(updatedAccount)
        }
    }

    private fun resolveCurrentAccountId(): Flow<Result<Int>> = flow {
        // Эта функция теперь всегда эмитит значение перед завершением.
        val result = resolveCurrentAccountIdSuspend()
        emit(result)
    }

    private suspend fun resolveCurrentAccountIdSuspend(): Result<Int> {
        if (useHardcodedAccountId) return Result.Success(HARDCODED_ACCOUNT_ID)
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
                        Result.Error(DomainError.Unknown(IllegalStateException("No accounts found on server")))
                    }
                }

                is Result.Error -> result
            }
        }
    }

    private companion object {
        const val HARDCODED_ACCOUNT_ID = 1
    }
}