package com.app.grader.ui.componets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.grader.R

data class MenuAction(
    val label: String,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderBack(
    text: @Composable () -> Unit,
    snackbarHost: @Composable () -> Unit = {},
    navigateBack: () -> Unit,
    actions: List<MenuAction> = emptyList(),
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = snackbarHost,
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = text,
                colors = TopAppBarColors(
                    MaterialTheme.colorScheme.background,
                    MaterialTheme.colorScheme.surfaceVariant,
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.onBackground,
                    MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    if (actions.isNotEmpty()) {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(48.dp).padding(end = 15.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.dots_vertical_outline),
                                contentDescription = "edit",
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            actions.forEach { action ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = action.label,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    onClick = {
                                        action.onClick()
                                        menuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.padding(start = 8.dp),
                        onClick = {
                            navigateBack()
                        }) {
                        Image(
                            painter = painterResource(id = R.drawable.arrow_left_outline),
                            contentDescription = "Back",
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .size(48.dp)
                                .padding(end = 15.dp),
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}