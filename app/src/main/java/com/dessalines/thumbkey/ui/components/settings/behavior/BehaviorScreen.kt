package com.dessalines.thumbkey.ui.components.settings.behavior

import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BorderInner
import androidx.compose.material.icons.outlined.Keyboard
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.navigation.NavController
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.db.AppSettings
import com.dessalines.thumbkey.db.AppSettingsViewModel
import com.dessalines.thumbkey.db.BehaviorUpdate
import com.dessalines.thumbkey.db.DEFAULT_AUTO_CAPITALIZE
import com.dessalines.thumbkey.db.DEFAULT_CIRCULAR_DRAG_ENABLED
import com.dessalines.thumbkey.db.DEFAULT_CLOCKWISE_DRAG_ACTION
import com.dessalines.thumbkey.db.DEFAULT_COUNTERCLOCKWISE_DRAG_ACTION
import com.dessalines.thumbkey.db.DEFAULT_DRAG_RETURN_ENABLED
import com.dessalines.thumbkey.db.DEFAULT_GHOST_KEYS_ENABLED
import com.dessalines.thumbkey.db.DEFAULT_MIN_SWIPE_LENGTH
import com.dessalines.thumbkey.db.DEFAULT_SLIDE_BACKSPACE_DEADZONE_ENABLED
import com.dessalines.thumbkey.db.DEFAULT_SLIDE_CURSOR_MOVEMENT_MODE
import com.dessalines.thumbkey.db.DEFAULT_SLIDE_ENABLED
import com.dessalines.thumbkey.db.DEFAULT_SLIDE_HOLD_ENABLED
import com.dessalines.thumbkey.db.DEFAULT_SLIDE_SENSITIVITY
import com.dessalines.thumbkey.db.DEFAULT_SLIDE_SPACEBAR_DEADZONE_ENABLED
import com.dessalines.thumbkey.db.DEFAULT_SPACEBAR_MULTITAPS
import com.dessalines.thumbkey.db.DEFAULT_SWITCH_IME_MODE
import com.dessalines.thumbkey.db.DEFAULT_SWITCH_TO_LETTERS_AFTER_SPACE
import com.dessalines.thumbkey.ui.components.common.TestOutTextField
import com.dessalines.thumbkey.ui.components.settings.about.SettingsDivider
import com.dessalines.thumbkey.utils.CircularDragAction
import com.dessalines.thumbkey.utils.CursorAccelerationMode
import com.dessalines.thumbkey.utils.ImeSwitchMode
import com.dessalines.thumbkey.utils.SimpleTopAppBar
import com.dessalines.thumbkey.utils.TAG
import com.dessalines.thumbkey.utils.toBool
import com.dessalines.thumbkey.utils.toInt
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.ProvidePreferenceTheme
import me.zhanghai.compose.preference.SwitchPreference

