package shmr.budgetly.data.source.remote.account

import shmr.budgetly.data.network.dto.AccountDto
import shmr.budgetly.data.network.dto.AccountResponseDto
import shmr.budgetly.data.network.dto.UpdateAccountRequestDto

interface AccountRemoteDataSource {
    suspend fun getAccountById(id: Int): AccountResponseDto
    suspend fun getAccounts(): List<AccountDto>
    suspend fun updateAccount(id: Int, request: UpdateAccountRequestDto): AccountDto
}