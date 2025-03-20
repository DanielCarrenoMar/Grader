package com.app.grader.ui.componets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.app.grader.R

data class AddMenuCompItem(
    val title : String,
    val iconId: Int,
    val navFun: () -> Unit
)

@Composable
fun AddMenuComp(items: List<AddMenuCompItem>) {
    var expanded by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(
        targetValue = if (expanded) Color.White.copy(0.9f) else Color.White.copy(0.0f),
    )

    Box(
        modifier = Modifier
            .zIndex(1f)
            .fillMaxSize()
            .background(color = backgroundColor),
        contentAlignment = Alignment.BottomEnd
    ){

    }
    Box(
        modifier = Modifier
            .zIndex(2f)
            .padding(bottom = 68.dp, end = 16.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ){
        IconButton (
            onClick = {
                if (items.size == 1) {
                    items[0].navFun()
                } else expanded = true
              },
            modifier = Modifier
                .size(60.dp)
                .shadow(8.dp, shape = MaterialTheme.shapes.large)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.large
                )
                .alpha(0.85f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.plus_outline),
                contentDescription = "add",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surface),
                modifier = Modifier.size(25.dp)
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = Color.Transparent,
                shadowElevation = 0.dp,
            ) {
                for (item in items) {
                    DropdownMenuItem(
                        onClick = item.navFun,
                        text = {
                            Text(item.title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onBackground)
                        },
                        trailingIcon = {
                            Box(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.background,
                                        MaterialTheme.shapes.medium
                                    )
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = item.iconId),
                                    contentDescription = item.title,
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}