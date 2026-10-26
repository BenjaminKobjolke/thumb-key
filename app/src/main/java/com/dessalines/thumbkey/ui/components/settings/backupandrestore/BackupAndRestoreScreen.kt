package com.dessalines.thumbkey.ui.components.settings.backupandrestore

import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.db.AppDB
import com.dessalines.thumbkey.db.AppSettings
import com.dessalines.thumbkey.db.AppSettingsViewModel
import com.dessalines.thumbkey.db.DEFAULT_KEYBOARD_LAYOUT
import com.dessalines.thumbkey.utils.SimpleTopAppBar
import com.dessalines.thumbkey.utils.keyboardLayoutsSetFromDbIndexString
import com.dessalines.thumbkey.utils.updateLayouts
import com.roomdbexportimport.RoomDBExportImport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupAndRestoreScreen(
    navController: NavController,
    appSettingsViewModel: AppSettingsViewModel,
) {
    Log.d("thumb key", "Got to Backup and Restore screen")

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showConfirmResetDialog by remember { mutableStateOf(false) }

    val dbHelper = RoomDBExportImport(AppDB.getDatabase(ctx).openHelper)

    val backedUpMsg = stringResource(R.string.database_backed_up)
    val operationFailedTemplate = stringResource(R.string.database_operation_failed)

    val exportDbLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("application/zip"),
        ) {
            it?.also { uri ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    scope.launch {
                        withContext(Dispatchers.IO) { dbHelper.export(ctx, uri) }
                            .onSuccess {
                                showBackupAndRestoreToast(ctx, backedUpMsg, android.widget.Toast.LENGTH_SHORT)
                            }.onFailure {
                                showBackupAndRestoreToast(
                                    ctx,
                                    operationFailedTemplate.format(it.message ?: it.stackTraceToString()),
                                )
                            }
                    }
                }
            }
        }

    val importDbLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument(),
        ) {
            it?.also { uri ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    scope.launch {
                        withContext(Dispatchers.IO) { dbHelper.import(ctx, uri, true) }
                            .onFailure {
                                showBackupAndRestoreToast(
                                    ctx,
                                    operationFailedTemplate.format(it.message ?: it.stackTraceToString()),
                                )
                            }
                    }
                }
            }
        }

    if (showConfirmResetDialog) {
        ResetConfirmationDialog(
            onDismiss = {
                showConfirmResetDialog = false
            },
            onConfirm = {
                showConfirmResetDialog = false
                resetAppSettingsToDefault(appSettingsViewModel)
            },
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopAppBar(text = stringResource(R.string.backup_and_restore), navController = navController)
        },
        content = { padding ->
            Column(
                modifier =
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(padding),
            ) {
                BackupAndRestoreOptions(
                    onBackup = {
                        val filename =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                "${LocalDate.now()}-thumb-key.zip"
                            } else {
                                "thumb-key.zip"
                            }
                        exportDbLauncher.launch(filename)
                    },
                    onRestore = { importDbLauncher.launch(arrayOf("application/zip")) },
                    onReset = { showConfirmResetDialog = true },
                )
            }
        },
    )
}

private fun resetAppSettingsToDefault(appSettingsViewModel: AppSettingsViewModel) {
    val layoutsDefault = keyboardLayoutsSetFromDbIndexString(DEFAULT_KEYBOARD_LAYOUT.toString())
    updateLayouts(appSettingsViewModel, layoutsDefault)

    appSettingsViewModel.update(
        AppSettings(
            id = 1,
            lastVersionCodeViewed = appSettingsViewModel.appSettings.value?.lastVersionCodeViewed ?: 0,
            viewedChangelog = appSettingsViewModel.appSettings.value?.viewedChangelog ?: 1,
        ),
    )
}
