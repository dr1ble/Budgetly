package shmr.budgetly.data.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import shmr.budgetly.data.network.exception.NoConnectivityException
import shmr.budgetly.domain.util.DomainError
import shmr.budgetly.domain.util.Result

/**
 * Выполняет предоставленный suspend-блок [apiCall] в IO-контексте.
 * Оборачивает результат в [Result], обрабатывая стандартные исключения сети и HTTP,
 * и преобразуя их в типизированные [DomainError].
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
    return withContext(Dispatchers.IO) {
        try {
            Result.Success(apiCall())
        } catch (e: Exception) {
            Result.Error(mapExceptionToDomainError(e))
        }
    }
}

/**
 * Преобразует исключение в соответствующую доменную ошибку [DomainError].
 */
private fun mapExceptionToDomainError(e: Exception): DomainError {
    return when (e) {
        is NoConnectivityException -> DomainError.NoInternet
        is HttpException -> if (e.code() >= 500) DomainError.ServerError else DomainError.Unknown(e)
        else -> DomainError.Unknown(e)
    }
}