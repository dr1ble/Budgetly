package shmr.budgetly.data.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import shmr.budgetly.data.mapper.toDomainModel
import shmr.budgetly.data.network.dto.UpdateAccountRequestDto
import shmr.budgetly.data.source.remote.account.AccountRemoteDataSource
import shmr.budgetly.data.util.safeApiCall
import shmr.budgetly.di.scope.AppScope
import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.repository.AccountRepository
import shmr.budgetly.domain.util.DomainError
import shmr.budgetly.domain.util.Result
import javax.inject.Inject
import javax.inject.Named

/**
 * Реализация [AccountRepository], отвечающая за получение данных о счетах.
 * Управляет логикой определения текущего счета пользователя (захардкоженный или первый с сервера)
 * и кэширует его ID для последующих запросов.
 */
@AppScope
class AccountRepositoryImpl @Inject constructor(
    private val remoteDataSource: AccountRemoteDataSource, // <-- ИЗМЕНЕНИЕ
    @Named("useHardcodedAccountId") private val useHardcodedAccountId: Boolean
) : AccountRepository {

    // ... остальной код без изменений ...
    @Volatile
    private var cachedAccountId: Int? = null
    private val mutex = Mutex()

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

    override suspend fun updateAccount(
        name: String,
        balance: String,
        currency: String
    ): Result<Account> {
        return when (val accountIdResult = resolveCurrentAccountId()) {
            is Result.Success -> {
                safeApiCall {
                    val request = UpdateAccountRequestDto(name, balance, currency)
                    remoteDataSource.updateAccount(accountIdResult.data, request).toDomainModel()
                }
            }
            is Result.Error -> Result.Error(accountIdResult.error)
        }
    }

    /**
     * Определяет ID текущего счета.
     * Использует захардкоженный ID, если установлен флаг, иначе запрашивает
     * список счетов с сервера и берет ID первого. Результат кэшируется.
     */
    private suspend fun resolveCurrentAccountId(): Result<Int> {
        if (useHardcodedAccountId) return Result.Success(HARDCODED_ACCOUNT_ID)
        cachedAccountId?.let { return Result.Success(it) }

        return mutex.withLock {
            // Повторная проверка внутри мьютекса на случай, если другой поток уже записал значение
            cachedAccountId?.let { return@withLock Result.Success(it) }
            fetchAndCacheFirstAccountId()
        }
    }

    /**
     * Запрашивает аккаунты с сервера и кэширует ID первого найденного.
     */
    private suspend fun fetchAndCacheFirstAccountId(): Result<Int> {
        return when (val result = safeApiCall { remoteDataSource.getAccounts() }) {
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

    private companion object {
        const val HARDCODED_ACCOUNT_ID = 1
    }
}