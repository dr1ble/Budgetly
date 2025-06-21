package shmr.budgetly.domain.util

/**
 * Класс, представляющий типизированные ошибки, которые могут возникнуть
 * в доменном слое или слое данных.
 */
sealed class DomainError {

    data object NoInternet : DomainError()

    data object ServerError : DomainError()

    data class Unknown(val throwable: Throwable? = null) : DomainError()
}