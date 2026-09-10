package com.app.grader.ui.componets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import com.app.grader.ui.theme.Error500
import com.app.grader.ui.theme.Shadow50

@Composable
fun DeleteConfirmationComp(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    description: String = "¿Realmente desea eliminar este elemento?",
    enabled: Boolean = true,
) {
    AlertDialog(
        onDismissRequest = {
            // Gate dismissal while deleting to avoid a trapped disabled dialog.
            if (enabled) onDismiss()
        },
        title = {
                    Text(
                        text = "Eliminar",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
        text = {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Error500,
                    contentColor = Shadow50
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (enabled) "Eliminar" else "Eliminando...")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Shadow50
                ),
            ) {
                Text("Cancelar")
            }
        },
        properties = DialogProperties(dismissOnBackPress = enabled, dismissOnClickOutside = enabled)
    )
}