package shmr.budgetly.data.repository

import shmr.budgetly.data.MockData
import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.entity.Category
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.repository.BudgetlyRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetlyRepositoryImpl @Inject constructor(

) : BudgetlyRepository {

    override fun getExpenseTransactions(): List<Transaction> {
        return MockData.expenseTransactions
    }

    override fun getIncomeTransactions(): List<Transaction> {
        return MockData.incomeTransactions
    }

    override fun getMainAccount(): Account {
        return MockData.mainAccount
    }

    override fun getAllCategories(): List<Category> {
        return MockData.allCategories
    }

}