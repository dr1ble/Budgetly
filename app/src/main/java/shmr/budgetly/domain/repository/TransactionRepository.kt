package shmr.budgetly.domain.repository

import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.util.Result
import java.time.LocalDate

/**
 * Репозиторий для управления данными транзакций.
 * Абстрагирует доменный слой от источников данных.
 */
interface TransactionRepository {
    /**
     * Получает список транзакций за указанный период.
     * @param startDate Начальная дата периода.
     * @param endDate Конечная дата периода.
     * @return [Result] со списком [Transaction] в случае успеха или [DomainError] в случае ошибки.
     */
    suspend fun getTransactions(startDate: LocalDate, endDate: LocalDate): Result<List<Transaction>>
}