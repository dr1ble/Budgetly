package shmr.budgetly.domain.repository

import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.entity.Category
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.util.Result

interface BudgetlyRepository {
    suspend fun getExpenseTransactions(): Result<List<Transaction>>
    suspend fun getIncomeTransactions(): Result<List<Transaction>>
    fun getMainAccount(): Account
    fun getAllCategories(): List<Category>
    suspend fun getHistory(
        accountId: Int,
        startDate: String,
        endDate: String
    ): Result<List<Transaction>>
}