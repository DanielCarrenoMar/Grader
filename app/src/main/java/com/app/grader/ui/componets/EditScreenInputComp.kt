package com.app.grader.ui.componets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.app.grader.R
import com.app.grader.ui.theme.IconLarge

@Composable
fun EditScreenInputComp(
    placeHolderText: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIconId: Int,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    suffix: @Composable() (()->Unit)? = null
) {
    TextField(
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
        leadingIcon = {
            Image(
                painter = painterResource(id = leadingIconId),
                contentDescription = placeHolderText,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                modifier = Modifier
                    .size(IconLarge)
                    .padding(end = 5.dp),
            )
        },
        placeholder = {
            Text(placeHolderText, modifier = Modifier.alpha(0.4f))
        },
        keyboardOptions = keyboardOptions,
        value = value,
        onValueChange = onValueChange,
        suffix = suffix
    )
    HorizontalDivider( modifier = Modifier.alpha(0.5f))
}