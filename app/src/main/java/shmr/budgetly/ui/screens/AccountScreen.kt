package shmr.budgetly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import shmr.budgetly.R
import shmr.budgetly.data.MockData
import shmr.budgetly.ui.components.BaseListItem
import shmr.budgetly.ui.components.EmojiIcon

@Preview
@Composable
fun AccountScreen(modifier: Modifier = Modifier) {
    val account = MockData.mainAccount
    val balanceCategory = MockData.balanceCategory

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        item {
            BaseListItem(
                lead = {
                    EmojiIcon(
                        balanceCategory.emoji,
                        backgroundColor = MaterialTheme.colorScheme.background
                    )
                },
                title = balanceCategory.name,
                titleTextStyle = MaterialTheme.typography.bodyLarge,
                backgroundColor = MaterialTheme.colorScheme.secondary,
                trail = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${account.balance} ${account.currency}",
                            style = MaterialTheme.typography.bodyLarge,

                            )
                        Spacer(Modifier.width(16.dp))
                        Icon(
                            painterResource(R.drawable.ic_list_item_trail_arrow),
                            contentDescription = (stringResource(R.string.account_ic_arrow_description))
                        )
                    }
                },
                onClick = { }
            )
        }

        item {
            BaseListItem(
                title = stringResource(R.string.account_currency_title),
                titleTextStyle = MaterialTheme.typography.bodyLarge,
                backgroundColor = MaterialTheme.colorScheme.secondary,
                trail = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = account.currency,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.width(16.dp))
                        Icon(
                            painterResource(R.drawable.ic_list_item_trail_arrow),
                            contentDescription = (stringResource(R.string.account_ic_arrow_description))
                        )
                    }
                },
                onClick = { }
            )
        }
    }
}