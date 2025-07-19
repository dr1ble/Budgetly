package shmr.budgetly.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import shmr.budgetly.data.local.model.toDomainModel
import shmr.budgetly.data.local.model.toEntity
import shmr.budgetly.data.mapper.toEntity
import shmr.budgetly.data.source.local.category.CategoryLocalDataSource
import shmr.budgetly.data.source.local.transaction.TransactionLocalDataSource
import shmr.budgetly.data.source.remote.transaction.TransactionRemoteDataSource
import shmr.budgetly.data.util.safeApiCall
import shmr.budgetly.di.scope.AppScope
import shmr.budgetly.domain.entity.Category
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.repository.AccountRepository
import shmr.budgetly.domain.repository.TransactionRepository
import shmr.budgetly.domain.util.Result
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AppScope
class TransactionRepositoryImpl @Inject constructor(
    private val remoteDataSource: TransactionRemoteDataSource,
    private val localDataSource: TransactionLocalDataSource,
    private val categoryLocalDataSource: CategoryLocalDataSource,
    private val accountRepository: AccountRepository
) : TransactionRepository {

    private val isoLocalDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override fun getTransactions(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<Result<List<Transaction>>> {
        return localDataSource.getTransactionsForPeriod(
            startDate.format(isoLocalDateFormatter),
            endDate.format(isoLocalDateFormatter)
        ).map { list ->
            Result.Success(list.map { it.transaction.toDomainModel(it.category.toDomainModel()) })
        }
    }

    override suspend fun refreshTransactions(
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<Unit> {
        accountRepository.refreshMainAccount()

        val accountResult = accountRepository.getMainAccount().first()
        if (accountResult is Result.Error) {
            return Result.Error(accountResult.error)
        }
        val accountId = (accountResult as Result.Success).data.id

        val remoteResult = safeApiCall {
            remoteDataSource.getTransactionsForPeriod(
                accountId,
                startDate.format(isoLocalDateFormatter),
                endDate.format(isoLocalDateFormatter)
            )
        }

        return when (remoteResult) {
            is Result.Success -> {
                val transactionsFromNetwork = remoteResult.data

                // Получаем ID всех локально измененных транзакций
                val dirtyTransactionIds =
                    localDataSource.getDirtyTransactions().map { it.id }.toSet()

                // Фильтруем список с сервера: убираем из него те транзакции, которые были изменены локально
                val transactionsToUpsert = transactionsFromNetwork.filter {
                    !dirtyTransactionIds.contains(it.id)
                }

                // Извлекаем все уникальные категории из отфильтрованного списка
                val categoriesFromNetwork =
                    transactionsToUpsert.map { it.category.toEntity() }.distinctBy { it.id }

                if (categoriesFromNetwork.isNotEmpty()) {
                    categoryLocalDataSource.upsertAll(categoriesFromNetwork)
                }

                if (transactionsToUpsert.isNotEmpty()) {
                    localDataSource.upsertTransactions(transactionsToUpsert.map {
                        it.toEntity(
                            isDirty = false
                        )
                    })
                }

                Result.Success(Unit)
            }

            is Result.Error -> {
                Result.Error(remoteResult.error)
            }
        }
    }


    override fun getTransactionById(id: Int): Flow<Result<Transaction>> {
        return localDataSource.getTransactionById(id).map { entity ->
            if (entity != null) {
                Result.Success(entity.transaction.toDomainModel(entity.category.toDomainModel()))
            } else {
                Result.Error(shmr.budgetly.domain.util.DomainError.Unknown(Exception("Transaction not found")))
            }
        }
    }

    override suspend fun createTransaction(
        accountId: Int,
        categoryId: Int,
        amount: String,
        transactionDate: LocalDateTime,
        comment: String
    ): Result<Transaction> {
        val accountResult = accountRepository.getMainAccount().first()
        if (accountResult is Result.Error) {
            return Result.Error(accountResult.error)
        }
        val account = (accountResult as Result.Success).data

        val tempId = (System.currentTimeMillis() / 1000).toInt() * -1

        val transaction = Transaction(
            id = tempId,
            category = Category(id = categoryId, name = "", emoji = "", isIncome = false),
            amount = amount,
            currency = account.currency,
            transactionDate = transactionDate,
            comment = comment
        )
        val entity = transaction.toEntity(isDirty = true).copy(accountId = account.id)

        localDataSource.upsertTransaction(entity)
        return getTransactionById(tempId).first()
    }

    override suspend fun updateTransaction(
        id: Int,
        accountId: Int,
        categoryId: Int,
        amount: String,
        transactionDate: LocalDateTime,
        comment: String
    ): Result<Transaction> {
        val accountResult = accountRepository.getMainAccount().first()
        if (accountResult is Result.Error) {
            return Result.Error(accountResult.error)
        }
        val account = (accountResult as Result.Success).data

        val transaction = Transaction(
            id = id,
            category = Category(id = categoryId, name = "", emoji = "", isIncome = false),
            amount = amount,
            currency = account.currency,
            transactionDate = transactionDate,
            comment = comment
        )
        val entity = transaction.toEntity(isDirty = true).copy(accountId = account.id)
        localDataSource.upsertTransaction(entity)
        return getTransactionById(id).first()
    }


    override suspend fun deleteTransaction(id: Int): Result<Unit> {
        if (id < 0) {
            localDataSource.deleteById(id)
        } else {
            localDataSource.markAsDeleted(id, System.currentTimeMillis())
        }
        return Result.Success(Unit)
    }
}