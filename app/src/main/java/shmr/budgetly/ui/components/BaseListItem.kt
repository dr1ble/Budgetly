package shmr.budgetly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import shmr.budgetly.ui.theme.dimens

@Composable
fun BaseListItem(
    title: String,
    titleTextStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    defaultHeight: Dp = 70.dp,
    lead: @Composable (() -> Unit)? = null,
    trail: @Composable (() -> Unit)? = null,
    showDivider: Boolean = true,
    backgroundColor: Color = Color.Transparent,
    onClick: (() -> Unit)? = null
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(defaultHeight)
            .background(backgroundColor)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (lead != null) {
                lead()
                Spacer(modifier = Modifier.width(16.dp))
            }


            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = titleTextStyle
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (trail != null) {
                Spacer(modifier = Modifier.width(16.dp))
                trail()

            }
        }


        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.align(Alignment.BottomCenter),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

@Composable
fun ListItemTrailText(
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyLarge
) {
    Text(
        text = text,
        style = style
    )
}

@Composable
fun TotalHeader(totalAmount: String, textStyle: TextStyle = MaterialTheme.typography.bodyLarge) {
    BaseListItem(
        title = "Всего",
        titleTextStyle = textStyle,
        defaultHeight = MaterialTheme.dimens.heights.small,
        backgroundColor = MaterialTheme.colorScheme.secondary,
        showDivider = true,
        trail = {
            ListItemTrailText(
                text = totalAmount,
                style = textStyle
            )
        }
    )
}