package shmr.budgetly.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import shmr.budgetly.R
import shmr.budgetly.data.MockData
import shmr.budgetly.ui.components.BaseListItem
import shmr.budgetly.ui.components.EmojiIcon
import shmr.budgetly.ui.components.TotalHeader
import shmr.budgetly.ui.theme.BudgetlyTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpensesScreen(modifier: Modifier = Modifier) {
    val transactions = MockData.expenseTransactions
    val totalAmount = "436 558 ₽"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Белый/серый фон для списка
    ) {

        stickyHeader {
            TotalHeader(totalAmount = totalAmount, textStyle = MaterialTheme.typography.bodyMedium)
        }

        items(
            items = transactions,
            key = { transaction -> transaction.id },
        ) { transaction ->
            BaseListItem(
                lead = {
                    EmojiIcon(
                        emoji = transaction.category.emoji,
                    )
                },
                title = transaction.category.name,
                subtitle = if (transaction.comment.isNotBlank()) transaction.comment else null,
                trail = {
                    Text(
                        text = transaction.amount,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        painterResource(R.drawable.ic_list_item_trail_arrow),
                        contentDescription = ("")
                    )
                },
                onClick = { }
            )
        }
    }
}


@Preview
@Composable
fun ExpensesScreenPreview() {
    BudgetlyTheme {
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            ExpensesScreen()
        }
    }
}