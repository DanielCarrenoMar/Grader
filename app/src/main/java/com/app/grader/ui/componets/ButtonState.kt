package com.app.grader.ui.componets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Reusable submit button with built-in loading state.
 *
 * When [isLoading] is true the button is disabled and shows a small
 * progress indicator together with [loadingText] (falls back to [text]).
 * Clicks received while loading are ignored.
*/
@Composable
fun ButtonState(
    text: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loadingText: String? = null,
    onClick: () -> Unit,
) {
    val effectiveEnabled = enabled && !isLoading
    val announcement = if (isLoading) loadingText ?: text else text
    Button(
        modifier = modifier.semantics {
            contentDescription = announcement
        },
        enabled = effectiveEnabled,
        onClick = {
            if (isLoading) return@Button
            onClick()
        }
    ) {
        if (isLoading) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(20.dp)
                        .semantics {
                            contentDescription = announcement
                        },
                    strokeWidth = 2.dp,
                )
                Text(
                    text = announcement,
                    maxLines = 1,
                )
            }
        } else {
            Text(
                text = text,
                maxLines = 1,
            )
        }
    }
}
