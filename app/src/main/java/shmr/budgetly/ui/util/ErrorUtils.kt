package shmr.budgetly.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import shmr.budgetly.R
import shmr.budgetly.domain.util.DomainError

/**
 * Преобразует доменную ошибку [DomainError] в локализованное сообщение для пользователя.
 * Эта функция должна вызываться внутри @Composable функции для доступа к ресурсам.
 *
 * @param error Доменная ошибка, которую нужно преобразовать.
 * @return Строка с сообщением об ошибке.
 */
@Composable
fun getErrorMessage(error: DomainError): String {
    return when (error) {
        DomainError.NoInternet -> stringResource(R.string.error_no_internet)
        DomainError.ServerError -> stringResource(R.string.error_server)
        is DomainError.Unknown -> stringResource(R.string.error_unknown)
    }
}