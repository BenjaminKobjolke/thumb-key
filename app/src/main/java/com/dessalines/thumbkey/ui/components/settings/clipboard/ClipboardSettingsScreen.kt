package com.dessalines.thumbkey.ui.components.settings.clipboard

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.db.AppSettings
import com.dessalines.thumbkey.db.AppSettingsViewModel
import com.dessalines.thumbkey.db.ClipboardRepository
import com.dessalines.thumbkey.db.ClipboardSettingsUpdate
import com.dessalines.thumbkey.db.DEFAULT_CLIPBOARD_AUTO_CLEANUP_ENABLED
import com.dessalines.thumbkey.db.DEFAULT_CLIPBOARD_CLEANUP_AFTER_MINUTES
import com.dessalines.thumbkey.db.DEFAULT_CLIPBOARD_HISTORY_ENABLED
import com.dessalines.thumbkey.db.DEFAULT_CLIPBOARD_MAX_SIZE
import com.dessalines.thumbkey.db.DEFAULT_CLIPBOARD_SIZE_LIMIT_ENABLED
import com.dessalines.thumbkey.db.DEFAULT_USE_PRIVATE_CLIPBOARD
import com.dessalines.thumbkey.utils.SimpleTopAppBar
import com.dessalines.thumbkey.utils.TAG
import com.dessalines.thumbkey.utils.toBool
import com.dessalines.thumbkey.utils.toInt
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.ProvidePreferenceTheme
import me.zhanghai.compose.preference.SwitchPreference

enum class CleanupDuration(
    val minutes: Int,
    val displayNameResId: Int,
) {
    MINUTES_5(5, R.string.duration_5_minutes),
    MINUTES_10(10, R.string.duration_10_minutes),
    MINUTES_30(30, R.string.duration_30_minutes),
    HOURS_1(60, R.string.duration_1_hour),
    HOURS_2(2 * 60, R.string.duration_2_hours),
    HOURS_4(4 * 60, R.string.duration_4_hours),
    HOURS_12(12 * 60, R.string.duration_12_hours),
    DAYS_1(24 * 60, R.string.duration_1_day),
    DAYS_2(2 * 24 * 60, R.string.duration_2_days),
    DAYS_4(4 * 24 * 60, R.string.duration_4_days),
    DAYS_7(7 * 24 * 60, R.string.duration_7_days),
    ;

    companion object {
        fun fromIndex(index: Int): CleanupDuration = entries.getOrElse(index) { HOURS_2 }

        fun fromMinutes(minutes: Int): CleanupDuration = entries.find { it.minutes == minutes } ?: HOURS_2
    }
}

internal class ClipboardSettingsForm(
    settings: AppSettings?,
) {
    var clipboardHistoryEnabledState =
        (settings?.clipboardHistoryEnabled ?: DEFAULT_CLIPBOARD_HISTORY_ENABLED).toBool()
    var clipboardAutoCleanupEnabledState =
        (settings?.clipboardAutoCleanupEnabled ?: DEFAULT_CLIPBOARD_AUTO_CLEANUP_ENABLED).toBool()
    var clipboardCleanupDuration =
        CleanupDuration.fromMinutes(
            settings?.clipboardCleanupAfterMinutes
                ?: DEFAULT_CLIPBOARD_CLEANUP_AFTER_MINUTES,
        )
    var clipboardSizeLimitEnabledState =
        (settings?.clipboardSizeLimitEnabled ?: DEFAULT_CLIPBOARD_SIZE_LIMIT_ENABLED).toBool()

    // Float is required because SliderPreference uses float values for the slider position
    var clipboardMaxSizeState =
        (settings?.clipboardMaxSize ?: DEFAULT_CLIPBOARD_MAX_SIZE).toFloat()
    var usePrivateClipboardState =
        (settings?.usePrivateClipboard ?: DEFAULT_USE_PRIVATE_CLIPBOARD).toBool()

    fun toUpdate(): ClipboardSettingsUpdate =
        ClipboardSettingsUpdate(
            id = 1,
            clipboardHistoryEnabled = clipboardHistoryEnabledState.toInt(),
            clipboardAutoCleanupEnabled = clipboardAutoCleanupEnabledState.toInt(),
            clipboardCleanupAfterMinutes = clipboardCleanupDuration.minutes,
            clipboardSizeLimitEnabled = clipboardSizeLimitEnabledState.toInt(),
            clipboardMaxSize = clipboardMaxSizeState.toInt(),
            usePrivateClipboard = usePrivateClipboardState.toInt(),
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClipboardSettingsScreen(
    navController: NavController,
    appSettingsViewModel: AppSettingsViewModel,
    clipboardRepository: ClipboardRepository?,
) {
    Log.d(TAG, "Got to clipboard settings activity")

    val settings by appSettingsViewModel.appSettings.observeAsState()
    val scope = rememberCoroutineScope()
    val form = ClipboardSettingsForm(settings)

    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    fun updateClipboardSettings() {
        appSettingsViewModel.updateClipboardSettings(form.toUpdate())
        // Enforce size limit after updating settings
        scope.launch {
            clipboardRepository?.enforceSizeLimit()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopAppBar(
                text = stringResource(R.string.clipboard_history),
                navController = navController,
            )
        },
        content = { padding ->
            Column(
                modifier =
                    Modifier
                        .padding(padding)
                        .verticalScroll(scrollState)
                        .background(color = MaterialTheme.colorScheme.surface)
                        .imePadding(),
            ) {
                ProvidePreferenceTheme {
                    ClipboardSettingsItems(form, ::updateClipboardSettings)
                }
            }
        },
    )
}

@Composable
private fun ClipboardSettingsItems(
    form: ClipboardSettingsForm,
    onChange: () -> Unit,
) {
    ClipboardHistoryEnabledItem(form, onChange)
    ClipboardCleanupItems(form, onChange)
    ClipboardSizeLimitItems(form, onChange)
    PrivateClipboardItem(form, onChange)
}

@Composable
private fun ClipboardHistoryEnabledItem(
    form: ClipboardSettingsForm,
    onChange: () -> Unit,
) {
    SwitchPreference(
        value = form.clipboardHistoryEnabledState,
        onValueChange = {
            form.clipboardHistoryEnabledState = it
            onChange()
        },
        title = { Text(stringResource(R.string.clipboard_history_enabled)) },
        summary = { Text(stringResource(R.string.clipboard_history_enabled_description)) },
        icon = { Icon(imageVector = Icons.Outlined.ContentPaste, contentDescription = null) },
    )
}

@Composable
private fun PrivateClipboardItem(
    form: ClipboardSettingsForm,
    onChange: () -> Unit,
) {
    SwitchPreference(
        value = form.usePrivateClipboardState,
        onValueChange = {
            form.usePrivateClipboardState = it
            onChange()
        },
        enabled = form.clipboardHistoryEnabledState,
        title = { Text(stringResource(R.string.use_private_clipboard)) },
        summary = { Text(stringResource(R.string.use_private_clipboard_description)) },
        icon = { Icon(imageVector = Icons.Outlined.VisibilityOff, contentDescription = null) },
    )
}
