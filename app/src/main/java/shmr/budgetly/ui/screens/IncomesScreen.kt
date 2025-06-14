package shmr.budgetly.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import shmr.budgetly.ui.components.TotalHeader

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun IncomesScreen() {
    val transactions = MockData.incomeTransactions
    val totalAmount = "600 000 ₽"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        stickyHeader {
            TotalHeader(totalAmount = totalAmount, textStyle = MaterialTheme.typography.bodyLarge)
        }

        items(
            items = transactions,
            key = { it.id }
        ) { transaction ->
            BaseListItem(
                title = transaction.category.name,
                titleTextStyle = MaterialTheme.typography.bodyLarge,
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