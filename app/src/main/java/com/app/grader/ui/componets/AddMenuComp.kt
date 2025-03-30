package com.app.grader.ui.componets

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.app.grader.R
import com.app.grader.ui.theme.IconLarge
import com.app.grader.ui.theme.IconMedium
import com.app.grader.ui.theme.IconSmall

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
        animationSpec = tween(
            durationMillis = 150,
            easing = EaseIn
        )
    )

    if (expanded) {
        Box(
            modifier = Modifier
                .zIndex(1f)
                .fillMaxSize()
                .clickable(onClick = { expanded = false })
                .background(color = backgroundColor),
            contentAlignment = Alignment.BottomEnd
        ) {}
    }
    Box(
        modifier = Modifier
            .zIndex(2f)
            .padding(bottom = 68.dp, end = 16.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(horizontalAlignment = Alignment.End) {
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }) + expandVertically(),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it }) + shrinkVertically()
            ) {
                LazyColumn(Modifier.padding(bottom = 8.dp)) {
                    items(items) { item ->
                        ItemMenu(iconId = item.iconId, title = item.title, onClick = item.navFun)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            val transition = updateTransition(targetState = expanded, label = "transition")
            val rotation by transition.animateFloat(label = "rotation") {
                if (it) 315f else 0f
            }

            FloatingActionButton(
                onClick = {
                    if (items.size == 1) {
                        items[0].navFun()
                    } else expanded = !expanded
                          },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Image(
                    painter = painterResource(id = R.drawable.plus_outline),
                    contentDescription = "Agregar",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surface),
                    modifier = Modifier.size(25.dp).rotate(rotation)
                )
            }
        }
    }
}

@Composable
private fun ItemMenu(iconId: Int, title: String, onClick: () -> Unit) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(10.dp))
        FloatingActionButton(
            onClick = onClick,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.size(42.dp),
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Image(
                painter = painterResource(id = iconId),
                contentDescription = title,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                modifier = Modifier.size(IconMedium)
            )
        }
    }
}