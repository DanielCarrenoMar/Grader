package com.app.grader.ui.componets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.app.grader.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderMenu(
    title: String,
    navigateHome: (() -> Unit)?,
    navigateAllGrades: (() -> Unit)?,
    navigateConfig: (() -> Unit)?,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(12.dp))
                    Text("Grader", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary)
                    HorizontalDivider()
                    NavigationDrawerItem(
                        label = { Text("Asignaturas", style = MaterialTheme.typography.labelLarge) },
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.home_outline),
                                contentDescription = "Asignaturas",
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                                modifier = Modifier.size(32.dp).padding(end = 5.dp),
                            )
                        },
                        selected = navigateHome == null,
                        onClick = { navigateHome?.invoke() }
                    )
                    NavigationDrawerItem(
                        label = { Text("Todas las notas", style = MaterialTheme.typography.labelLarge) },
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.star_outline),
                                contentDescription = "Todas las notas",
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                                modifier = Modifier.size(32.dp).padding(end = 5.dp),
                            )
                        },
                        selected = navigateAllGrades == null,
                        onClick = { navigateAllGrades?.invoke() }
                    )
                    NavigationDrawerItem(
                        label = { Text("Ajustes", style = MaterialTheme.typography.labelLarge) },
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.cog_outline),
                                contentDescription = "Ajustes",
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                                modifier = Modifier.size(32.dp).padding(end = 5.dp),
                            )
                        },
                        selected = navigateConfig == null,
                        onClick = { navigateConfig?.invoke() }
                    )
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            modifier = Modifier.padding(start = 8.dp),
                            onClick = {
                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.bars_outline),
                                contentDescription = "Menu",
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                                modifier = Modifier.size(48.dp).padding(end = 15.dp),
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            content(innerPadding)
        }
    }
}