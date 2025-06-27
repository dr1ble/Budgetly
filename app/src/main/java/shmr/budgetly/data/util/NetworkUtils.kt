package shmr.budgetly.data.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import shmr.budgetly.data.network.exception.NoConnectivityException
import shmr.budgetly.domain.util.DomainError
import shmr.budgetly.domain.util.Result

/**
 * Выполняет предоставленный suspend-блок [apiCall] в IO-контексте
 * и оборачивает результат в [Result], обрабатывая стандартные исключения.
 * Эта функция вынесена для переиспользования во всех репозиториях.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
    return withContext(Dispatchers.IO) {
        try {
            Result.Success(apiCall.invoke())
        } catch (e: Exception) {
            Result.Error(mapExceptionToDomainError(e))
        }
    }
}

private fun mapExceptionToDomainError(e: Exception): DomainError {
    return when (e) {
        is NoConnectivityException -> DomainError.NoInternet
        is HttpException -> if (e.code() >= 500) DomainError.ServerError else DomainError.Unknown(e)
        else -> DomainError.Unknown(e)
    }
}