package com.dessalines.thumbkey.ui.components.settings

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.db.AppSettingsViewModel
import com.dessalines.thumbkey.db.DEFAULT_KEYBOARD_LAYOUT
import com.dessalines.thumbkey.ui.components.common.TestOutTextField
import com.dessalines.thumbkey.ui.components.settings.about.SettingsDivider
import com.dessalines.thumbkey.utils.TAG
import com.dessalines.thumbkey.utils.keyboardLayoutsSetFromDbIndexString
import com.dessalines.thumbkey.utils.updateLayouts
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    appSettingsViewModel: AppSettingsViewModel,
    thumbkeyEnabled: Boolean,
    thumbkeySelected: Boolean,
) {
    Log.d(TAG, "Got to settings activity")

    val snackbarHostState = remember { SnackbarHostState() }
    val settings by appSettingsViewModel.appSettings.observeAsState()

    val scrollState = rememberScrollState()

    val layoutsState = keyboardLayoutsSetFromDbIndexString(settings?.keyboardLayouts)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
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
                    SettingsFeatureItems(
                        SettingsFeatureState(
                            navController = navController,
                            thumbkeyEnabled = thumbkeyEnabled,
                            thumbkeySelected = thumbkeySelected,
                            layoutsState = layoutsState,
                            onLayoutsChange = { layouts ->
                                val selectedLayouts =
                                    layouts.ifEmpty {
                                        keyboardLayoutsSetFromDbIndexString(DEFAULT_KEYBOARD_LAYOUT.toString())
                                    }
                                updateLayouts(appSettingsViewModel, selectedLayouts)
                            },
                        ),
                    )
                    SettingsAppItems(navController)
                    SettingsDivider()
                    TestOutTextField()
                }
            }
        },
    )
}
