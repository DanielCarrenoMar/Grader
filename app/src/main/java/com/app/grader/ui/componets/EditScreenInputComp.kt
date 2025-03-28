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
    modifier: Modifier = Modifier,
    enabled: Boolean  = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    suffix: @Composable() (()->Unit)? = null,
    maxLength: Int = Int.MAX_VALUE,
) {
    TextField(
        enabled = enabled,
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        modifier = modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp),
        leadingIcon = {
            Image(
                painter = painterResource(id = leadingIconId),
                contentDescription = placeHolderText,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                modifier = Modifier
                    .size(IconLarge),
            )
        },
        placeholder = {
            Text(placeHolderText, modifier = Modifier.alpha(0.6f), style = MaterialTheme.typography.labelLarge)
        },
        keyboardOptions = keyboardOptions,
        value = value.take(maxLength),
        onValueChange = { if (it.length <= maxLength) onValueChange(it) },
        suffix = suffix,
        textStyle = MaterialTheme.typography.labelLarge
    )
    HorizontalDivider( modifier = Modifier.alpha(0.5f))
}