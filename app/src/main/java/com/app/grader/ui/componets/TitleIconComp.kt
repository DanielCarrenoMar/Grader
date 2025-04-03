package com.app.grader.ui.componets

import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.grader.ui.theme.IconSmall

@Composable
fun TitleIcon(
    modifier: Modifier = Modifier,
    iconName: String,
    iconId: Int,
    size: Dp = IconSmall,
    backgroundColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.secondary,
    text: @Composable ()->Unit)
{
    Row (
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .padding(end = 12.dp)
                .size(26.dp)
                .background(
                    color = backgroundColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconId),
                contentDescription = iconName,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surface),
                modifier = Modifier.size(size)
            )
        }
        text()
    }
}