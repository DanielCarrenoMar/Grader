package com.app.grader.ui.componets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.app.grader.R

data class AddCompItem(
    val title : String,
    val navFun: () -> Unit
)

@Composable
fun AddComp(items: List<AddCompItem>) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(24.dp)
            .padding(bottom = 48.dp)
            .zIndex(1f)
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ){
        IconButton (
            onClick = { expanded = true },
            modifier = Modifier
                .size(60.dp)
                .shadow(6.dp, shape = RoundedCornerShape(24))
                .background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(24))
                .alpha(0.85f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.plus_outline),
                contentDescription = "add",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surface),
                modifier = Modifier.size(35.dp)
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                for (item in items) {
                    DropdownMenuItem(
                        onClick = item.navFun,
                        text = {
                            Text(item.title)
                        }
                    )
                }
            }
        }
    }
}