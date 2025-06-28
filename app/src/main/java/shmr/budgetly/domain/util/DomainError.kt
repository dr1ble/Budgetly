package shmr.budgetly.domain.util

/**
 * Представляет типизированные ошибки, которые могут возникнуть в доменном слое
 * или слое данных. Позволяет UI-слою реагировать на конкретные виды ошибок.
 */
sealed class DomainError {

    /** Ошибка, связанная с отсутствием интернет-соединения. */
    data object NoInternet : DomainError()

    /** Ошибка на стороне сервера (5xx). */
    data object ServerError : DomainError()

    /** Неизвестная или нетипизированная ошибка. */
    data class Unknown(val throwable: Throwable? = null) : DomainError()
}