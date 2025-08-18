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
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.grader.R
import com.app.grader.ui.theme.IconLarge
import com.app.grader.ui.theme.IconMedium
import kotlinx.coroutines.launch

@Composable
private fun MyNavigationDrawerItem(title:String, iconId:Int, onClick:(()->Unit)?){
    NavigationDrawerItem(
        label = {
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 5.dp)
            )
        },
        icon = {
            Image(
                painter = painterResource(id = iconId),
                contentDescription = title,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                modifier = Modifier.size(IconLarge),
            )
        },
        selected = onClick == null,
        onClick = { onClick?.invoke() },
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderMenu(
    title: String,
    navigateHome: (() -> Unit)?,
    navigateAllGrades: (() -> Unit)?,
    navigateRecord: (() -> Unit)?,
    navigateConfig: (() -> Unit)?,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(Unit) {
        scope.launch {
            drawerState.close()
        }
    }

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
                    MyNavigationDrawerItem(
                        title = "Asignaturas",
                        iconId = R.drawable.home_outline,
                        onClick = navigateHome
                    )
                    MyNavigationDrawerItem(
                        title = "Todas las calificaciones",
                        iconId = R.drawable.star_outline,
                        onClick = navigateAllGrades
                    )
                    /*MyNavigationDrawerItem(
                        title = "Hist. Académico",
                        iconId = R.drawable.book_outline,
                        onClick = navigateRecord
                    )*/
                    MyNavigationDrawerItem(
                        title = "Ajustes",
                        iconId = R.drawable.cog_outline,
                        onClick = navigateConfig
                    )
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    colors = TopAppBarColors(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.onBackground,
                        MaterialTheme.colorScheme.primary,
                    ),
                    navigationIcon = {
                        IconButton(
                            modifier = Modifier.padding(start = 8.dp, end = 15.dp),
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
                                modifier = Modifier.size(IconLarge),
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