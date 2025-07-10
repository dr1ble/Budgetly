package shmr.budgetly.ui.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import shmr.budgetly.R
import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.util.DomainError
import shmr.budgetly.ui.components.BaseListItem
import shmr.budgetly.ui.components.EmojiIcon
import shmr.budgetly.ui.components.ErrorState
import shmr.budgetly.ui.navigation.ACCOUNT_UPDATED_RESULT_KEY
import shmr.budgetly.ui.theme.dimens
import shmr.budgetly.ui.util.formatCurrencySymbol

private object AccountScreenDefaults {
    val balanceCategoryEmoji = "💰"
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AccountViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    LaunchedEffect(navBackStackEntry) {
        if (navBackStackEntry?.savedStateHandle?.remove<Boolean>(ACCOUNT_UPDATED_RESULT_KEY) == true) {
            viewModel.loadAccount(isInitialLoad = true)
        }
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }

            uiState.error != null -> {
                val currentError = uiState.error!! // Безопасное утверждение non-null, так как мы уже проверили
                val errorMessage = when (currentError) {
                    DomainError.NoInternet -> stringResource(R.string.error_no_internet)
                    DomainError.ServerError -> stringResource(R.string.error_server)
                    is DomainError.Unknown -> stringResource(R.string.error_unknown)
                }
                ErrorState(
                    message = errorMessage,
                    onRetry = { viewModel.loadAccount() }
                )
            }


            uiState.account != null -> {
                AccountContent(account = uiState.account!!)
            }
        }
    }
}

@Composable
private fun AccountContent(account: Account) {

    val currencySymbol = formatCurrencySymbol(account.currency)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            BaseListItem(
                lead = {
                    EmojiIcon(
                        emoji = AccountScreenDefaults.balanceCategoryEmoji,
                        backgroundColor = MaterialTheme.colorScheme.background
                    )
                },
                defaultHeight = MaterialTheme.dimens.heights.small,
                title = stringResource(id = R.string.account_balance_title),
                titleTextStyle = MaterialTheme.typography.bodyLarge,
                backgroundColor = MaterialTheme.colorScheme.secondary,
                trail = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${account.balance} $currencySymbol",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Spacer(Modifier.width(16.dp))

                    }
                }
            )
        }
        item {
            BaseListItem(
                title = stringResource(R.string.account_currency_title),
                titleTextStyle = MaterialTheme.typography.bodyLarge,
                backgroundColor = MaterialTheme.colorScheme.secondary,
                showDivider = false,
                defaultHeight = MaterialTheme.dimens.heights.small,
                trail = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currencySymbol,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.width(16.dp))
                    }
                }
            )
        }
    }
}