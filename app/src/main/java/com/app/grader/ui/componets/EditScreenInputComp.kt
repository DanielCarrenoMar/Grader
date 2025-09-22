package com.app.grader.ui.componets

import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.app.grader.R
import com.app.grader.ui.theme.IconLarge

@OptIn(ExperimentalMaterial3Api::class)
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
    maxLines: Int = Int.MAX_VALUE,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 15.dp),
            verticalAlignment = Alignment.Top // Alinea los elementos al inicio (arriba)
        ) {
            Image(
                painter = painterResource(id = leadingIconId),
                contentDescription = placeHolderText,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                modifier = Modifier
                    .size(IconLarge)
            )
            Spacer(modifier = Modifier.size(10.dp))
            BasicTextField(
                value = value.take(maxLength),
                onValueChange = { if (it.length <= maxLength) onValueChange(it) },
                modifier = Modifier.weight(1f),
                enabled = enabled,
                keyboardOptions = keyboardOptions,
                textStyle = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = maxLines,
                singleLine = maxLines == 1,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = value,
                        innerTextField = innerTextField,
                        enabled = enabled,
                        singleLine = maxLines == 1,
                        visualTransformation = VisualTransformation.None,
                        interactionSource = interactionSource,
                        placeholder = {
                            Text(
                                placeHolderText,
                                modifier = Modifier.alpha(0.6f),
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        suffix = suffix,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        ),
                        contentPadding = TextFieldDefaults.contentPaddingWithoutLabel( // Ajusta el padding si es necesario
                            start = 0.dp, top = 0.dp, end = 0.dp, bottom = 0.dp
                        )
                    )
                }
            )
        }
        HorizontalDivider(modifier = Modifier.alpha(0.5f))
    }
}