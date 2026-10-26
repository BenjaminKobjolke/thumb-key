package com.dessalines.thumbkey.ui.components.settings.backupandrestore

import android.content.Context
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ResetTv
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dessalines.thumbkey.R
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@Composable
internal fun BackupAndRestoreOptions(
    onBackup: () -> Unit,
    onRestore: () -> Unit,
    onReset: () -> Unit,
) {
    ProvidePreferenceTheme {
        Preference(
            title = { Text(stringResource(R.string.backup_database)) },
            icon = { Icon(Icons.Outlined.Save, contentDescription = null) },
            onClick = onBackup,
        )
        Preference(
            title = { Text(stringResource(R.string.restore_database)) },
            summary = { Text(stringResource(R.string.restore_database_warning)) },
            icon = { Icon(Icons.Outlined.Restore, contentDescription = null) },
            onClick = onRestore,
        )
        Preference(
            title = { Text(stringResource(R.string.reset_to_defaults)) },
            icon = { Icon(Icons.Outlined.ResetTv, contentDescription = null) },
            onClick = onReset,
        )
    }
}

@Composable
internal fun ResetConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.reset_to_defaults)) },
        text = { Text(stringResource(R.string.reset_to_defaults_msg)) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(R.string.reset_to_defaults_confirm))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}

internal fun showBackupAndRestoreToast(
    context: Context,
    message: String,
    duration: Int = Toast.LENGTH_LONG,
) {
    Toast.makeText(context, message, duration).show()
}
