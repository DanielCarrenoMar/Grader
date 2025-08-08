package com.app.grader.ui.componets.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.grader.ui.theme.Error500
import com.app.grader.ui.theme.IconLarge

@Composable
fun IconCardButton(
    onClick: () -> Unit,
    contentColor:Color,
    iconColor:Color = contentColor,
    icon: Int,
    text: String
) {
    Card (
        onClick = onClick,
        colors = CardColors(
            containerColor = Color.Transparent,
            contentColor = contentColor,
            disabledContainerColor = Error500,
            disabledContentColor = Error500
        )
    ){
        Row (
            modifier = Modifier
                .padding(vertical = 15.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(id = icon),
                contentDescription = text,
                colorFilter = ColorFilter.tint(iconColor),
                modifier = Modifier
                    .size(IconLarge),
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 16.dp),
                fontWeight = FontWeight.Medium
            )
        }
    }
}
