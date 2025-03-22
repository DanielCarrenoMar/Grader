package com.app.grader.ui.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CardContainer(
    onClick: (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit,
){
    if (onClick != null){
        Card(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            colors = CardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onBackground,
                disabledContainerColor = MaterialTheme.colorScheme.error,
                disabledContentColor = MaterialTheme.colorScheme.error,
            ),
            shape = MaterialTheme.shapes.large
        ) {
            content(PaddingValues(18.dp))
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.large),
        ) {
            content(PaddingValues(18.dp))
        }
    }

}