package com.dessalines.thumbkey.ui.components.settings.summera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.navigation.NavController
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.summera.SummeraAccount
import com.dessalines.thumbkey.utils.SimpleTopAppBar
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceTheme
import me.zhanghai.compose.preference.SwitchPreference

/** The debug switch, the recorded Summera calls and the last crash, with one button to clear both. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummeraLogScreen(navController: NavController) {
    val ctx = LocalContext.current
    val scrollState = rememberScrollState()

    var enabled by remember { mutableStateOf(SummeraAccount.debugLoggingEnabled(ctx)) }
    var log by remember { mutableStateOf(SummeraAccount.debugLog(ctx)) }
    var lastCrash by remember { mutableStateOf(SummeraAccount.lastCrash(ctx)) }

    // ponytail: not live, read once per resume; a call that ends while this screen is open shows up
    // on the next visit
    LifecycleResumeEffect(Unit) {
        log = SummeraAccount.debugLog(ctx)
        lastCrash = SummeraAccount.lastCrash(ctx)
        onPauseOrDispose { }
    }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                text = stringResource(R.string.summera_debug_log),
                navController = navController,
            )
        },
        content = { padding ->
            Column(
                modifier =
                    Modifier
                        .padding(padding)
                        .verticalScroll(scrollState)
                        .background(color = MaterialTheme.colorScheme.surface),
            ) {
                ProvidePreferenceTheme {
                    SwitchPreference(
                        value = enabled,
                        onValueChange = {
                            enabled = it
                            SummeraAccount.setDebugLogging(ctx, it)
                        },
                        title = { Text(stringResource(R.string.summera_debug_logging)) },
                        summary = { Text(stringResource(R.string.summera_debug_logging_summary)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.BugReport,
                                contentDescription = null,
                            )
                        },
                    )
                    Preference(
                        title = { Text(stringResource(R.string.summera_debug_log)) },
                        summary = { Text(log ?: stringResource(R.string.summera_log_empty)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Description,
                                contentDescription = null,
                            )
                        },
                    )
                    val crash = lastCrash
                    if (crash != null) {
                        Preference(
                            title = { Text(stringResource(R.string.summera_last_crash)) },
                            summary = { Text(crash) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.Warning,
                                    contentDescription = null,
                                )
                            },
                        )
                    }
                    Preference(
                        title = { Text(stringResource(R.string.summera_clear)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            SummeraAccount.clearDebugLog(ctx)
                            SummeraAccount.clearLastCrash(ctx)
                            log = null
                            lastCrash = null
                        },
                    )
                }
            }
        },
    )
}
