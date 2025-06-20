package shmr.budgetly.domain.repository

import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.entity.Category
import shmr.budgetly.domain.entity.Transaction

interface BudgetlyRepository {
    fun getExpenseTransactions(): List<Transaction>
    fun getIncomeTransactions(): List<Transaction>
    fun getMainAccount(): Account
    fun getAllCategories(): List<Category>
}