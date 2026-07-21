package com.app.grader.ui.componets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.grader.ui.theme.Shadow50

@Composable
fun InfoAlertDialogComp(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    confirmText: String = "Entendido",
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, style = MaterialTheme.typography.titleMedium) },
        text = { Text(message, style = MaterialTheme.typography.labelMedium) },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Shadow50
                ),
            ) {
                Text(confirmText)
            }
        }
    )
}

