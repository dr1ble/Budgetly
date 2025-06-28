package shmr.budgetly.domain.util

/**
 * Обертка для представления результата операции, которая может завершиться
 * либо успехом, либо ошибкой.
 * @param T Тип данных в случае успеха.
 */
sealed class Result<out T> {
    /**
     * Представляет успешный результат операции.
     * @param data Данные, полученные в результате.
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Представляет ошибку, произошедшую во время операции.
     * @param error Типизированная доменная ошибка.
     */
    data class Error(val error: DomainError) : Result<Nothing>()
}