internal class BehaviorForm(
    settings: AppSettings?,
) {
    var minSwipeLengthState = (settings?.minSwipeLength ?: DEFAULT_MIN_SWIPE_LENGTH).toFloat()
    var slideSensitivityState = (settings?.slideSensitivity ?: DEFAULT_SLIDE_SENSITIVITY).toFloat()
    var slideCursorMovementModeState =
        CursorAccelerationMode.entries[settings?.slideCursorMovementMode ?: DEFAULT_SLIDE_CURSOR_MOVEMENT_MODE]
    var slideEnabledState = (settings?.slideEnabled ?: DEFAULT_SLIDE_ENABLED).toBool()
    var slideSpacebarDeadzoneEnabledState =
        (settings?.slideSpacebarDeadzoneEnabled ?: DEFAULT_SLIDE_SPACEBAR_DEADZONE_ENABLED).toBool()
    var slideBackspaceDeadzoneEnabledState =
        (settings?.slideBackspaceDeadzoneEnabled ?: DEFAULT_SLIDE_BACKSPACE_DEADZONE_ENABLED).toBool()
    var autoCapitalizeState = (settings?.autoCapitalize ?: DEFAULT_AUTO_CAPITALIZE).toBool()
    var spacebarMultiTapsState = (settings?.spacebarMultiTaps ?: DEFAULT_SPACEBAR_MULTITAPS).toBool()
    var switchToLettersAfterSpaceState =
        (settings?.switchToLettersAfterSpace ?: DEFAULT_SWITCH_TO_LETTERS_AFTER_SPACE).toBool()
    var dragReturnEnabledState = (settings?.dragReturnEnabled ?: DEFAULT_DRAG_RETURN_ENABLED).toBool()
    var circularDragEnabledState = (settings?.circularDragEnabled ?: DEFAULT_CIRCULAR_DRAG_ENABLED).toBool()
    var clockwiseDragActionState =
        CircularDragAction.entries[settings?.clockwiseDragAction ?: DEFAULT_CLOCKWISE_DRAG_ACTION]
    var counterclockwiseDragActionState =
        CircularDragAction.entries[settings?.counterclockwiseDragAction ?: DEFAULT_COUNTERCLOCKWISE_DRAG_ACTION]
    var ghostKeysEnabledState = (settings?.ghostKeysEnabled ?: DEFAULT_GHOST_KEYS_ENABLED).toBool()
    var slideHoldEnabledState = (settings?.slideHoldEnabled ?: DEFAULT_SLIDE_HOLD_ENABLED).toBool()
    var switchImeModeState = ImeSwitchMode.entries[settings?.switchImeMode ?: DEFAULT_SWITCH_IME_MODE]

    fun toUpdate(): BehaviorUpdate =
        BehaviorUpdate(
            id = 1,
            minSwipeLength = minSwipeLengthState.toInt(),
            slideSensitivity = slideSensitivityState.toInt(),
            slideEnabled = slideEnabledState.toInt(),
            slideCursorMovementMode = slideCursorMovementModeState.ordinal,
            slideSpacebarDeadzoneEnabled = slideSpacebarDeadzoneEnabledState.toInt(),
            slideBackspaceDeadzoneEnabled = slideBackspaceDeadzoneEnabledState.toInt(),
            autoCapitalize = autoCapitalizeState.toInt(),
            spacebarMultiTaps = spacebarMultiTapsState.toInt(),
            switchToLettersAfterSpace = switchToLettersAfterSpaceState.toInt(),
            dragReturnEnabled = dragReturnEnabledState.toInt(),
            circularDragEnabled = circularDragEnabledState.toInt(),
            clockwiseDragAction = clockwiseDragActionState.ordinal,
            counterclockwiseDragAction = counterclockwiseDragActionState.ordinal,
            ghostKeysEnabled = ghostKeysEnabledState.toInt(),
            slideHoldEnabled = slideHoldEnabledState.toInt(),
            switchImeMode = switchImeModeState.ordinal,
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BehaviorScreen(
    navController: NavController,
    appSettingsViewModel: AppSettingsViewModel,
) {
    Log.d(TAG, "Got to behavior activity")

    val settings by appSettingsViewModel.appSettings.observeAsState()
    // Rebuilt on every recomposition, like the plain local vars it replaces
    val form = BehaviorForm(settings)
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val resources = LocalResources.current

    fun updateBehavior() = appSettingsViewModel.updateBehavior(form.toUpdate())

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopAppBar(
                text = stringResource(R.string.behavior),
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
                    BehaviorScreenItems(form, resources, ::updateBehavior)
                    TestOutTextField()
                }
            }
        },
    )
}

@Composable
private fun BehaviorScreenItems(
    form: BehaviorForm,
    resources: Resources,
    onChange: () -> Unit,
) {
    BehaviorGeneralItems(form, onChange)
    SettingsDivider()
    BehaviorSlideItems(form, onChange)
    BehaviorSlideHoldItem(form, onChange)
    SettingsDivider()
    BehaviorDragItems(form, onChange)
    BehaviorAdvancedItems(form, resources, onChange)
}

@Composable
private fun BehaviorAdvancedItems(
    form: BehaviorForm,
    resources: Resources,
    onChange: () -> Unit,
) {
    SwitchPreference(
        value = form.ghostKeysEnabledState,
        onValueChange = {
            form.ghostKeysEnabledState = it
            onChange()
        },
        title = { Text(stringResource(R.string.ghost_keys_enable)) },
        summary = { Text(stringResource(R.string.ghost_keys_description)) },
        icon = { Icon(imageVector = Icons.Outlined.BorderInner, contentDescription = stringResource(R.string.ghost_keys_enable)) },
    )
    SettingsDivider()
    ImeSwitchModeItem(form, resources, onChange)
    SettingsDivider()
}

@Composable
private fun ImeSwitchModeItem(
    form: BehaviorForm,
    resources: Resources,
    onChange: () -> Unit,
) {
    ListPreference(
        type = ListPreferenceType.DROPDOWN_MENU,
        value = form.switchImeModeState,
        onValueChange = {
            form.switchImeModeState = it
            onChange()
        },
        values = ImeSwitchMode.entries,
        valueToText = { AnnotatedString(resources.getString(it.resId)) },
        title = { Text(stringResource(R.string.ime_switch_mode)) },
        summary = { Text(stringResource(form.switchImeModeState.resId)) },
        icon = { Icon(imageVector = Icons.Outlined.Keyboard, contentDescription = null) },
    )
}
