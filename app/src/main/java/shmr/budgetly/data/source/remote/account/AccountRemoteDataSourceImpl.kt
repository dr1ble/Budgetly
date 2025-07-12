package shmr.budgetly.data.source.remote.account

import shmr.budgetly.data.network.ApiService
import shmr.budgetly.data.network.dto.AccountDto
import shmr.budgetly.data.network.dto.AccountResponseDto
import shmr.budgetly.data.network.dto.UpdateAccountRequestDto
import shmr.budgetly.di.scope.AppScope
import javax.inject.Inject

@AppScope
class AccountRemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : AccountRemoteDataSource {
    override suspend fun getAccountById(id: Int): AccountResponseDto = apiService.getAccountById(id)
    override suspend fun getAccounts(): List<AccountDto> = apiService.getAccounts()
    override suspend fun updateAccount(id: Int, request: UpdateAccountRequestDto): AccountDto =
        apiService.updateAccount(id, request)
}