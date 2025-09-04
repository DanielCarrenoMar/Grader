package com.app.grader.ui.componets.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CardContainer(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    cardColors: CardColors = CardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onBackground,
        disabledContainerColor = MaterialTheme.colorScheme.error,
        disabledContentColor = MaterialTheme.colorScheme.error,
    ),
    content: @Composable (PaddingValues) -> Unit,
){
    if (onClick != null){
        Card(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth(),
            colors = cardColors,
            shape = MaterialTheme.shapes.large
        ) {
            content(PaddingValues(18.dp))
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.large),
        ) {
            content(PaddingValues(18.dp))
        }
    }

}