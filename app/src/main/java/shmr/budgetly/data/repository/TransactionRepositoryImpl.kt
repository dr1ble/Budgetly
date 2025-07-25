package shmr.budgetly.data.repository

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import shmr.budgetly.data.local.AppDatabase
import shmr.budgetly.data.local.model.toDomainModel
import shmr.budgetly.data.local.model.toEntity
import shmr.budgetly.data.mapper.toEntity
import shmr.budgetly.data.source.local.account.AccountLocalDataSource
import shmr.budgetly.data.source.local.category.CategoryLocalDataSource
import shmr.budgetly.data.source.local.transaction.TransactionLocalDataSource
import shmr.budgetly.data.source.remote.transaction.TransactionRemoteDataSource
import shmr.budgetly.data.util.safeApiCall
import shmr.budgetly.di.scope.AppScope
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.repository.AccountRepository
import shmr.budgetly.domain.repository.TransactionRepository
import shmr.budgetly.domain.util.Result
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AppScope
class TransactionRepositoryImpl @Inject constructor(
    private val remoteDataSource: TransactionRemoteDataSource,
    private val localDataSource: TransactionLocalDataSource,
    private val categoryLocalDataSource: CategoryLocalDataSource,
    private val accountLocalDataSource: AccountLocalDataSource,
    private val accountRepository: AccountRepository,
    private val database: AppDatabase
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

                val dirtyTransactionIds =
                    localDataSource.getDirtyTransactions().map { it.id }.toSet()

                val transactionsToUpsert = transactionsFromNetwork.filter {
                    !dirtyTransactionIds.contains(it.id)
                }

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
    ): Result<Transaction> = database.withTransaction {

        val category = categoryLocalDataSource.getCategoryById(categoryId)
            ?: return@withTransaction Result.Error(
                shmr.budgetly.domain.util.DomainError.Unknown(
                    Exception("Category not found")
                )
            )

        val account = accountLocalDataSource.getAccountByIdSync(accountId)
            ?: return@withTransaction Result.Error(
                shmr.budgetly.domain.util.DomainError.Unknown(
                    Exception("Account not found")
                )
            )

        val amountDecimal = amount.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val currentBalance = account.balance.toBigDecimalOrNull() ?: BigDecimal.ZERO

        val newBalance = if (category.isIncome) {
            currentBalance.add(amountDecimal)
        } else {
            currentBalance.subtract(amountDecimal)
        }

        accountLocalDataSource.upsertAccount(
            account.copy(
                balance = newBalance.toPlainString(),
                isDirty = true
            )
        )

        val tempId = (System.currentTimeMillis() / 1000).toInt() * -1
        val transaction = Transaction(
            id = tempId,
            category = category.toDomainModel(),
            amount = amount,
            currency = account.currency,
            transactionDate = transactionDate,
            comment = comment
        )
        val entity = transaction.toEntity(isDirty = true).copy(accountId = account.id)

        localDataSource.upsertTransaction(entity)
        return@withTransaction getTransactionById(tempId).first()
    }


    override suspend fun updateTransaction(
        id: Int,
        accountId: Int,
        categoryId: Int,
        amount: String,
        transactionDate: LocalDateTime,
        comment: String
    ): Result<Transaction> = database.withTransaction {
        val oldTransactionWithCategory = localDataSource.getTransactionById(id).first()
            ?: return@withTransaction Result.Error(
                shmr.budgetly.domain.util.DomainError.Unknown(
                    Exception("Transaction to update not found")
                )
            )
        val oldTransactionEntity = oldTransactionWithCategory.transaction

        val newCategory = categoryLocalDataSource.getCategoryById(categoryId)
            ?: return@withTransaction Result.Error(
                shmr.budgetly.domain.util.DomainError.Unknown(
                    Exception("New category not found")
                )
            )

        val account = accountLocalDataSource.getAccountByIdSync(accountId)
            ?: return@withTransaction Result.Error(
                shmr.budgetly.domain.util.DomainError.Unknown(
                    Exception("Account not found")
                )
            )

        val oldAmount = oldTransactionEntity.amount.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val newAmount = amount.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val currentBalance = account.balance.toBigDecimalOrNull() ?: BigDecimal.ZERO

        val balanceAfterRevert = if (oldTransactionWithCategory.category.isIncome) {
            currentBalance.subtract(oldAmount)
        } else {
            currentBalance.add(oldAmount)
        }

        val finalBalance = if (newCategory.isIncome) {
            balanceAfterRevert.add(newAmount)
        } else {
            balanceAfterRevert.subtract(newAmount)
        }

        accountLocalDataSource.upsertAccount(
            account.copy(
                balance = finalBalance.toPlainString(),
                isDirty = true
            )
        )

        val transaction = Transaction(
            id = id,
            category = newCategory.toDomainModel(),
            amount = amount,
            currency = account.currency,
            transactionDate = transactionDate,
            comment = comment
        )
        val entity = transaction.toEntity(isDirty = true).copy(accountId = account.id)
        localDataSource.upsertTransaction(entity)
        return@withTransaction getTransactionById(id).first()
    }


    override suspend fun deleteTransaction(id: Int): Result<Unit> = database.withTransaction {
        val transactionToDelete = localDataSource.getTransactionById(id).first()
        if (transactionToDelete == null) {
            return@withTransaction Result.Success(Unit)
        }
        val entity = transactionToDelete.transaction
        val account = accountLocalDataSource.getAccountByIdSync(entity.accountId)
            ?: return@withTransaction Result.Error(
                shmr.budgetly.domain.util.DomainError.Unknown(
                    Exception("Account not found")
                )
            )

        val amountDecimal = entity.amount.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val currentBalance = account.balance.toBigDecimalOrNull() ?: BigDecimal.ZERO

        val newBalance = if (transactionToDelete.category.isIncome) {
            currentBalance.subtract(amountDecimal)
        } else {
            currentBalance.add(amountDecimal)
        }

        accountLocalDataSource.upsertAccount(
            account.copy(
                balance = newBalance.toPlainString(),
                isDirty = true
            )
        )

        if (id < 0) {
            localDataSource.deleteById(id)
        } else {
            localDataSource.markAsDeleted(id, System.currentTimeMillis())
        }
        return@withTransaction Result.Success(Unit)
    }
}